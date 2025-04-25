package com.hikers.hikemate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hikers.hikemate.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public class CrewPostDetailResponseDTO extends CrewPostResponseDTO {

    @JsonProperty("is_writer")
    private boolean isWriter;

    public CrewPostDetailResponseDTO(Long id, User author, String title, String content,
                                     List<String> imageUrls, LocalDateTime createdAt, boolean isWriter) {
        super(id, author, title, content, imageUrls, createdAt);
        this.isWriter = isWriter;
    }

    @JsonProperty("is_writer")
    public boolean getisWriter() {
        return isWriter;
    }

    @JsonProperty("is_writer")
    public void setWriter(boolean writer) {
        isWriter = writer;
    }
}
