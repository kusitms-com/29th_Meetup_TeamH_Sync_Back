package com.kusitms29.backendH.domain.post.repository;

import com.kusitms29.backendH.domain.post.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findByPostId(Long postId);
}
