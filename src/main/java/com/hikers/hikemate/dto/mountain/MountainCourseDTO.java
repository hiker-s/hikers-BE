package com.hikers.hikemate.dto.mountain;

import com.hikers.hikemate.dto.course.CourseFilePathDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MountainCourseDTO {
    private Long id;
    private String mntName;
    private Integer viewCount;
    private List<CourseFilePathDTO> courses;
}
