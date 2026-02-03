package com.sc7258.realworldjava.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ArticleNotFoundException extends RuntimeException {
    public ArticleNotFoundException(String slug) {
        super("Article with slug " + slug + " not found");
    }
}
