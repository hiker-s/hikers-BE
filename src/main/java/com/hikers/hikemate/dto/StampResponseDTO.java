package com.hikers.hikemate.dto;

import com.hikers.hikemate.entity.Stamp;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class StampResponseDTO {
    private Long stampId;          // 스탬프 ID
    private String courseName;     // 코스 이름
    private BigDecimal courseLat;  // 코스 위도
    private BigDecimal courseLng;  // 코스 경도
    private int levelWeight;       // 레벨 가중치
    private String mountainName;

    // Stamp 엔티티를 받아서 DTO로 변환하는 메서드
    public static StampResponseDTO fromEntity(Stamp stamp) {
        return new StampResponseDTO(
                stamp.getId(),
                stamp.getCourse().getCourseName(),
                stamp.getCourse().getCourseLastLat(),
                stamp.getCourse().getCourseLastLng(),
                stamp.getLevelWeight(),
                stamp.getCourse().getMountain().getMntName()
        );
    }
}