package com.hikers.hikemate.service;

import com.hikers.hikemate.dto.ReviewPostRequestDTO;
import com.hikers.hikemate.dto.ReviewPostResponseDTO;
import com.hikers.hikemate.entity.*;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.repository.*;
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
    private final LikeRepository likeRepository;

    @Autowired
    public ReviewPostService(ReviewPostRepository reviewPostRepository,
                             UserRepository userRepository,
                             CourseRepository courseRepository,
                             JwtUtil jwtUtil,
                             S3Service s3Service,
                             ImageService imageService,
                             LikeRepository likeRepository) {
        this.reviewPostRepository = reviewPostRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.jwtUtil = jwtUtil;
        this.s3Service = s3Service;
        this.imageService = imageService;
        this.likeRepository = likeRepository;
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

        int likeCount = post.getLikes().size();

        boolean likedByCurrentUser = post.getLikes().stream()
                .anyMatch(like -> like.getUser().getUserId().equals(currentUserId));

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
                likeCount,
                likedByCurrentUser,
                post.getCreatedAt() // 생성자 순서 꼭 맞춰줘야 함.
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
    public List<ReviewPostResponseDTO> getReviewList(String sortType, String currentUserId) {
        List<ReviewPost> reviewPosts;

        // 정렬 기준에 따라 게시물 목록을 가져옵니다.
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


                    ReviewPostResponseDTO dto = ReviewPostResponseDTO.builder()
                            .id(post.getId())
                            .authorName(post.getAuthor().getNickname())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .level(post.getLevel())
                            .courseName(post.getCourse().getCourseName())
                            .mountainName(post.getCourse().getMountain().getMntName())
                            .imageUrls(imageUrls)
                            .isWriter(post.getAuthor().getUserId().equals(currentUserId))
                            .likeCount(post.getLikes().size())
                            .likedByCurrentUser(likedByCurrentUser)
                            .createdAt(post.getCreatedAt())
                            .build();
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewPost updateReviewPost(Long postId, User user, ReviewPostRequestDTO dto) {
        // 1) Retrieve the existing post
        ReviewPost post = reviewPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));

        // 2) Authorization check
        if (!post.getAuthor().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        // 3) Delete old images
        imageService.deleteImagesByPost(post);

        // 4) Save new images
        List<Image> savedImages = new ArrayList<>();
        if (dto.getImages() != null) {
            for (MultipartFile file : dto.getImages()) {
                try {
                    String imageUrl = s3Service.uploadImage(file);
                    Image image = new Image();
                    image.setImageUrl(imageUrl);
                    image.setReviewPost(post);
                    savedImages.add(image);
                } catch (IOException e) {
                    throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
                }
            }
        }

        // 5) Update post content
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setLevel(dto.getLevel());

        // 6) Set new images
        post.setImages(savedImages);

        // 7) Save and return the updated post
        return reviewPostRepository.save(post);
    }

    public int likeReviewPost(Long postId, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        ReviewPost post = reviewPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        // 이미 좋아요 여부 검사
        Optional<Like> existing = likeRepository.findByUserAndReviewPost(user, post);
        if (existing.isEmpty()) {
            // 새 좋아요 저장
            Like like = new Like(user, post);
            likeRepository.save(like);
        }

        // 최종 좋아요 개수 반환
        return likeRepository.countByReviewPost(post);
    }

    @Transactional
    public int unlikeReviewPost(Long postId, String userId) {
        // 게시글 찾기
        ReviewPost post = reviewPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        // 유저 찾기
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // Like 찾기
        Like like = likeRepository.findByUserAndReviewPost(user, post)
                .orElseThrow(() -> new IllegalArgumentException("좋아요를 누르지 않았습니다."));
        // Like 삭제
        likeRepository.delete(like);
        // 남은 좋아요 수 리턴 (count로 세거나, post.getLikes().size() 새로 읽어오기)
        return likeRepository.countByReviewPost(post);
    }

    // 내가 작성한 리뷰 목록 가져오기
    public List<ReviewPostResponseDTO> getMyWrittenReviewPosts(String userId, String sortType) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<ReviewPost> reviewPosts;

        if ("likes".equals(sortType)) {
            reviewPosts = reviewPostRepository.findByAuthorOrderByLikesDesc(user);
        } else {
            reviewPosts = reviewPostRepository.findByAuthorOrderByCreatedAtDesc(user);
        }

        return reviewPosts.stream()
                .map(post -> toResponseDTO(post, userId))
                .collect(Collectors.toList());
    }

    // 좋아요 누른 리뷰 목록 가져오기
    public List<ReviewPostResponseDTO> getLikedReviewPosts(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Like> likes = likeRepository.findByUser(user);

        return likes.stream()
                .map(like -> toResponseDTO(like.getReviewPost(), userId))
                .collect(Collectors.toList());
    }
    @Transactional
    public void deleteReviewPost(Long postId, String userId) {
        // 1) 게시글 가져오기
        ReviewPost post = reviewPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 2) 작성자인지 확인
        if (!post.getAuthor().getUserId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        // 3) 연관된 이미지 삭제 (DB + S3)
        imageService.deleteImagesByPost(post);

        // 4) 연관된 좋아요 삭제
        List<Like> likes = likeRepository.findByReviewPost(post);
        likeRepository.deleteAll(likes);

        // 5) 게시글 삭제
        reviewPostRepository.delete(post);
    }




}
