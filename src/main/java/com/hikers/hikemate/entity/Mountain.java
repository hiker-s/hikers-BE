package com.hikers.hikemate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Mountain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="mntId")
    private Long id;

    private String mntName;

    @Lob
    private String mntInfo;

    // 코스 정보 추가 후 연결
    @OneToMany(mappedBy = "mountain")
    private List<Course> courses;


}
