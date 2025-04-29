package com.hikers.hikemate.controller;
import com.hikers.hikemate.dto.ReviewPostRequestDTO;
import com.hikers.hikemate.dto.ReviewPostResponseDTO;
import com.hikers.hikemate.dto.base.ErrorResponseDTO;
import com.hikers.hikemate.dto.base.SuccessResponseDTO;
import com.hikers.hikemate.entity.ReviewPost;
import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.service.ImageService;
import com.hikers.hikemate.service.ReviewPostService;
import com.hikers.hikemate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.hikers.hikemate.entity.Image;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/review")
public class ReviewPostController {

    private final ReviewPostService reviewPostService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ImageService imageService;

    @Autowired
    public ReviewPostController(ReviewPostService reviewPostService, UserService userService, JwtUtil jwtUtil, ImageService imageService) {
        this.reviewPostService = reviewPostService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.imageService = imageService;

    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<?> createReviewPost(@RequestHeader("Authorization") String token,
                                              @RequestParam("title") String title,
                                              @RequestParam("content") String content,
                                              @RequestParam("level") String level,
                                              @RequestParam("courseId") Integer courseId,
                                              @RequestParam(value = "images", required = false) List<MultipartFile> images){


        // JWT 토큰에서 사용자 ID 추출
        if (!token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String pureToken = token.substring(7).trim();
        String userId = jwtUtil.extractUserId(pureToken);

        // ReviewPostRequestDTO 객체 생성
        ReviewPostRequestDTO requestDTO = new ReviewPostRequestDTO(title, content, images, level, courseId);

        // 서비스 호출하여 ReviewPostResponseDTO 생성
        ReviewPostResponseDTO createdPost = reviewPostService.createReviewPost(pureToken, requestDTO);

        // 생성된 게시물을 클라이언트에게 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getReviewPost(@RequestHeader(value = "Authorization", required = false) String token,
                                          @PathVariable Long postId) {

        // JWT 토큰에서 사용자 ID 추출
        String userId = null;
        if (token != null && token.startsWith("Bearer ")) {
            String pureToken = token.substring(7).trim();
            userId = jwtUtil.extractUserId(pureToken);
        }

        // ReviewPostService에서 상세조회 처리
        ReviewPostResponseDTO reviewPostResponseDTO = reviewPostService.getReviewPostById(postId, userId);

        // 해당 게시물이 없을 경우 404 반환
        if (reviewPostResponseDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");
        }

        // 조회된 게시물 반환
        return ResponseEntity.status(HttpStatus.OK).body(reviewPostResponseDTO);
    }

    @GetMapping()
    public ResponseEntity<?> getReviewList(@RequestHeader(value = "Authorization", required = false) String token,
                                           @RequestParam(value = "sortType", defaultValue = "latest") String sortType) {
        // JWT 토큰에서 사용자 ID 추출
        String userId = null;
        if (token != null && token.startsWith("Bearer ")) {
            String pureToken = token.substring(7).trim();
            userId = jwtUtil.extractUserId(pureToken);
        }

        // 서비스에서 게시물 목록을 받아옵니다.
        List<ReviewPostResponseDTO> reviewPostList = reviewPostService.getReviewList(sortType, userId);

        return ResponseEntity.status(HttpStatus.OK).body(reviewPostList);
    }

    @PutMapping(value = "/{postId}", produces = "application/json")
    public ResponseEntity<?> updateReviewPost(
            @RequestHeader("Authorization") String token,
            @PathVariable("postId") Long postId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("level") String level,
            @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) {
        if (!token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid token format.");
        }
        String userId = jwtUtil.extractUserId(token.substring(7).trim());
        User user = userService.findUserByUserId(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

        ReviewPostRequestDTO dto = new ReviewPostRequestDTO(title, content, images, level, null); // null for courseId


        ReviewPost updatedPost;
        try {
            updatedPost = reviewPostService.updateReviewPost(postId, user, dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }

        // Convert the updated post to DTO for response
        List<String> imageUrls = updatedPost.getImages().stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        ReviewPostResponseDTO response = new ReviewPostResponseDTO(
                updatedPost.getId(),
                updatedPost.getAuthor().getNickname(),
                updatedPost.getTitle(),
                updatedPost.getContent(),
                updatedPost.getLevel(),
                updatedPost.getCourse().getCourseName(),
                updatedPost.getCourse().getMountain().getMntName(),
                imageUrls,
                true, // Assuming the user is the writer for now
                updatedPost.getLikes().size(),
                false, // Assuming the user has not liked the post for now
                updatedPost.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likeReviewPost(
            @RequestHeader("Authorization") String token,
            @PathVariable Long postId) {

        // 토큰 검증
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Invalid token format."));
        }
        String userId = jwtUtil.extractUserId(token.substring(7).trim());

        // 서비스 호출: 좋아요 처리
        int updatedLikeCount;
        try {
            updatedLikeCount = reviewPostService.likeReviewPost(postId, userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, e.getMessage()));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SuccessResponseDTO<>(
                        201,
                        "좋아요가 성공적으로 추가되었습니다.",
                        Map.of(
                                "postId", postId,
                                "likeCount", updatedLikeCount,
                                "likedByCurrentUser", true
                        )
                ));
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<?> unlikeReviewPost(
            @RequestHeader("Authorization") String token,
            @PathVariable Long postId) {

        // 토큰 검증
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "Invalid token format."));
        }
        String userId = jwtUtil.extractUserId(token.substring(7).trim());

        // 서비스 호출: 좋아요 취소 처리
        int updatedLikeCount;
        try {
            updatedLikeCount = reviewPostService.unlikeReviewPost(postId, userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, e.getMessage()));
        }

        // 200 OK로 반환 (DELETE는 보통 OK or NoContent(204))
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        200,
                        "좋아요가 성공적으로 취소되었습니다.",
                        Map.of(
                                "postId", postId,
                                "likeCount", updatedLikeCount,
                                "likedByCurrentUser", false
                        )
                )
        );
    }
    @GetMapping("/liked")
    public ResponseEntity<?> getLikedReviewPosts(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "sortType", defaultValue = "latest") String sortType) {

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "유효하지 않은 토큰입니다."));
        }
        String userId = jwtUtil.extractUserId(token.substring(7).trim());

        List<ReviewPostResponseDTO> likedPosts = reviewPostService.getLikedReviewPosts(userId, sortType);

        SuccessResponseDTO<List<ReviewPostResponseDTO>> response =
                new SuccessResponseDTO<>(200, "내가 좋아요한 리뷰 항목을 불러오는데 성공하였습니다.", likedPosts);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getMyWrittenReviewPosts( @RequestHeader("Authorization") String token,
                                                      @RequestParam(value = "sortType", defaultValue = "latest") String sortType) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "유효하지 않은 토큰입니다."));
        }
        String userId = jwtUtil.extractUserId(token.substring(7).trim());

        List<ReviewPostResponseDTO> myPosts = reviewPostService.getMyWrittenReviewPosts(userId, sortType);

        SuccessResponseDTO<List<ReviewPostResponseDTO>> response =
                new SuccessResponseDTO<>(200, "내가 작성한 리뷰 항목을 불러오는데 성공하였습니다.", myPosts);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deleteReviewPost(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(400, "유효하지 않은 토큰입니다."));
        }

        String userId = jwtUtil.extractUserId(token.substring(7).trim());

        // 서비스 호출에서 '권한 부족' 처리를 직접 해줘
        try {
            reviewPostService.deleteReviewPost(postId, userId);

            SuccessResponseDTO<String> response =
                    new SuccessResponseDTO<>(200, "리뷰가 성공적으로 삭제되었습니다.", null);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO(401, "삭제 권한이 없습니다."));  // 여기서 직접 401 처리
        }
    }





}
