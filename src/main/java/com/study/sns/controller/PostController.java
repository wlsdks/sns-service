package com.study.sns.controller;

import com.study.sns.controller.request.PostCreateRequest;
import com.study.sns.controller.response.Response;
import com.study.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/")
    public Response<Void> create(@RequestBody PostCreateRequest request) {

        postService.create(request.getTitle(), request.getBody(), "");
        return Response.success(null);
    }
}
