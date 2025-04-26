package com.hikers.hikemate.dto.mountain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MountainNameDTO {
    private Long id;
    private String mntName;
    private Integer viewCount;
}
