package com.hikers.hikemate.dto.course;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseDetailDto {

    private int id;
    private String courseFilePath;
    private String courseName;

    public CourseDetailDto(int id, String courseFilePath, String courseName) {
        this.id = id;
        this.courseFilePath = courseFilePath;
        this.courseName = courseName;
    }
}
