package com.kusitms29.backendH.application.community.controller;

import com.kusitms29.backendH.domain.comment.application.controller.dto.request.CommentCreateRequestDto;
import com.kusitms29.backendH.domain.comment.application.controller.dto.response.CommentCreateResponseDto;
import com.kusitms29.backendH.domain.comment.application.controller.dto.response.CommentResponseDto;
import com.kusitms29.backendH.domain.comment.application.service.CommentService;
import com.kusitms29.backendH.domain.post.application.controller.dto.request.PostCreateRequestDto;
import com.kusitms29.backendH.domain.post.application.controller.dto.response.PostCreateResponseDto;
import com.kusitms29.backendH.domain.post.application.controller.dto.response.PostDetailResponseDto;
import com.kusitms29.backendH.domain.post.application.controller.dto.response.PostResponseDto;
import com.kusitms29.backendH.domain.post.application.controller.dto.response.PostSearchResponseDto;
import com.kusitms29.backendH.domain.post.application.service.PostSearchService;
import com.kusitms29.backendH.domain.post.application.service.PostService;
import com.kusitms29.backendH.domain.postLike.application.service.PostLikeService;
import com.kusitms29.backendH.global.common.SuccessResponse;
import com.kusitms29.backendH.infra.config.auth.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/community")
@RestController
public class CommunityController {
    private final PostService postService;
    private final PostSearchService postSearchService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;
    @GetMapping("/post")
    public ResponseEntity<SuccessResponse<?>> getPagingPostByPostType(@UserId Long userId, @RequestParam String postType, Pageable pageable) {
        List<PostResponseDto> responseDto = postService.getPagingPostByPostType(userId, postType, pageable);
        return SuccessResponse.ok(responseDto);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<SuccessResponse<?>> getDetailPost(@UserId Long userId, @PathVariable Long postId) {
        PostDetailResponseDto responseDto = postService.getDetailPost(userId, postId);
        return SuccessResponse.ok(responseDto);
    }

    @PostMapping("/post")
    public ResponseEntity<SuccessResponse<?>> createPost(@UserId Long userId,
                                                         @RequestPart List<MultipartFile> images,
                                                         @RequestPart PostCreateRequestDto requestDto) {
        PostCreateResponseDto responseDto = postService.createPost(userId, images, requestDto);
        return SuccessResponse.ok(responseDto);
    }

    @GetMapping("/post/search")
    public ResponseEntity<SuccessResponse<?>> searchPost(@UserId Long userId, @RequestParam String keyword, Pageable pageable) {
        List<PostSearchResponseDto> responseDtos = postSearchService.searchPosts(userId, keyword, pageable);
        return SuccessResponse.ok(responseDtos);
    }
    @PostMapping("/post/like/{postId}")
    public ResponseEntity<SuccessResponse<?>> createPostLike(@UserId Long userId,
                                                                @PathVariable Long postId) {
        postLikeService.createPostLike(userId, postId);
        return SuccessResponse.ok(true);
    }
    @DeleteMapping("/post/like/{postId}")
    public ResponseEntity<SuccessResponse<?>> deletePostLike(@UserId Long userId,
                                                                @PathVariable Long postId) {
        postLikeService.deletePostLike(userId, postId);
        return SuccessResponse.ok(true);
    }

    @GetMapping("/comment/{postId}")
    public ResponseEntity<SuccessResponse<?>> getCommentsInPost(@UserId Long userId, @PathVariable Long postId, Pageable pageable) {
        List<CommentResponseDto> comments = commentService.getCommentsInPost(userId, postId, pageable);
        return SuccessResponse.ok(comments);
    }
    @PostMapping("/commment/{postId}")
    public ResponseEntity<SuccessResponse<?>> createComment(@UserId Long userId, @PathVariable Long postId,
                                                            @RequestBody CommentCreateRequestDto content) {
        CommentCreateResponseDto commentCreateResponseDto = commentService.createComment(userId, postId, content.getContent());
        return SuccessResponse.ok(commentCreateResponseDto);
    }



}
