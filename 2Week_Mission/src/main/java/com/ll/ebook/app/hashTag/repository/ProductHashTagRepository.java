package com.ll.ebook.app.hashTag.repository;

import com.ll.ebook.app.hashTag.entity.ProductHashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductHashTagRepository extends JpaRepository<ProductHashTag, Long> {

    Optional<ProductHashTag> findByProductIdAndProductKeywordId(Long id, Long id1);

    List<ProductHashTag> findAllByProductId(Long id);

    List<ProductHashTag> findAllByProductIdIn(long[] ids);

    List<ProductHashTag> findAllByProductKeyword_contentOrderByProduct_idDesc(String productKeywordContent);
}
