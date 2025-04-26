package com.hikers.hikemate.service;

import com.hikers.hikemate.dto.ReviewPostRequestDTO;
import com.hikers.hikemate.dto.ReviewPostResponseDTO;
import com.hikers.hikemate.entity.*;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.repository.CourseRepository;
import com.hikers.hikemate.repository.CrewPostRepository;
import com.hikers.hikemate.repository.ReviewPostRepository;
import com.hikers.hikemate.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewPostService {
    private final ReviewPostRepository reviewPostRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final JwtUtil jwtUtil;
    private final S3Service s3Service;
    private final ImageService imageService;

    @Autowired
    public ReviewPostService(ReviewPostRepository reviewPostRepository,
                             UserRepository userRepository,
                             CourseRepository courseRepository,
                             JwtUtil jwtUtil,
                             S3Service s3Service,
                             ImageService imageService) {
        this.reviewPostRepository = reviewPostRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.jwtUtil = jwtUtil;
        this.s3Service = s3Service;
        this.imageService = imageService;
    }

    private ReviewPostResponseDTO toResponseDTO(ReviewPost post, String currentUserId) {
        boolean isWriter = post.getAuthor().getUserId().equals(currentUserId);

        // 연관된 엔티티에서 필요한 값들 가져오기
        String courseName = post.getCourse().getCourseName();
        String mountainName = post.getCourse().getMountain().getMntName();
        List<String> imageUrls = post.getImages()
                .stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        // ResponseDTO 반환
        return new ReviewPostResponseDTO(
                post.getId(),
                post.getAuthor().getNickname(),
                post.getTitle(),
                post.getContent(),
                post.getLevel(),
                courseName,
                mountainName,
                imageUrls,
                isWriter,
                post.getCreatedAt()

        );
    }


        @Transactional
    public ReviewPostResponseDTO createReviewPost(String token, ReviewPostRequestDTO request){
        String userId = jwtUtil.extractUserId(token);

        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("작성자가 존재하지 않습니다.");
        }
        User user = userOptional.get();
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("해당 코스를 찾을 수 없습니다."));

        ReviewPost reviewPost = ReviewPost.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .level(request.getLevel())
                .author(user)
                .course(course)
                .build();

        List<Image> imageList = new ArrayList<>(); // 이미지 목록을 저장할 리스트
        for (MultipartFile file : request.getImages()) {
            try {
                // S3에 파일 업로드 후 URL 반환
                String imageUrl = s3Service.uploadImage(file); // 이미지 업로드

                // Image 엔티티 생성
                Image image = new Image();
                image.setImageUrl(imageUrl);
                image.setReviewPost(reviewPost);

                imageList.add(image); // 이미지 리스트에 추가
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e); // 예외 처리
            }
        }
        reviewPost.setImages(imageList);
            ReviewPost savedPost = reviewPostRepository.save(reviewPost);

        return toResponseDTO(savedPost, userId);
    }

    public ReviewPostResponseDTO getReviewPostById(Long postId, String currentUserId) {
        Optional<ReviewPost> optionalPost = reviewPostRepository.findById(postId);

        if (optionalPost.isEmpty()) {
            return null;
        }

        ReviewPost post = optionalPost.get();
        return toResponseDTO(post, currentUserId);
    }


}
