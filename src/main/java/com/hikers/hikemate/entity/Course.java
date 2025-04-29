package com.hikers.hikemate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mnt_id")
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

    @OneToMany(mappedBy = "course")
    private List<Scrap> scraps;
}
