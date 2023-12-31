package com.study.sns.controller;

import com.study.sns.controller.request.PostCommentRequest;
import com.study.sns.controller.request.PostCreateRequest;
import com.study.sns.controller.request.PostModifyRequest;
import com.study.sns.controller.response.CommentResponse;
import com.study.sns.controller.response.PostResponse;
import com.study.sns.controller.response.Response;
import com.study.sns.model.Post;
import com.study.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    // 요청보낼때 title, body값 json으로 body에 넣어서 보내줘야함
    @PutMapping("/{postId}")
    public Response<PostResponse> modify(@PathVariable Integer postId,
                                         @RequestBody PostModifyRequest request,
                                         Authentication authentication) {

        Post post = postService.modify(request.getTitle(), request.getBody(), authentication.getName(), postId);
        return Response.success(PostResponse.fromPost(post));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(@PathVariable Integer postId,
                                 Authentication authentication) {

        postService.delete(authentication.getName(), postId);
        return Response.success();
    }

    @GetMapping
    public Response<Page<PostResponse>> list(Pageable pageable,
                                             Authentication authentication) {

        return Response.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    @GetMapping("/my")
    public Response<Page<PostResponse>> my(Pageable pageable,
                                           Authentication authentication) {

        return Response.success(postService.my(authentication.getName(), pageable).map(PostResponse::fromPost));
    }

    @PostMapping("/{postId}/likes")
    public Response<Void> like(@PathVariable Integer postId,
                               Authentication authentication) {

        postService.like(postId, authentication.getName());
        return Response.success();
    }

    @GetMapping("/{postId}/likes")
    public Response<Long> likeCount(@PathVariable Integer postId,
                                       Authentication authentication) {

        return Response.success(postService.likeCount(postId));
    }

    /**
     * 댓글 작성
     */
    @PostMapping("/{postId}/comments")
    public Response<Void> comment(@PathVariable Integer postId,
                                  @RequestBody PostCommentRequest request,
                                  Authentication authentication) {

        postService.comment(postId, authentication.getName(), request.getComment());
        return Response.success();
    }

    /**
     * 댓글 가져오기
     */
    @GetMapping("/{postId}/comments")
    public Response<Page<CommentResponse>> commen2t(@PathVariable Integer postId,
                                                    Pageable pageable,
                                                    Authentication authentication) {

        return Response.success(postService.getComments(postId, pageable).map(CommentResponse::fromComment));
    }

}
