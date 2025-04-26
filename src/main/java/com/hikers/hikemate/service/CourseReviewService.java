package com.hikers.hikemate.service;

import com.hikers.hikemate.dto.review.ReviewCardDTO;
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
public class CourseReviewService {

    private final ReviewPostRepository reviewPostRepository;

    public List<ReviewCardDTO> getReviewListByCourse(String sortType, String currentUserId) {
        List<ReviewPost> reviewPosts;

        // 정렬 기준에 따라 게시물 목록 가져오기
        if ("latest".equals(sortType)) {
            reviewPosts = reviewPostRepository.findAllByOrderByCreatedAtDesc();  // 최신순
        } else if ("likes".equals(sortType)) {
            reviewPosts = reviewPostRepository.findAllByOrderByLikesDesc();  // 좋아요 많은 순
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


                    ReviewCardDTO dto = ReviewCardDTO.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .description(post.getContent())
                            .level(post.getLevel())
                            .imgUrl(imageUrls.isEmpty() ? null : imageUrls.get(0))
                            .isLiked(likedByCurrentUser)
                            .build();
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
