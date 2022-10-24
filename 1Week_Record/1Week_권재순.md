## Title: [1Week] 권재순

### 미션 요구사항 분석 & 체크리스트

- [x]  회원가입
- [x]  회원 가입 후 이메일 발송
- [x]  로그인
- [x]  로그아웃
- [x]  회원 정보 수정
- [x]  아이디 찾기
- [x]  비밀번호 찾기
- [x]  글 작성
- [x]  글 수정
- [x]  글 리스트
- [x]  글 삭제

### 1주차 미션 요약

---

**[접근 방법]**

**전체적으로 기존 수업의 내용을 참고하여 진행했습니다.**

**프로젝트 진행 순서**

- 프로젝트를 진행하기 위해 **우선 순서를 부여**했습니다.
- 필수 과제를 가장 우선으로 진행했습니다.
- 필수 과제가 아니더라도 비슷한 기능의 추가 과제는 함께 진행했습니다.
- **`Member` → `Post` → `Product`** 순으로 진행했습니다.

1. **회원가입 및 회원가입 이메일 발송**
- `validation`을 이용한 공백 확인
- 중복된 username을 사용한 회원 가입 방지

    ```java
    Member oldMember = memberService.findMemberByUsername(joinForm.getUsername());
    
    if(oldMember != null) {
        return "redirect:/?errorMsg=Already Join";
    }
    ```

- 필명의 유무를 통한 `authLevel`변경

    ```java
    int authLevel = 3;
    
    if(nickname != null) {
        authLevel = 7;
    }
    ```

- `SMTP`를 사용한 이메일 전송
    - **참고문서** : [https://victorydntmd.tistory.com/m/342](https://victorydntmd.tistory.com/m/342)
    - 회원가입 된 Email로 전송
    - 가입된 Member의 username을 사용해 메시지 전송

    ```java
    String title = "%s님의 회원가입 축하메시지".formatted(joinForm.getUsername());
    String msg = "$s님의 회원가입을 축하합니다!!!!!".formatted(joinForm.getUsername());
    
    contactService.sendSimpleMessage(member, title, msg);
    ```


- **결과**



1. **로그인, 로그아웃**
- `**Spring Security**`를 활용

    ```java
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(
                        authorizeRequests -> authorizeRequests
                                .antMatchers("/**")
                                .permitAll()
                )
                .formLogin(
                        formLogin -> formLogin
                                .loginPage("/member/login") // GET
                                .loginProcessingUrl("/member/login") // POST
                )
                .logout(
                        logout -> logout
    														.logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                                .logoutSuccessUrl("/?msg=Logout!!! ")
                                .invalidateHttpSession(true)
                );
    
        return http.build();
    }
    ```


1. **회원 정보 수정**
- `@AuthenticationPrincipal`을 사용해 현재 세션에 로그인 되어있는 멤버 확인
- `model.addAttribute`로 로그인 되어있는 Member 객체를 전달 → 전달된 객체의 정보 출력

    ```java
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String showModify(@AuthenticationPrincipal MemberContext context, Model model) {
        Member loginedMember = memberService.findMemberByUsername(context.getUsername());
    
        model.addAttribute("loginedMember", loginedMember);
    
        return "member/modify";
    }
    ```

- 폼에 입력된 값을 POST method로 받아와 수정

    ```java
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modify(@AuthenticationPrincipal MemberContext context, String nickname, String email) {
        Member member = memberService.findMemberByUsername(context.getUsername());
    
        memberService.modify(member, nickname, email);
    
        return "redirect:/member/profile";
    }
    ```

    ```java
    public void modify(Member member, String nickname, String email) {
        member.setNickname(nickname);
        member.setEmail(email);
    
        memberRepository.save(member);
    }
    ```

    - **비밀번호 변경**
        - 현재 비밀번호, 변경할 비밀번호를 입력
        - 현재 비밀번호가 다를 경우 초기화(에러 메시지 출력)

        ```java
        @PreAuthorize("isAuthenticated()")
        @PostMapping("/modifyPassword")
        public String modifyPassword(@AuthenticationPrincipal MemberContext context, String password, String modifyPassword) {
            Member loginedMember = memberService.findMemberByUsername(context.getUsername());
        
            if(!passwordEncoder.matches(password, loginedMember.getPassword())) {
                String msg = Util.url.encode("현재 비밀번호가 틀립니다.");
                return "redirect:/member/modifyPassword?msg=%s".formatted(msg);
            }
        
            memberService.modifyPassword(loginedMember, modifyPassword);
        
            return "redirect:/member/profile";
        }
        
        ```

        ```java
        public void modifyPassword(Member member, String newPassword) {
                String encodePassword = passwordEncoder.encode(newPassword);
                member.setPassword(encodePassword);
        
                memberRepository.save(member);
            }
        ```


1. **아이디 / 비밀번호 찾기**
- **아이디 찾기**
    - 가입할 때 등록한 Email을 활용
    - JPA의 `findByEmail`을 통해 Member객체를 가져옴

    ```java
    Member member = memberService.findMemberByEmail(email);
    ```

    - `model.addAttribute`로 Email로 찾은 Member 객체를 전달 → 전달된 객체의 정보 출력

- **비밀번호 찾기**
    - 가입할 때 등록한 `username`, `email` 활용

        ```java
        Member member = memberService.findMemberByUsernameAndEmail(username, email);
        ```

    - 입력한 `username`과 `email`이 등록되어 있지 않을 경우 오류 메시지 출력

        ```java
        if(member == null) {
            String msg = Util.url.encode("가입된 아이디가 없습니다.");
            return "redirect:/member/findPassword?msg=%s".formatted(msg);
        }
        ```

    - 새로운 패스워드를 랜덤한 문자열 10개를 전역변수로 설정

        ```java
        String newPassword = "";
        for(int i = 0; i < 10; i++) {
            char ran = (char)((int)(Math.random() * 25) + 97);
            newPassword += ran;
        }
        ```

    - 전역변수로 설정한 새로운 패스워드를 DB에 등록하고, 등록한 Email로 전송

        ```java
        memberService.setNewPassword(member, newPassword);
        contactService.sendSimpleMessage(member, newPassword);
        ```


1. **글 작성 및 수정**
- 작성
    - `validation`을 이용한 공백 확인
    - 작성된 form을 사용해 게시물 작성
    - `Toast Editor`를 활용해 마크다운 구현
    - 해시태그의 **“#”을 기준**으로 나누어 Keyword로 넣어준다.
    - 해시태그의 `Post`, `Keyword`, `Member`를 통해 어떤 Member의 어떤 Post에는 어떤 Keyword가 있는지 구분하여 저장

- 수정
    - 글을 등록한 멤버가 아닐 경우 글 수정 불가

      → 파라미터로 받은 id를 통해 Post 객체를 가져온다.

      → 만약 Post 객체의 MemberId와 MemberContext의 id가 다를 경우 수정 불가


    ```java
    if (context.memberIsNot(post.getAuthorId())) {
        String msg = Util.url.encode("%d번 게시물을 수정할 수 없습니다.".formatted(id));
        return "redirect:/post/%d?msg=%s".formatted(id, msg);
    }
    ```


1. **글 리스트**
- List를 활용해 DB에 저장되어 있는 Post객체를 불러온다.
- 모든 Post 객체를 thymeleaf의 `each`를 활용해 하나하나 출력시켜준다.
    - 최대 100개까지 출력이 가능한 부분

    ```java
    if (posts.size() > 100) {
        posts = posts.subList(0, 101);
    }
    ```

- memberId, keyword 파라미터를 전송하여 작성자가 달아 놓은 똑같은 해시태그를 QueryDSL을 통해 List로 모아 출력시켜준다. → **요구사항에 맞춰 리팩토링 예정**

    ```java
    @GetMapping("/list")
    public String showList(@RequestParam(value = "memberId", defaultValue = "0") Long memberId, @RequestParam(value = "kw", defaultValue = "") String keyword, Model model) {
        if (memberId == 0 || keyword.equals("")) {
            List<Post> posts = postService.getPosts();
            model.addAttribute("posts", posts);
    
            return "post/list";
        }
    
        List<Post> posts = postService.getPostsBymemberIdAndKeyword(memberId, keyword);
    
        model.addAttribute("posts", posts);
    
        return "post/list";
    }
    ```


1. **글 삭제**
- 파라미터로 불러온 id값으로 Post객체를 찾는다.
- 찾은 Post객체의 `MemberId`와 `MemberContext`의 id가 다를 경우 삭제 불가능(에러 메시지 출력)

    ```java
    if(context.memberIsNot(post.getAuthorId())) {
        String msg = Util.url.encode("%d번 게시물을 삭제할 수 없습니다.".formatted(id));
        return "redirect:/post/%d?msg=%s".formatted(id, msg);
    }
    ```

- 글 삭제 시 FK로 묶여있는 모든 엔티티를 `CASCADE`로 지정하여 연쇄적으로 삭제

    ```java
    @ManyToOne
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member memberId;
    @ManyToOne
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post postId;
    @ManyToOne
    @ToString.Exclude
    private PostKeyword postKeywordId;
    ```


**[특이사항]**

학교 시험과 함께 진행하여 프로젝트 진행을 집중하지 못한 아쉬움이 있습니다.

최대한의 시간을 활용해 코드를 구현해 보았지만, 여전히 많이 부족함이 보였습니다.

시험 종료 후 현재 발생하는 오류의 예외처리, 강사님 코드에 의존하지 않고 스스로 코드 작성하기 등 리팩토링을 거칠 예정입니다.

구현 과정에서 아쉬웠던 점 / 궁금했던 점을 정리합니다.

- 추후 리팩토링 시, 어떤 부분을 추가적으로 진행하고 싶은지에 대해 구체적으로 작성해주시기 바랍니다.

**[Refactoring]**

- 전체적으로 강사님의 코드에 의존하여 강사님의 코드를 참고한 나만의 코드를 직접 개발해 볼 예정입니다.
- 마크다운 원문의 메시지를 저장하지 못한 부분을 수정할 예정입니다.
- 각 부분마다 에러 메시지의 출력하는 방법이 달라 확인 후 모두 동일하게 수정할 예정입니다.
- 급하게 짠 코드이기에 무분별한 에러 발생과, SQL 쿼리가 실행됩니다. 에러 발생과 SQL 쿼리를 최대한 줄이기 위해 수정할 예정입니다.