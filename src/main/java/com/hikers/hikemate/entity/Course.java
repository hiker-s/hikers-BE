package com.hikers.hikemate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;

public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="courseID")
    private int id;

    private String courseFilePath;

    @Column(precision = 10, scale = 5)
    private BigDecimal courseLastLan;

    @Column(precision = 10, scale = 5)
    private BigDecimal courseLatLon;
}
