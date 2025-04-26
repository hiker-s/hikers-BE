package com.hikers.hikemate.controller;


import com.hikers.hikemate.dto.CrewPostDetailResponseDTO;
import com.hikers.hikemate.dto.CrewPostRequestDTO;
import com.hikers.hikemate.dto.CrewPostResponseDTO;
import com.hikers.hikemate.dto.base.ErrorResponseDTO;
import com.hikers.hikemate.dto.base.SuccessResponseDTO;
import com.hikers.hikemate.entity.CrewPost;
import com.hikers.hikemate.entity.Image;
import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.service.CrewPostService;
import com.hikers.hikemate.service.ImageService;
import com.hikers.hikemate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private final ImageService imageService;

    @Autowired
    public CrewPostController(CrewPostService crewPostService, UserService userService, JwtUtil jwtUtil, ImageService imageService) {
        this.crewPostService = crewPostService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.imageService = imageService;
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
                    createdPost.getTitle(),
                    createdPost.getContent(),
                    imageUrls,
                    createdPost.getCreatedAt() // createdAt 포함
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } else {
            return ResponseEntity.status(500).body("게시물 생성에 실패하였습니다.");
        }


    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCrewPost(@RequestHeader("Authorization") String token, @PathVariable("id") Long id) {

        CrewPost post = crewPostService.getCrewPostById(id);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");
        }

        String userId = null;
        String pureToken;
        boolean isWriter = false;

        if (token != null && token.startsWith("Bearer ")) {
            pureToken = token.substring(7).trim();
            userId = JwtUtil.extractUserId(pureToken);
            User user = userService.findUserByUserId(userId);

            if (user != null && user.getId().equals(post.getAuthor().getId())) {
                isWriter = true;
            }
        }

        List<String> imageUrls = post.getImages().stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        CrewPostDetailResponseDTO responseDTO = new CrewPostDetailResponseDTO(
                post.getId(),
                post.getAuthor(),
                post.getTitle(),
                post.getContent(),
                imageUrls,
                post.getCreatedAt(),
                isWriter
        );

        return ResponseEntity.ok(responseDTO);
    }

    //악 미친 꼬였다. 서비스에다가 넘겨줘야지
    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> updateCrewPost(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") Long postId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) {

        if (!token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid token format.");
        }
        String userId = JwtUtil.extractUserId(token.substring(7).trim());
        User user = userService.findUserByUserId(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }


        CrewPostRequestDTO dto = new CrewPostRequestDTO(title, content, images);

        // !!!!서비스에 위임 (이 한 줄로 내부에서 이미지 삭제→저장, 본문 수정 모두 처리하도록)
        CrewPost updatedPost;
        try {
            updatedPost = crewPostService.updateCrewPost(postId, user, dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }

        //통합하기
        List<String> imageUrls = updatedPost.getImages().stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        CrewPostResponseDTO response = new CrewPostResponseDTO(
                updatedPost.getId(),
                updatedPost.getAuthor(),
                updatedPost.getTitle(),
                updatedPost.getContent(),
                imageUrls,
                updatedPost.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllCrewPosts() {
        List<CrewPost> posts = crewPostService.getAllCrewPosts();

        if (posts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("게시물이 없습니다.");
        }

        // 게시물 목록을 ResponseDTO 형식으로 변환
        List<CrewPostResponseDTO> responseDTOs = posts.stream()
                .map(post -> {
                    List<String> imageUrls = post.getImages().stream()
                            .map(Image::getImageUrl)
                            .collect(Collectors.toList());
                    return new CrewPostResponseDTO(
                            post.getId(),
                            post.getAuthor(),
                            post.getTitle(),
                            post.getContent(),
                            imageUrls,
                            post.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCrewPost(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") Long postId
    ) {
        if (!token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(400, "유효하지 않은 토큰 형식입니다."));
        }

        String userId = JwtUtil.extractUserId(token.substring(7).trim());
        User user = userService.findUserByUserId(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(404, "사용자를 찾을 수 없습니다."));
        }

        try {
            // 서비스에 삭제 위임
            crewPostService.deleteCrewPost(postId, user);
            // 응답 메시지와 함께 200 OK 상태 코드 반환
            return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponseDTO<>(200, "게시물이 성공적으로 삭제되었습니다.", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseDTO(403, "삭제 권한이 없습니다."));
        }
    }


}
