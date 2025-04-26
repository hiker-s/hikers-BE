package com.hikers.hikemate.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Null;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCardDTO {
    private int id;
    private String title;
    private String level;
    private String description;

    @JsonProperty("imgUrl")
    private String imgUrl;

    @JsonProperty("isLiked")
    private boolean isLiked;
}
