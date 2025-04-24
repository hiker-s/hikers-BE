package com.hikers.hikemate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSignupDTO {

    @NotBlank
    @JsonProperty("user_id")  // 프론트의 user_id -> 백엔드의 userId로 매핑
    private String userId;

    @NotBlank
    @JsonProperty("passwd")
    private String password;

    @NotBlank
    private String nickname;

    @Email
    private String email;


}
