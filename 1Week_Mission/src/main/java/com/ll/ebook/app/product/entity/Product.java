package com.ll.ebook.app.product.entity;

import com.ll.ebook.app.base.entity.BaseEntity;
import com.ll.ebook.app.hashTag.entity.ProductHashTag;
import com.ll.ebook.app.keyword.entity.PostKeyword;
import com.ll.ebook.app.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.LAZY;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Product extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    @ManyToOne(fetch = LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PostKeyword postKeyword;
    private String subject;
    private int price;

    public Product(long id) {
        super(id);
    }

    public String getExtra_productTagLinks() {
        Map<String, Object> extra = getExtra();

        if (extra.containsKey("productTags") == false) {
            return "";
        }

        List<ProductHashTag> productTags = (List<ProductHashTag>) extra.get("productTags");

        if (productTags.isEmpty()) {
            return "";
        }

        return productTags
                .stream()
                .map(productTag -> {
                    String text = "#" + productTag.getProductKeyword().getContent();

                    return """
                            <a href="%s" class="text-link">%s</a>
                            """
                            .stripIndent()
                            .formatted(productTag.getProductKeyword().getListUrl(), text);
                })
                .sorted()
                .collect(Collectors.joining(" "));
    }

    public String getExtra_inputValue_hashTagContents() {
        Map<String, Object> extra = getExtra();

        if (extra.containsKey("productTags") == false) {
            return "";
        }

        List<ProductHashTag> productTags = (List<ProductHashTag>) extra.get("productTags");

        if (productTags.isEmpty()) {
            return "";
        }

        return productTags
                .stream()
                .map(productTag -> "#" + productTag.getProductKeyword().getContent())
                .sorted()
                .collect(Collectors.joining(" "));
    }

    public int getSalePrice() {
        return getPrice();
    }

    public String getJdenticon() {
        return "product__" + getId();
    }

}
