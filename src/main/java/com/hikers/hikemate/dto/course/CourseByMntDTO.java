package com.hikers.hikemate.dto.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CourseByMntDTO {
    private CourseDetailDto course;
    @JsonProperty("is_scrapped")
    private boolean isScrapped;
}


