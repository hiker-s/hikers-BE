package com.hikers.hikemate.dto.scrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hikers.hikemate.dto.course.CourseDetailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScrapDTO {
    private Long scrap_id;
    private CourseDetailDto course;
    @JsonProperty("is_scrapped")
    private boolean isScrapped;

    public ScrapDTO(Long id, CourseDetailDto courseDetail) {
        this.scrap_id = id;
        this.course = courseDetail;
        this.isScrapped = true;
    }

    // 새로운 생성자
    public ScrapDTO(Long id, CourseDetailDto courseDetail, boolean isScrapped) {
        this.scrap_id = id;
        this.course = courseDetail;
        this.isScrapped = isScrapped;
    }
}
