package com.ll.ebook.app.hashTag.entity;

import com.ll.ebook.app.base.entity.BaseEntity;
import com.ll.ebook.app.keyword.entity.PostKeyword;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.post.entity.Post;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class PostHashTag extends BaseEntity {
    @ManyToOne
    @ToString.Exclude
    private Member memberId;
    @ManyToOne
    @ToString.Exclude
    private Post postId;
    @ManyToOne
    @ToString.Exclude
    private PostKeyword postKeywordId;
}