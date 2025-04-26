package com.hikers.hikemate.controller;

import com.hikers.hikemate.dto.StampResponseDTO;
import com.hikers.hikemate.entity.Stamp;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.service.StampService;
import com.hikers.hikemate.dto.base.ErrorResponseDTO;
import com.hikers.hikemate.dto.base.SuccessResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stamp")
public class StampController {

    @Autowired
    private StampService stampService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUserLocation(
            @RequestHeader("Authorization") String token,  // 헤더에서 토큰 추출
            @RequestParam int courseId,
            @RequestParam double userLat,
            @RequestParam double userLng) {

        try {
            // JWT 토큰에서 userId 추출
            String userId = jwtUtil.extractUserId(token);

            // 인증 로직 수행
            stampService.authenticateUserLocation(userId, courseId, userLat, userLng);

            // 인증 성공 시 SuccessResponseDTO 반환
            SuccessResponseDTO<String> successResponse = new SuccessResponseDTO<>(200, "인증 성공", "인증이 완료되었습니다.");
            return new ResponseEntity<>(successResponse, HttpStatus.OK);

        } catch (IllegalArgumentException ex) {
            // 인증 실패 시 에러 메시지 반환
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(400, "인증 실패");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            // 일반적인 예외 처리
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(500, "서버 오류가 발생했습니다.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/mine")
    public ResponseEntity<?> getMyStamps(@RequestHeader("Authorization") String token) {
        try {
            // JWT 토큰에서 userId 추출
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponseDTO(400, "유효하지 않은 토큰입니다."));
            }

            String userId = jwtUtil.extractUserId(token.substring(7).trim());

            // 사용자별 스탬프 목록 조회
            List<StampResponseDTO> stamps = stampService.getStampsByUser(userId).stream()
                    .map(StampResponseDTO::fromEntity)  // 엔티티를 DTO로 변환
                    .collect(Collectors.toList());

            // 스탬프 목록 반환
            return ResponseEntity.ok(stamps);

        } catch (IllegalArgumentException ex) {
            // 사용자 또는 스탬프를 찾을 수 없는 경우
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(404, "사용자 또는 스탬프를 찾을 수 없습니다.");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            // 일반적인 예외 처리
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(500, "서버 오류가 발생했습니다.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
