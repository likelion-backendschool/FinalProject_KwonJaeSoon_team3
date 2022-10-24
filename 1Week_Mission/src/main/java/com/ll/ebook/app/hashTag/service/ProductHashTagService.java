package com.ll.ebook.app.hashTag.service;

import com.ll.ebook.app.hashTag.entity.PostHashTag;
import com.ll.ebook.app.hashTag.entity.ProductHashTag;
import com.ll.ebook.app.hashTag.repository.ProductHashTagRepository;
import com.ll.ebook.app.keyword.entity.ProductKeyword;
import com.ll.ebook.app.keyword.service.ProductKeywordService;
import com.ll.ebook.app.post.entity.Post;
import com.ll.ebook.app.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductHashTagService {
    private final ProductKeywordService productKeywordService;
    private final ProductHashTagRepository productTagRepository;

    public void applyProductTags(Product product, String productTagContents) {
        List<ProductHashTag> oldProductTags = getProductTags(product);

        List<String> productKeywordContents = Arrays.stream(productTagContents.split("#"))
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .collect(Collectors.toList());

        List<ProductHashTag> needToDelete = new ArrayList<>();

        for (ProductHashTag oldProductTag : oldProductTags) {
            boolean contains = productKeywordContents.stream().anyMatch(s -> s.equals(oldProductTag.getProductKeyword().getContent()));

            if (contains == false) {
                needToDelete.add(oldProductTag);
            }
        }

        needToDelete.forEach(productTag -> productTagRepository.delete(productTag));

        productKeywordContents.forEach(productKeywordContent -> {
            saveProductHashTag(product, productKeywordContent);
        });
    }

    public List<ProductHashTag> getProductTags(Product product) {
        return productTagRepository.findAllByProductId(product.getId());
    }

    public List<ProductHashTag> getProductTags(String productKeywordContent) {
        return productTagRepository.findAllByProductKeyword_contentOrderByProduct_idDesc(productKeywordContent);
    }

    private ProductHashTag saveProductHashTag(Product product, String productKeywordContent) {
        ProductKeyword productKeyword = productKeywordService.save(productKeywordContent);

        Optional<ProductHashTag> opProductTag = productTagRepository.findByProductIdAndProductKeywordId(product.getId(), productKeyword.getId());

        if (opProductTag.isPresent()) {
            return opProductTag.get();
        }

        ProductHashTag productTag = ProductHashTag.builder()
                .product(product)
                .member(product.getMember())
                .productKeyword(productKeyword)
                .build();

        productTagRepository.save(productTag);

        return productTag;
    }

    public List<ProductHashTag> getProductTagsByProductIdIn(long[] ids) {
        return productTagRepository.findAllByProductIdIn(ids);
    }
}