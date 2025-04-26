package com.hikers.hikemate.controller;
import com.hikers.hikemate.dto.ReviewPostRequestDTO;
import com.hikers.hikemate.dto.ReviewPostResponseDTO;
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

import java.util.List;
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
                                              @RequestParam("courseId") Long courseId,
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
    public ResponseEntity<?> getReviewPost(@RequestHeader("Authorization") String token,
                                          @PathVariable Long postId) {

        // JWT 토큰에서 사용자 ID 추출
        if (!token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String pureToken = token.substring(7).trim();
        String userId = jwtUtil.extractUserId(pureToken);

        // ReviewPostService에서 상세조회 처리
        ReviewPostResponseDTO reviewPostResponseDTO = reviewPostService.getReviewPostById(postId, userId);

        // 해당 게시물이 없을 경우 404 반환
        if (reviewPostResponseDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");
        }

        // 조회된 게시물 반환
        return ResponseEntity.status(HttpStatus.OK).body(reviewPostResponseDTO);
    }

}
