package com.hikers.hikemate.controller;

import com.hikers.hikemate.dto.base.ErrorResponseDTO;
import com.hikers.hikemate.dto.base.SuccessResponseDTO;
import com.hikers.hikemate.dto.course.CourseDetailDto;
import com.hikers.hikemate.dto.course.CourseDetailWithScrapDTO;
import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseController {

    private final JwtUtil jwtUtil;
    private final CourseService courseService;

    @GetMapping("{mnt_id}")
    public ResponseEntity<?> getReviewList(@RequestHeader(value = "Authorization", required = false) String token,
                                           @RequestParam(value = "sortType", defaultValue = "abc") String sortType,
                                           @PathVariable Long mnt_id
    ) {
        try {
            User user = jwtUtil.getUserFromToken(token);

            List<CourseDetailWithScrapDTO> courseDetailDtoList = courseService.getCourseBySort(sortType, mnt_id, user.getId());


            return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponseDTO<>(
                    200,
                    switch (sortType) {
                        case "abc" -> "가나다순으로 정렬된 코스 조회에 성공하였습니다.";
                        case "level" -> "난이도순으로 정렬된 코스 조회에 성공하였습니다.";
                        case "review" -> "리뷰순으로 정렬된 코스 조회에 성공하였습니다.";
                        case "scrap" -> "스크랩순으로 정렬된 코스 조회에 성공하였습니다.";
                        default -> "가나다순으로 정렬된 코스 조회에 성공하였습니다.";
                    },
                    courseDetailDtoList
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(new ErrorResponseDTO(400, e.getMessage()));
        } catch (IllegalArgumentException e) {
            // 토큰 관련 에러
            return ResponseEntity.status(401).body(new ErrorResponseDTO(401, e.getMessage()));
        } catch (Exception e) {
            // 알 수 없는 서버 에러
            return ResponseEntity.status(500).body(new ErrorResponseDTO(500,  e.getMessage()));
        }
    }
}