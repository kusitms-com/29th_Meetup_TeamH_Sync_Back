package com.kusitms29.backendH.application.community.service;

import com.kusitms29.backendH.domain.comment.repository.CommentRepository;
import com.kusitms29.backendH.domain.post.application.controller.dto.request.PostCalculateDto;
import com.kusitms29.backendH.domain.post.application.controller.dto.request.PostCreateRequestDto;
import com.kusitms29.backendH.domain.post.application.controller.dto.response.PostCreateResponseDto;
import com.kusitms29.backendH.domain.post.application.controller.dto.response.PostDetailResponseDto;
import com.kusitms29.backendH.domain.post.application.controller.dto.response.PostResponseDto;
import com.kusitms29.backendH.domain.post.domain.Post;
import com.kusitms29.backendH.domain.post.domain.PostImage;
import com.kusitms29.backendH.domain.post.domain.PostType;
import com.kusitms29.backendH.domain.post.repository.PostImageRepository;
import com.kusitms29.backendH.domain.post.repository.PostPagingRepository;
import com.kusitms29.backendH.domain.post.repository.PostRepository;
import com.kusitms29.backendH.domain.postLike.repository.PostLikeRepository;
import com.kusitms29.backendH.domain.user.domain.User;
import com.kusitms29.backendH.domain.user.repository.UserRepository;
import com.kusitms29.backendH.global.error.exception.EntityNotFoundException;
import com.kusitms29.backendH.global.error.exception.NotAllowedException;
import com.kusitms29.backendH.infra.config.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.kusitms29.backendH.global.error.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostService {
    private final PostPagingRepository postPagingRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AwsS3Service awsS3Service;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;

    public List<PostResponseDto> getPagingPostByPostType(Long userId, String postType, Pageable pageable) {
        PostType enumPostType = PostType.getEnumPostTypeFromStringPostType(postType);
        Page<Post> lifePosts = postPagingRepository.findByPostType(enumPostType, pageable);
        return lifePosts.stream()
                .map(post -> mapToPostResponseDto(post, userId))
                .collect(Collectors.toList());
    }

    private PostResponseDto mapToPostResponseDto(Post post, Long userId) {
        PostCalculateDto postCalculateDto = calculatePostDetail(post, userId);
        PostImage postImage = postImageRepository.findByPostIdAndIsRepresentative(post.getId(), true);

        return PostResponseDto.of(
                post.getId(),
                post.getPostType().getStringPostType(),
                post.getUser().getProfile(),
                post.getUser().getUserName(),
                post.getCreatedAt(),
                post.getTitle(),
                post.getContent(),
                (postImage != null)  ? postImage.getImage_url() : null,
                postCalculateDto.getLikeCount(),
                postCalculateDto.isLikedByUser(),
                postCalculateDto.getCommentCount(),
                postCalculateDto.isPostedByUser()
        );
    }

    public PostDetailResponseDto getDetailPost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new EntityNotFoundException(POST_NOT_FOUND));

        PostCalculateDto postCalculateDto = calculatePostDetail(post, userId);

        List<String> imageUrls = postImageRepository.findByPostId(post.getId())
                .stream()
                .map(PostImage::getImage_url)
                .collect(Collectors.toList());

        return PostDetailResponseDto.of(
                post.getPostType().getStringPostType(),
                post.getUser().getProfile(),
                post.getUser().getUserName(),
                post.getCreatedAt(),
                post.getTitle(),
                post.getContent(),
                postCalculateDto.getLikeCount(),
                postCalculateDto.isLikedByUser(),
                postCalculateDto.getCommentCount(),
                postCalculateDto.isPostedByUser(),
                imageUrls
        );
    }

    private PostCalculateDto calculatePostDetail(Post post, Long userId) {
        int likeCount = postLikeRepository.countByPostId(post.getId());
        boolean isLikedByUser = postLikeRepository.existsByPostIdAndUserId(post.getId(), userId);
        int commentCount = commentRepository.countByPostId(post.getId());
        boolean isPostedByUser = post.getUser().getId() == userId;
        return new PostCalculateDto(likeCount, isLikedByUser, commentCount, isPostedByUser);
    }

    public PostCreateResponseDto createPost(Long userId, List<MultipartFile> images, PostCreateRequestDto requestDto) {
        User writer = userRepository.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException(USER_NOT_FOUND));
        PostType postType = PostType.getEnumPostTypeFromStringPostType(requestDto.getPostType());

        String title = requestDto.getTitle();
        String content = requestDto.getContent();
        if(title.length() > 30) {
            throw new NotAllowedException(TOO_LONG_TITLE_NOT_ALLOWED);
        }
        if(content.length() > 300) {
            throw new NotAllowedException(TOO_LONG_CONTENT_NOT_ALLOWED);
        }
        if(images.size() > 5) {
            throw new NotAllowedException(TOO_MANY_IMAGES_NOT_ALLOWED);
        }

        Post newPost = postRepository.save
                (Post.builder()
                        .user(writer)
                        .postType(postType)
                        .title(requestDto.getTitle())
                        .content(requestDto.getContent())
                        .build());

        List<String> imageUrls = awsS3Service.uploadImages(images);
        for(int i=0; i<images.size(); i++) {
            postImageRepository.save(PostImage.builder()
                    .post(newPost)
                    .image_url(imageUrls.get(i))
                    .isRepresentative(i == 0)
                    .build());
        }

        return PostCreateResponseDto.of(
                newPost.getPostType().getStringPostType(),
                newPost.getTitle(),
                newPost.getContent(),
                imageUrls
        );
    }
}
