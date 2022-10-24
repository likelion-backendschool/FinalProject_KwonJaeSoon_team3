package com.ll.ebook.app.post.repository;

import com.ll.ebook.app.post.entity.Post;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ll.ebook.app.hashTag.entity.QPostHashTag.postHashTag;
import static com.ll.ebook.app.keyword.entity.QPostKeyword.postKeyword;
import static com.ll.ebook.app.post.entity.QPost.post;
import static com.ll.ebook.app.member.entity.QMember.member;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getPosts() {
        return jpaQueryFactory
                .select(post)
                .from(post)
                .orderBy(post.id.desc())
                .fetch();
    }

    @Override
    public List<Post> getPostsByMemberIdAndKeyword(Long memberId, String keyword) {
        return jpaQueryFactory
                .selectFrom(post)
                .innerJoin(member)
                .on(post.member.id.eq(member.id))
                .innerJoin(postHashTag)
                .on(post.id.eq(postHashTag.post.id))
                .innerJoin(postKeyword)
                .on(postHashTag.postKeyword.id.eq(postKeyword.id))
                .where(post.member.id.eq(memberId)
                        .and(
                                postKeyword.content.eq(keyword)
                        )
                )
                .fetch();
    }
}
