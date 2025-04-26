package com.hikers.hikemate.dto.mountain;

import com.hikers.hikemate.dto.course.CourseDetailDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MountainGetAllResponseDto {
    private Long id;
    private String mntName;
    private String mntInfo;
    private List<CourseDetailDto> courses;

    public MountainGetAllResponseDto(Long id, String mntName, String mntInfo, List<CourseDetailDto> courses) {
        this.id = id;
        this.mntName = mntName;
        this.mntInfo = mntInfo;
        this.courses = courses;
    }
}
