package com.sc7258.realworldjava.articles;

import com.sc7258.realworldjava.api.ArticlesApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ArticlesController implements ArticlesApi {

    private final ArticleService articleService;

}
