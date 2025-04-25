package com.hikers.hikemate.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CrewPostRequestDTO {

    private String title;
    private String content;
    private List<MultipartFile> images;

    public CrewPostRequestDTO(String title, String content, List<MultipartFile> images) {
        this.title = title;
        this.content = content;
        this.images = (images != null) ? images : new ArrayList<>();
    }

}
