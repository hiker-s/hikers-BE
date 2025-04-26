package com.hikers.hikemate.dto.scrap;

import com.hikers.hikemate.dto.course.CourseDetailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ScrapDTO {
    private Long scrap_id;
    private CourseDetailDto course;
}
