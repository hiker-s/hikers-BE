package com.hikers.hikemate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hikers.hikemate.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
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

}
