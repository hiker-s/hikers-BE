package com.hikers.hikemate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponseDTO {
    private String user_id;
    private String nickname;
    private String email;
}