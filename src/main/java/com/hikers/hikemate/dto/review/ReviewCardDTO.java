package com.hikers.hikemate.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCardDTO {
    private int id;
    private String title;
    private String content;
    private String level;
    private boolean isLiked;
}
