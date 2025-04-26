package com.hikers.hikemate.dto.mountain;

import com.hikers.hikemate.dto.course.CourseDetailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MountainGetAllResponseDto {
    private Long id;
    private String mntName;
    private String mntInfo;
    private List<CourseDetailDto> courses;
}
