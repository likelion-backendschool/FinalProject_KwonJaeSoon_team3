package com.ll.ebook.app.post.repository;

import com.ll.ebook.app.post.entity.Post;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> getPosts();

    List<Post> getPostsBymemberIdAndKeyword(Long memberId, String keyword);
}
