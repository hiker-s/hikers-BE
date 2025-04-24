package com.hikers.hikemate.controller;


import com.hikers.hikemate.dto.LoginRequestDTO;
import com.hikers.hikemate.dto.LoginResponseDTO;
import com.hikers.hikemate.dto.UserSignupDTO;
import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.hikers.hikemate.jwt.JwtUtil;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;


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
    // 로그인

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {

        String token = userService.login(loginRequestDTO);

        // JWT 토큰 반환
        return ResponseEntity.ok().body(new LoginResponseDTO(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        // 응답 객체에 로그아웃 성공 메시지와 사인 추가
        Map<String, Object> response = new HashMap<>();
        response.put("status", "200 OK");  // 사인
        response.put("message", "로그아웃 성공. JWT 토큰을 삭제가 필요합니당.");

        // OK 응답과 함께 응답 객체 반환
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");

        if (!JwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        String userId = JwtUtil.extractUserId(jwt);  // 여기서 "test123" 같은 문자열 userId가 나옴

        boolean isDeleted = userService.deleteUserByUserId(userId);

        if (isDeleted) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "200 OK");
            response.put("message", "회원 탈퇴 성공");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "400 BAD REQUEST");
            response.put("message", "회원 탈퇴 실패");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }

}
