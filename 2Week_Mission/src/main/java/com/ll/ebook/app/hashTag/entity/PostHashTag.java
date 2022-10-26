package com.ll.ebook.app.hashTag.entity;

import com.ll.ebook.app.base.entity.BaseEntity;
import com.ll.ebook.app.keyword.entity.PostKeyword;
import com.ll.ebook.app.member.entity.Member;
import com.ll.ebook.app.post.entity.Post;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    @ManyToOne
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;
    @ManyToOne
    @ToString.Exclude
    private PostKeyword postKeyword;
}