package com.ll.ebook.app.home.controller;

import com.ll.ebook.app.post.entity.Post;
import com.ll.ebook.app.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final PostService postService;

    @GetMapping("/")
    public String showMain(Model model) {
        List<Post> posts = postService.getPosts();

        if (posts.size() > 100) {
            posts = posts.subList(0, 101);
        }

        model.addAttribute("posts", posts);

        return "home/main";
    }
}