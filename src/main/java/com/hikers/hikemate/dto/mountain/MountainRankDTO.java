package com.hikers.hikemate.dto.mountain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MountainRankDTO {
    private Long id;
    private Integer rank;
    private String mountainName;
    private Integer viewCount;
}
