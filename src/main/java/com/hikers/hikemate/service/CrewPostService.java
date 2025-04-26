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
    private final ImageService imageService;

    @Autowired
    public CrewPostService(CrewPostRepository crewPostRepository,
                           UserRepository userRepository,
                           JwtUtil jwtUtil,
                           S3Service s3Service,
                           ImageService imageService) {
        this.crewPostRepository = crewPostRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.s3Service = s3Service;
        this.imageService = imageService;
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
                .author(user)
                .build();

        List<Image> imageList = new ArrayList<>(); // 이미지 목록을 저장할 리스트
        for (MultipartFile file : request.getImages()) {
            try {

                String imageUrl = s3Service.uploadImage(file); // 이미지 업로드


                Image image = new Image();
                image.setImageUrl(imageUrl);
                image.setCrewPost(crewPost);
                imageList.add(image); // 이미지 리스트에 추가
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
            }
        }

        crewPost.setImages(imageList);


        return crewPostRepository.save(crewPost);
    }


    public CrewPost getCrewPostById(Long id) {

        Optional<CrewPost> crewPostOptional = crewPostRepository.findById(id);

        if (crewPostOptional.isPresent()) {
            return crewPostOptional.get();
        } else {

            throw new IllegalArgumentException("게시물을 찾을 수 없습니다.");
        }


    }

    //권한 확인 > 이미지 일괄 삭제 > 이미지 일괄 저장 > 나머지 덮어쓰기
    @Transactional
    public CrewPost updateCrewPost(Long postId, User user, CrewPostRequestDTO dto) {
        // 1) 조회
        CrewPost post = crewPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));

        // 2) 권한 체크
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        // 3) 기존 이미지 완전 삭제
        imageService.deleteImagesByPost(post);

        // 4) 새 이미지 저장
        List<Image> saved = imageService.saveImages(dto.getImages(), post);
        post.setImages(saved);

        // 5) 본문+제목 덮어쓰기
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        // 6) 저장 후 반환 (flush & commit)
        return crewPostRepository.save(post);
    }


    public List<CrewPost> getAllCrewPosts() {
        return crewPostRepository.findAll();
    }

    @Transactional
    public void deleteCrewPost(Long postId, User user) {
        CrewPost post = crewPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));


        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }


        imageService.deleteImagesByPost(post);


        crewPostRepository.delete(post);
    }





}
