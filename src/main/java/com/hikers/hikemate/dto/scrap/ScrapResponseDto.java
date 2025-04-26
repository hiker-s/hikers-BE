package com.hikers.hikemate.dto.scrap;

import com.hikers.hikemate.dto.UserIdNickNameDto;
import com.hikers.hikemate.dto.course.CourseDetailDto;
import com.hikers.hikemate.entity.Course;
import com.hikers.hikemate.entity.Scrap;
import com.hikers.hikemate.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ScrapResponseDto {

    private ScrapDTO scrap;
    private UserIdNickNameDto user;
    private CourseDetailDto course;
}
