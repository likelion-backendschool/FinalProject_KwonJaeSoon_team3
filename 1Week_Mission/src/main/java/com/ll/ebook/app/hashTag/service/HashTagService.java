package com.ll.ebook.app.hashTag.service;

import com.ll.ebook.app.hashTag.entity.PostHashTag;
import com.ll.ebook.app.hashTag.repository.HashTagRepository;
import com.ll.ebook.app.keyword.entity.PostKeyword;
import com.ll.ebook.app.keyword.service.KeywordService;
import com.ll.ebook.app.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashTagService {
    private final KeywordService keywordService;
    private final HashTagRepository hashTagRepository;


    public void applyHashTags(Post post, String hashTagContents) {
        List<PostHashTag> oldHashTags = getHashTags(post);

        List<String> keywordContents = Arrays.stream(hashTagContents.split("#"))
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .collect(Collectors.toList());

        List<PostHashTag> needToDelete = new ArrayList<>();

        for (PostHashTag oldHashTag : oldHashTags) {
            boolean contains = keywordContents.stream().anyMatch(s -> s.equals(oldHashTag.getPostKeywordId().getContent()));

            if (contains == false) {
                needToDelete.add(oldHashTag);
            }
        }

        needToDelete.forEach(hashTag -> {
            hashTagRepository.delete(hashTag);
        });

        keywordContents.forEach(keywordContent -> {
            saveHashTag(post, keywordContent);
        });
    }

    private PostHashTag saveHashTag(Post post, String keywordContent) {
        PostKeyword keyword = keywordService.save(keywordContent);

        Optional<PostHashTag> opHashTag = hashTagRepository.findByPostIdIdAndPostKeywordIdId(post.getId(), keyword.getId());

        if (opHashTag.isPresent()) {
            return opHashTag.get();
        }

        PostHashTag hashTag = PostHashTag.builder()
                .memberId(post.getAuthorId())
                .postId(post)
                .postKeywordId(keyword)
                .build();

        hashTagRepository.save(hashTag);

        return hashTag;
    }

    public List<PostHashTag> getHashTags(Post post) {
        return hashTagRepository.findAllByPostIdId(post.getId());
    }
}
