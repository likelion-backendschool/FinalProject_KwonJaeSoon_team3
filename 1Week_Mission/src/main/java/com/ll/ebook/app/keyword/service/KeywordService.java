package com.ll.ebook.app.keyword.service;

import com.ll.ebook.app.keyword.entity.PostKeyword;
import com.ll.ebook.app.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;

    public PostKeyword save(String keywordContent) {
        Optional<PostKeyword> optKeyword = keywordRepository.findByContent(keywordContent);

        if ( optKeyword.isPresent() ) {
            return optKeyword.get();
        }

        PostKeyword keyword = PostKeyword
                .builder()
                .content(keywordContent)
                .build();

        keywordRepository.save(keyword);

        return keyword;
    }
}