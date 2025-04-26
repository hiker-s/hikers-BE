package com.hikers.hikemate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class ReviewPostRequestDTO {

    private String title;
    private String content;
    private List<MultipartFile> images;
    private String level;
    private Long courseId;

    public List<MultipartFile> getImages() {
        return Optional.ofNullable(images).orElse(new ArrayList<>());
    }

}
