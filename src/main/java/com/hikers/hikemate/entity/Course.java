package com.hikers.hikemate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="courseId")
    private int id;

    @ManyToOne
    @JoinColumn(name = "mntId")
    private Mountain mountain;

    private String courseFilePath;

    private String courseName;

    @Column(precision = 12, scale = 6)
    private BigDecimal courseLastLat;

    @Column(precision = 12, scale = 6)
    private BigDecimal courseLastLng;

    private String startName;
    private String endName;
    private String level;
    private String time;

    @OneToMany(mappedBy = "course")
    private List<ReviewPost> reviews;
}
