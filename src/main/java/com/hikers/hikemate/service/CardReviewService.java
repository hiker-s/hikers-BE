package com.hikers.hikemate.service;

import com.hikers.hikemate.dto.review.CardReviewDTO;
import com.hikers.hikemate.entity.ReviewPost;
import com.hikers.hikemate.repository.ReviewPostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CardReviewService {

    private final ReviewPostRepository reviewPostRepository;

    public List<CardReviewDTO> getReviewListByCourse(String sortType, String currentUserId, Long courseId) {
        List<ReviewPost> reviewPosts;

        if ("latest".equals(sortType)) {
            reviewPosts = reviewPostRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
        } else if ("likes".equals(sortType)) {
            reviewPosts = reviewPostRepository.findByCourseIdOrderByLikesDesc(courseId);
        } else {
            throw new IllegalArgumentException("정렬 타입이 잘못되었습니다.");
        }

        return reviewPosts.stream()
                .map(post -> {
                    boolean likedByCurrentUser = post.getLikes().stream()
                            .anyMatch(like -> like.getUser().getUserId().equals(currentUserId));

                    List<String> imageUrls = post.getImages().stream()
                            .map(image -> image.getImageUrl())
                            .collect(Collectors.toList());

                    return CardReviewDTO.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .description(post.getContent())
                            .level(post.getLevel())
                            .imgUrl(imageUrls.isEmpty() ? null : imageUrls.get(0))
                            .isLiked(likedByCurrentUser)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<CardReviewDTO> getReviewListByMountain(String sortType, String currentUserId, Long mnt_id) {
        List<ReviewPost> reviewPosts;

        if ("latest".equals(sortType)) {
            reviewPosts = reviewPostRepository.findByMountainIdOrderByCreatedAtDesc(mnt_id);
        } else if ("likes".equals(sortType)) {
            reviewPosts = reviewPostRepository.findByMountainIdOrderByLikesDesc(mnt_id);
        } else {
            throw new IllegalArgumentException("정렬 타입이 잘못되었습니다.");
        }

        return reviewPosts.stream()
                .map(post -> {
                    boolean likedByCurrentUser = post.getLikes().stream()
                            .anyMatch(like -> like.getUser().getUserId().equals(currentUserId));

                    List<String> imageUrls = post.getImages().stream()
                            .map(image -> image.getImageUrl())
                            .collect(Collectors.toList());

                    return CardReviewDTO.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .description(post.getContent())
                            .level(post.getLevel())
                            .imgUrl(imageUrls.isEmpty() ? null : imageUrls.get(0))
                            .isLiked(likedByCurrentUser)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
