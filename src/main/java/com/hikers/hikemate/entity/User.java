package com.hikers.hikemate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter // 자동으로 모든 필드 getter setter 메서드 자동 생성
@Entity
@Builder
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @NotEmpty(message = "Password 는 빈칸일 수 없음")
    private String passwd;

    @NotEmpty(message = "Nickname 은 빈칸일 수 없음")
    private String nickname;

    @Email
    @NotEmpty(message = "Email 은 빈칸일 수 없음")
    private String email;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CrewPost> crewPosts;

    //오늘 집가서 랭크 관련 필드 만들어는 놔야함

    //추후 도장 개발시 도장에 대한 list 객체 추가 필요

    //리뷰와의 연결 필요
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewPost> reviewPosts;

    // 코스 스크랩, 양방향 매핑(내가 스크랩한 코스를 보여주기 위함)
    @OneToMany(mappedBy = "user")
    private List<Scrap> scraps;

}
