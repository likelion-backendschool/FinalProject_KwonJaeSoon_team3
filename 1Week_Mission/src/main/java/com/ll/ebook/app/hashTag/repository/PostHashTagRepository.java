package com.ll.ebook.app.hashTag.repository;

import com.ll.ebook.app.hashTag.entity.PostHashTag;
import com.ll.ebook.app.hashTag.entity.ProductHashTag;
import com.ll.ebook.app.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostHashTagRepository extends JpaRepository<PostHashTag, Long> {
    Optional<PostHashTag> findByPostIdAndPostKeywordId(Long postId, Long postKeywordId);

    List<PostHashTag> findAllByPostId(Long postId);

    List<PostHashTag> findAllByMemberIdAndPostKeywordIdOrderByPost_idDesc(Long memberId, Long keywordId);
}
