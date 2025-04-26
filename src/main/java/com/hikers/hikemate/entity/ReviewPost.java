package com.hikers.hikemate.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class ReviewPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reviewId")
    private int id;


    private String title;

    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @Lob
    private String content;

    //이미지 객체와의 연관관계
    @OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL)
    private List<Image> images;

    //코스와의 연관관계 > 여기서 해당 하는 코스를 입력하면 자동으로 산으로까지 이어질텐데 굳이 필요한가?
    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    private String level;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // like entity 만들고 나서 작성
    // private int likedCnt;
}
