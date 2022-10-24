package com.ll.ebook.app.keyword.repository;

import com.ll.ebook.app.keyword.entity.PostKeyword;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.ll.ebook.app.hashTag.entity.QPostHashTag.postHashTag;
import static com.ll.ebook.app.keyword.entity.QPostKeyword.postKeyword;

@RequiredArgsConstructor
public class PostKeywordRepositoryImpl implements PostKeywordRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PostKeyword> getQslAllByAuthorId(Long authorId) {
        List<Tuple> fetch = jpaQueryFactory
                .select(postKeyword, postHashTag.count())
                .from(postKeyword)
                .innerJoin(postHashTag)
                .on(postKeyword.eq(postHashTag.postKeyword))
                .where(postHashTag.member.id.eq(authorId))
                .orderBy(postHashTag.post.id.desc())
                .groupBy(postKeyword.id)
                .fetch();

        return fetch.stream().
                map(tuple -> {
                    PostKeyword _postKeyword = tuple.get(postKeyword);
                    Long postTagsCount = tuple.get(postHashTag.count());

                    _postKeyword.getExtra().put("postTagsCount", postTagsCount);

                    return _postKeyword;
                })
                .collect(Collectors.toList());
    }
}
