package com.ll.ebook.app.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.ebook.app.cart.entity.CartItem;
import com.ll.ebook.app.cart.exception.EmptyProductException;
import com.ll.ebook.app.cart.service.CartItemService;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.member.service.MemberService;
import com.ll.ebook.app.order.entity.Order;
import com.ll.ebook.app.order.exception.ActorCanNotPayOrderException;
import com.ll.ebook.app.order.exception.OrderIdNotMatchedException;
import com.ll.ebook.app.order.exception.OrderNotEnoughRestCashException;
import com.ll.ebook.app.order.exception.OrderNotFoundException;
import com.ll.ebook.app.order.service.OrderService;
import com.ll.ebook.app.product.entity.Product;
import com.ll.ebook.app.security.dto.MemberContext;
import com.ll.ebook.util.Rq;
import com.ll.ebook.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String addItem(@AuthenticationPrincipal MemberContext memberContext) {
        Member member = memberContext.getMember();

        orderService.createFromCart(member);

        String msg = Util.url.encode("주문이 완료되었습니다!");
        return "redirect:/order/list?msg=%s".formatted(msg);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showList(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        List<Order> orderList = orderService.findAllByMemberId(memberContext.getMember().getId());

        model.addAttribute("orderList", orderList);

        return "order/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String showDetail(@AuthenticationPrincipal MemberContext memberContext, @PathVariable Long id, Model model) {
        Order order = orderService.findOrderById(id).orElse(null);
        Member member = memberContext.getMember();
        long restCash = memberService.getRestCash(member);

        if(order == null) {
            throw new OrderNotFoundException();
        }

        model.addAttribute("order", order);
        model.addAttribute("restCash", restCash);

        return "order/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/pay")
    public String payOrder(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id) {
        Order order = orderService.findForPrintById(id).get();

        Member member = memberContext.getMember();

        long restCash = memberService.getRestCash(member);

        if (orderService.actorCanPayment(member, order) == false) {
            throw new ActorCanNotPayOrderException();
        }

        orderService.payByRestCashOnly(order);

        String msg = Util.url.encode("카드결제가 완료되었습니다!");
        return "redirect:/order/list?msg=%s".formatted(msg);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/cancel")
    public String cancelOrder(@AuthenticationPrincipal MemberContext memberContext, @PathVariable Long id) {
        Member member = memberContext.getMember();
        Order order = orderService.findOrderById(id).get();

        orderService.delete(member, order);

        String msg = Util.url.encode("주문이 완료되었습니다!");
        return "redirect:/order/list?msg=%s".formatted(msg);
    }


    @PostConstruct
    private void init() {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
            }
        });
    }

    private final String SECRET_KEY = "test_sk_aBX7zk2yd8ygRqp6RQAVx9POLqKQ";

    @RequestMapping("/{id}/success")
    public String confirmPayment(
            @PathVariable long id,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            Model model,
            @AuthenticationPrincipal MemberContext memberContext
    ) throws Exception {
        Order order = orderService.findForPrintById(id).get();

        long orderIdInputed = Long.parseLong(orderId.split("__")[1]);

        if ( id != orderIdInputed ) {
            throw new OrderIdNotMatchedException();
        }

        HttpHeaders headers = new HttpHeaders();
        // headers.setBasicAuth(SECRET_KEY, ""); // spring framework 5.2 이상 버전에서 지원
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));

        Member member = memberContext.getMember();
        long restCash = memberService.getRestCash(member);
        long payPriceRestCash = order.calculatePayPrice() - amount;

        if (payPriceRestCash > restCash) {
            throw new OrderNotEnoughRestCashException();
        }

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            orderService.payByTossPayments(order, payPriceRestCash);

            String msg = Util.url.encode("카드결제를 성공했습니다.");
            return "redirect:/order/list?msg=%s".formatted(msg);
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());
            String msg = Util.url.encode("카드결제를 실패했습니다.");
            return "redirect:/order/%d?msg=%s".formatted(id, msg);
        }
    }

    @RequestMapping("/{id}/fail")
    public String failPayment(@RequestParam String message, @RequestParam String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        String msg = Util.url.encode("카드결제를 실패했습니다.");
        return "redirect:/order/list?msg=%s".formatted(msg);
    }
}
