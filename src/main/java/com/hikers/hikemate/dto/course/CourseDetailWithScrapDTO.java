package com.hikers.hikemate.dto.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CourseDetailWithScrapDTO {
    private int id;
    private String courseFilePath;
    private String courseName;
    private String startName;
    private String endName;
    private String level;
    private String time;
    private Long mountainId;

    @JsonProperty("is_scrapped")
    private boolean isScrapped;
}
