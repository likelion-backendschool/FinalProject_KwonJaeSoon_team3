## Title: [2Week] 권재순

### 미션 요구사항 분석 & 체크리스트

- [x]  장바구니
- [x]  주문
- [x]  결제
- [x]  PG연동
- [ ]  환불

### N주차 미션 요약

---

**[접근 방법]**

**전체적으로 기존 수업의 내용을 참고하여 진행했습니다.**

**프로젝트 진행 순서**

- 프로젝트를 진행하기 위해 **우선 순서를 부여**했습니다.
- 필수 과제를 가장 우선으로 진행했습니다.
- 요구사항에 집중적으로 맞춰 진행했습니다.
- `**장바구니` → `주문` → `결제` → `PG연동`** 순으로 진행했습니다.

1. **도서를 장바구니에 담기**
- **`productId`**를 사용해 물건을 조회함

    ```java
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add/{productId}")
    public String addItem(@AuthenticationPrincipal MemberContext memberContext, @PathVariable Long productId) {
        Optional<Product> product = productService.findById(productId);
    
        if(product.isEmpty()) {
            throw new EmptyProductException();
        }
    
        cartItemService.addItem(memberContext.getMember(), product.get());
    
        return "redirect:/product/list";
    }
    ```

- 현재 로그인된 회원의 장바구니에 조회한 물건 추가

    ```java
    public CartItem addItem(Member member, Product product) {
        CartItem cartItem = CartItem.builder()
                .member(member)
                .product(product)
                .build();
    
        cartItemRepository.save(cartItem);
    
    	return cartItem;
    }
    ```


1. **장바구니 제거**
- 장바구니 담기와 같이 **`productId`**를 활용해 물건 조회
- 장바구니에 물건을 넣은 사람과 현재 로그인 되어있는 멤버를 비교하여 삭제 여부 판단
- 맞으면 삭제, 맞지 않으면 에러 발생

    ```java
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/remove/{productId}")
    public String removeItem(@AuthenticationPrincipal MemberContext memberContext, @PathVariable Long productId) {
        Product product = productService.findById(productId).get();
        Member member = memberContext.getMember();
    
        if(cartItemService.memberCanRemove(member, product) == false) {
            throw new ActorCanNotRemoveException();
        }
    
        cartItemService.remove(member, product);
    
        return Rq.redirectWithMsg("/cart/list", "%d번 상품이 삭제되었습니다.".formatted(product.getId()));
    }
    ```

- 장바구니 아이템을 삭제할 수 있는 버튼 생성

    ```html
    <a onclick="deleteItemForm__Submit();" sec:authorize="isAuthenticated()">
        <span>삭제하기</span>
    </a>
    <form id="deleteItemForm" th:action="@{|/cart/remove/${cartItem.id}|}" method="post" hidden></form>
    ```


1. **장바구니 주문**
- 현재 로그인 되어있는 회원을 조회
- 조회한 회원의 장바구니에 있는 아이템을 주문

    ```java
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String addItem(@AuthenticationPrincipal MemberContext memberContext) {
        Member member = memberContext.getMember();
    
        orderService.createFromCart(member);
    
        String msg = Util.url.encode("주문이 완료되었습니다!");
        return "redirect:/order/list?msg=%s".formatted(msg);
    }
    ```

- 장바구니에 있는 아이템은 주문 목록에 추가 후 삭제

    ```java
    public Order createFromCart(Member member) {
        List<CartItem> cartItems = cartItemService.getItemsByMember(member);
    
        List<OrderItem> orderItems = new ArrayList<>();
    
        for (CartItem cartItem : cartItems) {
            orderItems.add(new OrderItem(cartItem.getProduct()));
    
            cartItemService.deleteItem(cartItem);
        }
    
        return create(member, orderItems);
    }
    ```

- 주문버튼 생성

    ```html
    <a onclick="OrderItemForm__Submit();" class="btn btn-sm btn-secondary" sec:authorize="isAuthenticated()">
        <span>주문하기</span>
    </a>
    <form id="orderItemForm" th:action="@{/order/create}" method="post" hidden></form>
    ```


1. **주문 세부정보**
- 주문의 세부정보 페이지에서 결재도 가능하도록

1. **예치금 시스템**
- Member에 예치금을 충전할 수 있도록
- Entity를 추가하여 DB에 Member 테이블에 저장

    ```java
    public long addCash(Member member, long price, String eventType) {
        CashLog cashLog = cashLogService.addCash(member, price, eventType);
    
        long newRestCash = member.getRestCash() + cashLog.getPrice();
        member.setRestCash(newRestCash);
        memberRepository.save(member);
    
        return newRestCash;
    }
    ```

- CashLog를 통해 돈에 내한 내역을 한번에 저장

    ```java
    public CashLog addCash(Member member, long price, String eventType) {
        CashLog cashLog = CashLog.builder()
                .member(member)
                .price(price)
                .eventType(eventType)
                .build();
    
        cashLogRepository.save(cashLog);
    
        return cashLog;
    }
    ```


1. TossPayment 연동
- 전체 금액 결재, 예치금 사용 후 남은 금액 결재 등
- 강사님 코드 사용

1. 결재
- 예치금 결제, 예치금 사용 후 카드결제, 카드결제

**[특이사항]**

현재 졸업작품 전시회가 얼마남지 않는 시간이라 시간을 많이 투자하지 못했습니다.

기능은 작동하지만 아직 마음에 들지 않는 코드들이 많습니다.

시험 종료 후 현재 발생하는 오류의 예외처리, 강사님 코드에 의존하지 않고 스스로 코드 작성하기 등 리팩토링을 거칠 예정입니다.

**[Refactoring]**

- 전체적으로 강사님의 코드에 의존하여 강사님의 코드를 참고한 나만의 코드를 직접 개발해 볼 예정입니다.