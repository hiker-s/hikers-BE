package com.hikers.hikemate.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardReviewDTO {
    private int id;
    private String title;
    private String level;
    private String description;

    @JsonProperty("imgUrl")
    private String imgUrl;

    @JsonProperty("isLiked")
    private boolean isLiked;
}
