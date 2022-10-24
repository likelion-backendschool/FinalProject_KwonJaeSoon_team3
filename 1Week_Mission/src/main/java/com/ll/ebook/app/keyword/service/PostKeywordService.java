package com.ll.ebook.app.keyword.service;

import com.ll.ebook.app.keyword.entity.PostKeyword;
import com.ll.ebook.app.keyword.repository.PostKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostKeywordService {
    private final PostKeywordRepository postKeywordRepository;

    public PostKeyword save(String keywordContent) {
        Optional<PostKeyword> optKeyword = postKeywordRepository.findByContent(keywordContent);

        if ( optKeyword.isPresent() ) {
            return optKeyword.get();
        }

        PostKeyword keyword = PostKeyword
                .builder()
                .content(keywordContent)
                .build();

        postKeywordRepository.save(keyword);

        return keyword;
    }

    public Optional<PostKeyword> findById(Long postKeywordId) {
        return postKeywordRepository.findById(postKeywordId);
    }

    public List<PostKeyword> findByMemberId(Long authorId) {
        return postKeywordRepository.getQslAllByAuthorId(authorId);
    }
}