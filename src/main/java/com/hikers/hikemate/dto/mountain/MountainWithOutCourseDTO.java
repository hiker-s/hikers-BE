package com.hikers.hikemate.dto.mountain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MountainWithOutCourseDTO {
    private Long id;
    private String mntName;
    private String mntInfo;
    private Integer viewCount;
}
