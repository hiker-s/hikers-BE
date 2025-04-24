package com.hikers.hikemate.service;

import com.hikers.hikemate.dto.LoginRequestDTO;
import com.hikers.hikemate.dto.UserSignupDTO;
import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(UserSignupDTO request) {
        // 1. 중복 검사
        if (userRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 아이디입니다.");

        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 엔티티 생성 및 저장
        User user = User.builder()
                .userId(request.getUserId())
                .passwd(encodedPassword)
                .nickname(request.getNickname())
                .email(request.getEmail())
                .build();

        return userRepository.save(user);
    }

    // 로그인 처리
    public String login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByUserId(loginRequestDTO.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디가 존재하지 않습니다."));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPasswd())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
        }

        return JwtUtil.generateToken(user.getUserId());
    }


    public User findUserByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다: " + userId));
    }
    public boolean deleteUserByUserId(String userId) {
        Optional<User> userOptional = userRepository.findByUserId(userId);

        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
            return true;
        }
        return false;
    }
}