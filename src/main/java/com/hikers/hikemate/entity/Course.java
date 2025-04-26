package com.hikers.hikemate.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="courseId")
    private int id;

    @ManyToOne
    @JoinColumn(name = "mntId")
    private Mountain mountain;

    private String courseFilePath;

    @Column(precision = 10, scale = 5)
    private BigDecimal courseLastLat;

    @Column(precision = 10, scale = 5)
    private BigDecimal courseLatLng;
}
