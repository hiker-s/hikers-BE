package com.hikers.hikemate.controller;


import com.hikers.hikemate.dto.CrewPostRequestDTO;
import com.hikers.hikemate.dto.CrewPostResponseDTO;
import com.hikers.hikemate.entity.CrewPost;
import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.service.CrewPostService;
import com.hikers.hikemate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/crewpost")
public class CrewPostController {

    private final CrewPostService crewPostService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public CrewPostController(CrewPostService crewPostService, UserService userService, JwtUtil jwtUtil) {
        this.crewPostService = crewPostService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // 게시물 생성 API
    @PostMapping(produces = "application/json")
    public ResponseEntity<?> createCrewPost(@RequestHeader("Authorization") String token,
                                                 @RequestParam("title") String title,
                                                 @RequestParam("content") String content,
                                                 @RequestParam(value = "images" , required = false) List<MultipartFile> images) {
        String userId;
        String pureToken;
        // JWT 토큰을 통해 사용자 정보 추출
        if (token.startsWith("Bearer ")) {
            pureToken = token.substring(7).trim();
            userId = JwtUtil.extractUserId(pureToken);
        } else {
            return ResponseEntity.status(400).body("Invalid token format.");
        }

        User user = userService.findUserByUserId(userId);

        // CrewPostRequestDTO 생성
        CrewPostRequestDTO requestDTO = new CrewPostRequestDTO(title, content, images);

        // 게시물 생성
        CrewPost createdPost = crewPostService.createCrewPost(token, requestDTO);

        if (createdPost != null) {
            List<String> imageUrls = createdPost.getImages().stream()
                    .map(image -> image.getImageUrl()) // 이미지 URL 가져오기
                    .collect(Collectors.toList());

            // ResponseDTO 생성
            CrewPostResponseDTO responseDTO = new CrewPostResponseDTO(
                    createdPost.getId(),
                    createdPost.getAuthor(),
                    createdPost.getContent(),
                    imageUrls,
                    createdPost.getCreatedAt() // createdAt 포함
            );
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.status(500).body("게시물 생성에 실패하였습니다.");
        }
    }

}
