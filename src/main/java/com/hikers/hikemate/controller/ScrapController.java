package com.hikers.hikemate.controller;

import com.hikers.hikemate.dto.UserIdNickNameDto;
import com.hikers.hikemate.dto.base.SuccessResponseDTO;
import com.hikers.hikemate.dto.course.CourseDetailDto;
import com.hikers.hikemate.dto.base.ErrorResponseDTO;
import com.hikers.hikemate.dto.scrap.ScrapDTO;
import com.hikers.hikemate.dto.scrap.ScrapResponseDto;
import com.hikers.hikemate.entity.Course;
import com.hikers.hikemate.entity.Scrap;
import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.service.CourseService;
import com.hikers.hikemate.service.ScrapService;
import com.hikers.hikemate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scrap")
public class ScrapController {

    private final ScrapService scrapService;
    private final UserService userService;
    private final CourseService courseService;
    private final JwtUtil jwtUtil;

    @PostMapping(produces = "application/json")
    public ResponseEntity<?> scrapPost(
            @RequestHeader("Authorization") String token,
            @RequestParam("course_id") Long course_id
    ) {
        try {
            // JWT 토큰을 통해 사용자 정보 추출
            String userId;
            String pureToken;
            if (token.startsWith("Bearer ")) {
                pureToken = token.substring(7).trim();
                try {
                    userId = jwtUtil.extractUserId(pureToken);
                } catch (Exception e) {
                    // 토큰 파싱 실패
                    return ResponseEntity.status(401).body(new ErrorResponseDTO(401, "유효하지 않은 토큰 입니다."));
                }
            } else {
                return ResponseEntity.status(401).body(new ErrorResponseDTO(401, "유효하지 않은 토큰 형식입니다."));
            }

            User user = userService.findUserByUserId(userId);
            Course course = courseService.findCourseById(course_id);

            Scrap scrap = scrapService.postScrap(user, course);

            ScrapDTO scrapDTO = new ScrapDTO(scrap.getId());
            UserIdNickNameDto userDto = new UserIdNickNameDto(user.getUserId(), user.getNickname());
            CourseDetailDto courseDto = new CourseDetailDto(course.getId(), course.getCourseFilePath(), course.getCourseName());

            SuccessResponseDTO<ScrapResponseDto> successResponseDTO = new SuccessResponseDTO<>(
                    200,
                    "스크랩하기에 성공하였습니다.",
                    new ScrapResponseDto(scrapDTO, userDto, courseDto)
            );

            return ResponseEntity.ok(successResponseDTO);

        } catch (IllegalStateException e) {
            // 스크랩 중복 에러
            return ResponseEntity.status(400).body(new ErrorResponseDTO(400, e.getMessage()));
        } catch (Exception e) {
            // 알 수 없는 서버 에러
            return ResponseEntity.status(500).body(new ErrorResponseDTO(500, "서버 내부 오류가 발생했습니다."));
        }
    }
}