package com.hikers.hikemate.controller;

import com.hikers.hikemate.common.ScrapSortType;
import com.hikers.hikemate.dto.UserIdNickNameDto;
import com.hikers.hikemate.dto.base.SuccessResponseDTO;
import com.hikers.hikemate.dto.course.CourseDetailDto;
import com.hikers.hikemate.dto.base.ErrorResponseDTO;
import com.hikers.hikemate.dto.scrap.ScrapDTO;
import com.hikers.hikemate.dto.scrap.ScrapResponseDto;
import com.hikers.hikemate.dto.scrap.ScrapsByUserDTO;
import com.hikers.hikemate.entity.Course;
import com.hikers.hikemate.entity.Scrap;
import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.service.CourseService;
import com.hikers.hikemate.service.ScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scrap")
public class ScrapController {

    private final ScrapService scrapService;
    private final CourseService courseService;
    private final JwtUtil jwtUtil;

    @PostMapping(produces = "application/json")
    public ResponseEntity<?> scrapPost(
            @RequestHeader("Authorization") String token,
            @RequestParam("course_id") Integer course_id
    ) {
        try {
            User user = jwtUtil.getUserFromToken(token);
            Course course = courseService.findCourseById(course_id);
            Scrap scrap = scrapService.postScrap(user, course);

            SuccessResponseDTO<ScrapResponseDto> successResponseDTO = getScrapResponseDtoAsSuccessResponseDTO(scrap, user, course, "스크랩하기에 성공하였습니다.");

            return ResponseEntity.ok(successResponseDTO);

        } catch (IllegalStateException e) {
            // 스크랩 중복 에러
            return ResponseEntity.status(400).body(new ErrorResponseDTO(400, e.getMessage()));
        } catch (IllegalArgumentException e) {
            // 토큰 관련 에러
            return ResponseEntity.status(401).body(new ErrorResponseDTO(401, e.getMessage()));
        } catch (Exception e) {
            // 알 수 없는 서버 에러
            return ResponseEntity.status(500).body(new ErrorResponseDTO(500, "서버 내부 오류가 발생했습니다."));
        }
    }

    @DeleteMapping(produces = "application/json")
    public ResponseEntity<?> scrapDelete(
            @RequestHeader("Authorization") String token,
            @RequestParam("course_id") Integer course_id
    ) {
        try {
            User user = jwtUtil.getUserFromToken(token);
            Course course = courseService.findCourseById(course_id);
            scrapService.deleteScrap(user, course);

            return ResponseEntity.ok(new SuccessResponseDTO<>(200, "스크랩을 취소하였습니다.", null));

        } catch (IllegalStateException e) {
            // 스크랩 하지 않았을 때 에러
            return ResponseEntity.status(400).body(new ErrorResponseDTO(400, e.getMessage()));
        } catch (IllegalArgumentException e) {
            // 토큰 관련 에러
            return ResponseEntity.status(401).body(new ErrorResponseDTO(401, e.getMessage()));
        } catch (Exception e) {
            // 알 수 없는 서버 에러
            return ResponseEntity.status(500).body(new ErrorResponseDTO(500, "서버 내부 오류가 발생했습니다."));
        }
    }

    /*@GetMapping(value="user", produces = "application/json")
    public ResponseEntity<?> scrapGetByUser(
            @RequestHeader("Authorization") String token
    ) {
        try {
            User user = jwtUtil.getUserFromToken(token);
            ScrapsByUserDTO scrapsList = scrapService.getScrapByUser(user);


            SuccessResponseDTO<ScrapsByUserDTO> successResponseDTO = new SuccessResponseDTO<>(
                    200,
                    "내가 스크랩한 코스 목록 반환에 성공하였습니다.",
                    scrapsList
            );


            return ResponseEntity.ok(successResponseDTO);

        } catch (IllegalStateException e) {
            // 스크랩 하지 않았을 때 에러
            return ResponseEntity.status(400).body(new ErrorResponseDTO(400, e.getMessage()));
        } catch (IllegalArgumentException e) {
            // 토큰 관련 에러
            return ResponseEntity.status(401).body(new ErrorResponseDTO(401, e.getMessage()));
        } catch (Exception e) {
            // 알 수 없는 서버 에러
            return ResponseEntity.status(500).body(new ErrorResponseDTO(500, "서버 내부 오류가 발생했습니다."));
        }
    }*/

    @GetMapping(value = "user", produces = "application/json")
    public ResponseEntity<?> scrapGetByUser(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "NAME") ScrapSortType sortBy
    ) {
        try {
            User user = jwtUtil.getUserFromToken(token);
            ScrapsByUserDTO scrapsList = scrapService.getScrapByUser(user, sortBy);

            SuccessResponseDTO<ScrapsByUserDTO> successResponseDTO = new SuccessResponseDTO<>(
                    200,
                    "내가 스크랩한 코스 목록 반환에 성공하였습니다.",
                    scrapsList
            );
            return ResponseEntity.ok(successResponseDTO);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(new ErrorResponseDTO(400, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(new ErrorResponseDTO(401, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponseDTO(500, "서버 내부 오류가 발생했습니다."));
        }
    }


    private static SuccessResponseDTO<ScrapResponseDto> getScrapResponseDtoAsSuccessResponseDTO(Scrap scrap, User user, Course course, String message) {
        UserIdNickNameDto userDto = new UserIdNickNameDto(user.getUserId(), user.getNickname());
        CourseDetailDto courseDto = new CourseDetailDto(
                course.getId(),
                course.getCourseFilePath(),
                course.getCourseName(),
                course.getStartName(),
                course.getEndName(),
                course.getLevel(),
                course.getTime(),
                course.getMountain().getId()
        );
        ScrapDTO scrapDTO = new ScrapDTO(scrap.getId(), courseDto);

        SuccessResponseDTO<ScrapResponseDto> successResponseDTO = new SuccessResponseDTO<>(
                200,
                message,
                new ScrapResponseDto(userDto, scrapDTO)
        );
        return successResponseDTO;
    }
}