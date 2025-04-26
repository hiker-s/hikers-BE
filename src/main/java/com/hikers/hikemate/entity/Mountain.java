package com.hikers.hikemate.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

public class Mountain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mntName;

    @Lob
    private String mntInfo;

    // 코스 정보 추가 후 연결
    // private List<Course> courses;

}
