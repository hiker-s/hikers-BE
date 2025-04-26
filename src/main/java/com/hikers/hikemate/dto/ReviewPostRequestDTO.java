package com.hikers.hikemate.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ReviewPostRequestDTO {

    private String title;
    private String content;
    private List<MultipartFile> images;
    private String level;
    private

    public ReviewPostRequestDTO(String title, String content, List<MultipartFile> images) {}


}
