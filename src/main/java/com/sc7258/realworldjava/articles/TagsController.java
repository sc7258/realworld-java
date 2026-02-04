package com.sc7258.realworldjava.articles;

import com.sc7258.realworldjava.api.TagsApi;
import com.sc7258.realworldjava.model.TagsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TagsController implements TagsApi {

    private final TagService tagService;

    @Override
    public ResponseEntity<TagsResponse> getTags() {
        return ResponseEntity.ok(tagService.getTags());
    }
}
