package com.ll.ebook.app.product.service;

import com.ll.ebook.app.hashTag.entity.PostHashTag;
import com.ll.ebook.app.hashTag.entity.ProductHashTag;
import com.ll.ebook.app.hashTag.service.PostHashTagService;
import com.ll.ebook.app.hashTag.service.ProductHashTagService;
import com.ll.ebook.app.keyword.entity.PostKeyword;
import com.ll.ebook.app.keyword.service.PostKeywordService;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.post.entity.Post;
import com.ll.ebook.app.product.entity.Product;
import com.ll.ebook.app.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final PostKeywordService postKeywordService;
    private final ProductRepository productRepository;
    private final ProductHashTagService productHashTagService;
    private final PostHashTagService postHashTagService;

    public Product create(Member author, String subject, int price, Long postKeywordId, String productTagContents) {
        PostKeyword postKeyword = postKeywordService.findById(postKeywordId).get();

        return create(author, subject, price, postKeyword, productTagContents);
    }

    public Product create(Member author, String subject, int price, PostKeyword postKeyword, String productTagContents) {
        Product product = Product
                .builder()
                .member(author)
                .postKeyword(postKeyword)
                .subject(subject)
                .price(price)
                .build();

        productRepository.save(product);

        applyProductTags(product, productTagContents);

        return product;
    }

    public void applyProductTags(Product product, String productTagContents) {
        productHashTagService.applyProductTags(product, productTagContents);
    }

    public Optional<Product> findForPrintById(Long id) {
        Optional<Product> opProduct = findById(id);

        if (opProduct.isEmpty()) return opProduct;

        List<ProductHashTag> productTags = getProductHashTags(opProduct.get());

        opProduct.get().getExtra().put("productTags", productTags);

        return opProduct;
    }

    public Optional<Product> findById(long id) {
        return productRepository.findById(id);
    }

    private List<ProductHashTag> getProductHashTags(Product product) {
        return productHashTagService.getProductTags(product);
    }

    public List<Post> findPostsByProduct(Product product) {
        Member author = product.getMember();
        PostKeyword postKeyword = product.getPostKeyword();
        List<PostHashTag> postTags = postHashTagService.getPostTags(author.getId(), postKeyword.getId());

        return postTags
                .stream()
                .map(PostHashTag::getPost)
                .collect(Collectors.toList());
    }

    public boolean actorCanModify(Member author, Post post) {
        return author.getId().equals(post.getMember().getId());
    }

    public boolean actorCanModify(Member actor, Product product) {
        if (actor == null) return false;

        return actor.getId().equals(product.getMember().getId());
    }

    public boolean actorCanRemove(Member author, Post post) {
        return actorCanModify(author, post);
    }

    public boolean actorCanRemove(Member actor, Product product) {
        return actorCanModify(actor, product);
    }

    public List<Product> findAllForPrintByOrderByIdDesc(Member actor) {
        List<Product> products = findAllByOrderByIdDesc();

        loadForPrintData(products, actor);

        return products;
    }

    private void loadForPrintData(List<Product> products, Member actor) {
        long[] ids = products
                .stream()
                .mapToLong(Product::getId)
                .toArray();

        List<ProductHashTag> productTagsByProductIds = productHashTagService.getProductTagsByProductIdIn(ids);

        Map<Long, List<ProductHashTag>> productTagsByProductIdMap = productTagsByProductIds.stream()
                .collect(groupingBy(
                        productTag -> productTag.getProduct().getId(), toList()
                ));

        products.stream().forEach(product -> {
            List<ProductHashTag> productTags = productTagsByProductIdMap.get(product.getId());

            if (productTags == null || productTags.size() == 0) return;

            product.getExtra().put("productTags", productTags);
        });
    }

    private List<Product> findAllByOrderByIdDesc() {
        return productRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public void modify(Product product, String subject, int price, String productTagContents) {
        product.setSubject(subject);
        product.setPrice(price);

        applyProductTags(product, productTagContents);
    }

    @Transactional
    public void remove(Product product) {
        productRepository.delete(product);
    }

    public List<ProductHashTag> getProductTags(String productHashTagContent, Member actor) {
        List<ProductHashTag> productTags = productHashTagService.getProductTags(productHashTagContent);

        loadForPrintDataOnProductTagList(productTags, actor);

        return productTags;
    }

    private void loadForPrintDataOnProductTagList(List<ProductHashTag> productTags, Member actor) {
        List<Product> products = productTags
                .stream()
                .map(ProductHashTag::getProduct)
                .collect(toList());

        loadForPrintData(products, actor);
    }
}
