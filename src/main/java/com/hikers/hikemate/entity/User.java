package com.hikers.hikemate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter // 자동으로 모든 필드 getter setter 메서드 자동 생성
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String user_id; //사용자 아이디

    @NotBlank
    private String passwd; // 사용자 비밀번호

    @NotBlank
    private String nickname; // 사용자 닉네임

    @Email
    @NotBlank
    private String Email; //사용자 이메일

    public User (){}

    //생성자
    public User(String user_id, String passwd, String nickname, String Email) {
        this.user_id = user_id;
        this.passwd = passwd;
        this.nickname = nickname;
        this.Email = Email;
    }

}
