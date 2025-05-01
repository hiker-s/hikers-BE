package com.hikers.hikemate.dto.course;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CourseForListWithNameDTO {
    private int id;
    private Long mountainId;
    private String courseFilePath;
    private String courseName;
}
