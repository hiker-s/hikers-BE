package com.hikers.hikemate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hikers.hikemate.entity.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CrewPostResponseDTO {
    private Long id;
    private String authorName;
    private String title;
    private String content;
    private List<String> imageUrls;
    private String createdAt;

    // 생성자 수정: authorName을 user의 nickname으로 설정
    public CrewPostResponseDTO(Long id, User author, String title, String content, List<String> imageUrls, LocalDateTime createdAt) {
        this.id = id;
        this.authorName = author.getNickname(); // User에서 nickname 가져오기
        this.title = title;
        this.content = content;
        this.imageUrls = imageUrls;

        // createdAt을 원하는 형식으로 포맷
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.createdAt = createdAt.format(formatter);
    }

    // Getter와 Setter 추가 (선택 사항)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
