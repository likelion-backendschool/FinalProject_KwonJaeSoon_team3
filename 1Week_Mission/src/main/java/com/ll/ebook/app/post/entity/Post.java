package com.ll.ebook.app.post.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.ebook.app.base.entity.BaseEntity;
import com.ll.ebook.app.hashTag.entity.PostHashTag;
import com.ll.ebook.app.member.entity.Member;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Post extends BaseEntity {
    @ManyToOne
    private Member authorId;
    private String subject;
    private String content;
    private String contentHtml;

    public String getExtra_inputValue_hashTagContents() {
        Map<String, Object> extra = getExtra();

        if (extra.containsKey("hashTags") == false) {
            return "";
        }

        List<PostHashTag> hashTags = (List<PostHashTag>) extra.get("hashTags");

        if (hashTags.isEmpty()) {
            return "";
        }

        return hashTags
                .stream()
                .map(hashTag -> "#" + hashTag.getPostKeywordId().getContent())
                .sorted()
                .collect(Collectors.joining(" "));
    }

    public String getExtra_hashTagLinks() {
        Map<String, Object> extra = getExtra();

        if (extra.containsKey("hashTags") == false) {
            return "";
        }

        List<PostHashTag> hashTags = (List<PostHashTag>) extra.get("hashTags");

        if (hashTags.isEmpty()) {
            return "";
        }

        return hashTags
                .stream()
                .map(hashTag -> {
                    String text = "#" + hashTag.getPostKeywordId().getContent();

                    return """
                            <a href="%s" target="_blank">%s</a>
                            """
                            .stripIndent()
                            .formatted(hashTag.getPostKeywordId().getListUrl(), text);
                })
                .sorted()
                .collect(Collectors.joining(" "));
    }
}