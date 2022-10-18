package com.ll.ebook.app.keyword.repository;

import com.ll.ebook.app.keyword.entity.PostKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeywordRepository extends JpaRepository<PostKeyword, Long> {
    Optional<PostKeyword> findByContent(String keywordContent);
}
