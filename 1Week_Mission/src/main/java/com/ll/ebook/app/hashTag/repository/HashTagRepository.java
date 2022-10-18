package com.ll.ebook.app.hashTag.repository;

import com.ll.ebook.app.hashTag.entity.PostHashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HashTagRepository extends JpaRepository<PostHashTag, Long> {
    Optional<PostHashTag> findByPostIdIdAndPostKeywordIdId(Long postId, Long postKeywordId);

    List<PostHashTag> findAllByPostIdId(Long postId);
}
