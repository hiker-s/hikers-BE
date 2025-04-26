package com.hikers.hikemate.entity;

import jakarta.persistence.*;

public class Mountain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="mntID")
    private Long id;

    private String mntName;

    @Lob
    private String mntInfo;

    // 코스 정보 추가 후 연결
    // private List<Course> courses;

}
