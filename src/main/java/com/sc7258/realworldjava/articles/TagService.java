package com.sc7258.realworldjava.articles;

import com.sc7258.realworldjava.articles.entity.Tag;
import com.sc7258.realworldjava.model.TagsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public TagsResponse getTags() {
        List<String> tagNames = tagRepository.findAll().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
        return new TagsResponse().tags(tagNames);
    }
}
