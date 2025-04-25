package com.hikers.hikemate.service;

import com.hikers.hikemate.dto.CrewPostRequestDTO;
import com.hikers.hikemate.entity.CrewPost;
import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.entity.Image;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.repository.CrewPostRepository;
import com.hikers.hikemate.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class CrewPostService {

    private final CrewPostRepository crewPostRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final S3Service s3Service;

    @Autowired
    public CrewPostService(CrewPostRepository crewPostRepository,
                           UserRepository userRepository,
                           JwtUtil jwtUtil,
                           S3Service s3Service) {
        this.crewPostRepository = crewPostRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.s3Service = s3Service;
    }

    @Transactional
    public CrewPost createCrewPost(String token, CrewPostRequestDTO request) {
        // JWT 토큰에서 사용자 ID 추출
        String userId = jwtUtil.extractUserId(token);


        // 사용자 정보 조회
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("작성자가 존재하지 않습니다.");
        }
        User user = userOptional.get();
        CrewPost crewPost = CrewPost.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(user)  // 작성자 정보 설정
                .build();

        List<Image> imageList = new ArrayList<>(); // 이미지 목록을 저장할 리스트
        for (MultipartFile file : request.getImages()) {
            try {
                // S3에 파일 업로드 후 URL 반환
                String imageUrl = s3Service.uploadImage(file); // 이미지 업로드

                // Image 엔티티 생성
                Image image = new Image();
                image.setImageUrl(imageUrl);
                image.setCrewPost(crewPost);
                imageList.add(image); // 이미지 리스트에 추가
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e); // 예외 처리
            }
        }

        crewPost.setImages(imageList);

        // CrewPost 저장
        return crewPostRepository.save(crewPost);
    }
}
