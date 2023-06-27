package com.study.sns.controller;

import com.study.sns.controller.request.PostCreateRequest;
import com.study.sns.controller.response.Response;
import com.study.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 요청보낼때 title, body값 json으로 body에 넣어서 보내줘야함
    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.create(request.getTitle(), request.getBody(), authentication.getName());
        return Response.success();
    }

}
