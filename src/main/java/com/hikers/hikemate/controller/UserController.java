package com.hikers.hikemate.controller;


import com.hikers.hikemate.dto.UserSignupDTO;
import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;


    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserSignupDTO signupDTO) {
        User savedUser = userService.registerUser(signupDTO);

        // 응답 데이터 포맷 간단히 구성
        return ResponseEntity.ok().body(
                new SignupResponse(savedUser.getUserId(), savedUser.getNickname(), savedUser.getEmail())
        );
    }

    // 응답 DTO (필요한 필드만 보내기)
    private record SignupResponse(String user_id, String nickname, String email) {}
}
