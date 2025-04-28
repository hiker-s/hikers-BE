package com.hikers.hikemate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 리뷰 게시글 응답용 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewPostResponseDTO {
    private int id;
    private String authorName;
    private String title;
    private String content;
    private String level;
    private String courseName;
    private String mountainName;
    private List<String> imageUrls;
    @JsonProperty("is_writer")
    private boolean isWriter;

    private int likeCount;
    private boolean likedByCurrentUser;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDateTime createdAt;
}
