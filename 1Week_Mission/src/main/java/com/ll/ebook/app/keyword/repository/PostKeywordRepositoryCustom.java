package com.ll.ebook.app.keyword.repository;

import com.ll.ebook.app.keyword.entity.PostKeyword;

import java.util.List;

public interface PostKeywordRepositoryCustom {
    List<PostKeyword> getQslAllByAuthorId(Long authorId);
}
