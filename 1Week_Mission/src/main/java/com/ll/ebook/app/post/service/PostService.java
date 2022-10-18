package com.ll.ebook.app.post.service;

import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.post.entity.Post;
import com.ll.ebook.app.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public List<Post> getPosts() {
        return postRepository.getPosts();
    }

    public Post write(Member member, String subject, String content, String contentHtml, String hashTag) {
        Post post = Post
                .builder()
                .authorId(member)
                .subject(subject)
                .content(content)
                .contentHtml(contentHtml)
                .build();

        postRepository.save(post);

        return post;
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }
}
