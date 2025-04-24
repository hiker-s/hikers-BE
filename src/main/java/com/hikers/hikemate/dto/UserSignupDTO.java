package com.hikers.hikemate.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class UserSignupDTO {
    private String user_id;
    private String password;
    private String email;
    private String phone;
}
