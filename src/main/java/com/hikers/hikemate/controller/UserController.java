package com.hikers.hikemate.controller;


import com.hikers.hikemate.dto.LoginRequestDTO;
import com.hikers.hikemate.dto.LoginResponseDTO;
import com.hikers.hikemate.dto.SignupResponseDTO;
import com.hikers.hikemate.dto.UserSignupDTO;
import com.hikers.hikemate.dto.base.ErrorResponseDTO;
import com.hikers.hikemate.dto.base.SuccessResponseDTO;
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

        if (savedUser == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "409 CONFLICT");  // 중복된 사용자 ID
            response.put("message", "이미 사용 중인 아이디입니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        SignupResponseDTO response = new SignupResponseDTO(
                savedUser.getUserId(),
                savedUser.getNickname(),
                savedUser.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 응답 DTO (필요한 필드만 보내기)
    private record SignupResponse(String user_id, String nickname, String email) {}
    // 로그인

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {

        String result = userService.login(loginRequestDTO);

        // 로그인 실패 시
        if (result.equals("ID_NOT_FOUND")) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "400 BAD REQUEST");
            response.put("message", "존재하지 않는 아이디입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (result.equals("INVALID_PASSWORD")) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "400 BAD REQUEST");
            response.put("message", "비밀번호가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // JWT 토큰 반환
        return ResponseEntity.ok().body(new LoginResponseDTO(result));
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
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO(401, "유효하지 않은 토큰입니다."));
        }
        String userId = jwtUtil.extractUserId(token.substring(7).trim());

        User user = userService.findUserByUserId(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "사용자를 찾을 수 없습니다."));
        }

        Map<String, String> profile = Map.of(
                "userId", user.getUserId(),
                "nickname", user.getNickname()
        );

        return ResponseEntity.ok(
                new SuccessResponseDTO<>(200, "프로필 정보를 불러오는데 성공했습니다.", profile)
        );
    }



}
