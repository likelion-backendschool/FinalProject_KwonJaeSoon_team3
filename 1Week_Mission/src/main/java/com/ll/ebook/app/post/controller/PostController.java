package com.ll.ebook.app.post.controller;

import com.ll.ebook.app.post.entity.Post;
import com.ll.ebook.app.post.form.PostForm;
import com.ll.ebook.app.post.service.PostService;
import com.ll.ebook.app.security.dto.MemberContext;
import com.ll.ebook.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @GetMapping("/list")
    public String showList(Model model) {
        List<Post> posts = postService.getPosts();

        model.addAttribute("posts", posts);

        return "post/list";
    }

    @GetMapping("/{id}")
    public String showDetail(Model model, @PathVariable Long id) {
        Post post = postService.getForPrintPostById(id);

        model.addAttribute("post", post);

        return "post/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String showWrite() {
        return "post/write";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write(@AuthenticationPrincipal MemberContext context, @Valid PostForm postForm) {
        Post post = postService.write(context.getId(), postForm.getSubject(), postForm.getContent(), postForm.getContentHtml(), postForm.getKeywords());

        String msg = "%d번 게시물이 작성되었습니다.".formatted(post.getId());
        msg = Util.url.encode(msg);
        return "redirect:/post/%d?msg=%s".formatted(post.getId(), msg);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String showModify(@AuthenticationPrincipal MemberContext context, Model model, @PathVariable Long id) {
        Post post =  postService.getForPrintPostById(id);

        if (context.memberIsNot(post.getAuthorId())) {
            String msg = Util.url.encode("%d번 게시물을 수정할 수 없습니다.".formatted(id));
            return "redirect:/post/%d?msg=%s".formatted(id, msg);
        }

        model.addAttribute("post", post);

        return "post/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/modify")
    public String modify(@AuthenticationPrincipal MemberContext context, @PathVariable Long id, @Valid PostForm postForm) {
        Post post = postService.getForPrintPostById(id);

        if (context.memberIsNot(post.getAuthorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        postService.modify(post, postForm.getSubject(), postForm.getContent(), postForm.getContentHtml(), postForm.getKeywords());

        String msg = Util.url.encode("%d번 게시물이 수정되었습니다.".formatted(id));
        return "redirect:/post/%d?msg=%s".formatted(id, msg);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal MemberContext context, @PathVariable Long id) {
        Post post = postService.getForPrintPostById(id);

        if(context.memberIsNot(post.getAuthorId())) {
            String msg = Util.url.encode("%d번 게시물을 삭제할 수 없습니다.".formatted(id));
            return "redirect:/post/%d?msg=%s".formatted(id, msg);
        }
        postService.delete(post);

        String msg = Util.url.encode("%d번 게시물이 삭제되었습니다.".formatted(id));
        return "redirect:/post/list?msg=%s".formatted(msg);
    }

}
