package com.ll.ebook.app.product.controller;

import com.ll.ebook.app.keyword.entity.PostKeyword;
import com.ll.ebook.app.keyword.service.PostKeywordService;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.post.entity.Post;
import com.ll.ebook.app.product.entity.Product;
import com.ll.ebook.app.product.form.ProductForm;
import com.ll.ebook.app.product.service.ProductService;
import com.ll.ebook.app.security.dto.MemberContext;
import com.ll.ebook.util.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final PostKeywordService postKeywordService;
    private final ProductService productService;

    private final Rq rq;

    @PreAuthorize("isAuthenticated() and hasAuthority('writer')")
    @GetMapping("/create")
    public String showCreate(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        List<PostKeyword> postKeywords = postKeywordService.findByMemberId(memberContext.getId());
        model.addAttribute("postKeywords", postKeywords);
        return "product/create";
    }

    @PreAuthorize("isAuthenticated() and hasAuthority('writer')")
    @PostMapping("/create")
    public String create(@AuthenticationPrincipal MemberContext memberContext, @Valid ProductForm productForm) {
        Member author = memberContext.getMember();
        Product product = productService.create(author, productForm.getSubject(), productForm.getPrice(), productForm.getPostKeywordId(), productForm.getProductTagContents());
        return "redirect:/product/" + product.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = productService.findForPrintById(id).get();
        List<Post> posts = productService.findPostsByProduct(product);

        model.addAttribute("product", product);
        model.addAttribute("posts", posts);

        return "product/detail";
    }

    @GetMapping("/list")
    public String list(Model model) {
        List<Product> products = productService.findAllForPrintByOrderByIdDesc(rq.getMember());

        model.addAttribute("products", products);

        return "product/list";
    }

}
