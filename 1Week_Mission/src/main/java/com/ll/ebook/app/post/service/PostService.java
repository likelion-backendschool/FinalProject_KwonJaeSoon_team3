package com.ll.ebook.app.post.service;

import com.ll.ebook.app.hashTag.entity.PostHashTag;
import com.ll.ebook.app.hashTag.service.PostHashTagService;
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
    private final PostHashTagService postHashTagService;

    public List<Post> getPosts() {
        return postRepository.getPosts();
    }

    public Post write(Long authorId, String subject, String content, String contentHtml) {
        return write(new Member(authorId), subject, content, contentHtml);
    }

    public Post write(Long authorId, String subject, String content, String contentHtml, String hashTagContents) {
        return write(new Member(authorId), subject, content, contentHtml, hashTagContents);
    }

    public Post write(Member member, String subject, String content, String contentHtml) {
        return write(member, subject, content, contentHtml, "");
    }

    public Post write(Member member, String subject, String content, String contentHtml, String hashTagContents) {
        Post post = Post
                .builder()
                .member(member)
                .subject(subject)
                .content(content)
                .contentHtml(contentHtml)
                .build();

        postRepository.save(post);

        postHashTagService.applyHashTags(post, hashTagContents);

        return post;
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public Post getForPrintPostById(Long id) {
        Post post = getPostById(id);

        loadForPrintData(post);

        return post;
    }

    public void loadForPrintData(Post post) {
        List<PostHashTag> hashTags = postHashTagService.getHashTags(post);

        post.getExtra().put("hashTags", hashTags);
    }

    public void modify(Post post, String subject, String content, String contentHtml, String keywords) {
        post.setSubject(subject);
        post.setContent(content);
        post.setContentHtml(contentHtml);
        postRepository.save(post);

        postHashTagService.applyHashTags(post, keywords);
    }

    public Post findById(Long id) {
        return postRepository.findById(id).get();
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    public List<Post> getPostsByMemberIdAndKeyword(Long memberId, String keyword) {
        return postRepository.getPostsByMemberIdAndKeyword(memberId, keyword);
    }

    public boolean actorCanSee(Member actor, Post post) {
        if ( actor == null ) return false;
        if ( post == null ) return false;

        return post.getMember().getId().equals(actor.getId());
    }
}
