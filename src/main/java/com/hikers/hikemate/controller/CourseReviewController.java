package com.hikers.hikemate.controller;

import com.hikers.hikemate.dto.ReviewPostResponseDTO;
import com.hikers.hikemate.dto.base.ErrorResponseDTO;
import com.hikers.hikemate.dto.base.SuccessResponseDTO;
import com.hikers.hikemate.dto.review.ReviewCardDTO;
import com.hikers.hikemate.dto.scrap.ScrapsByUserDTO;
import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.service.CourseReviewService;
import com.hikers.hikemate.service.ReviewPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/{course_id}/review")
public class CourseReviewController {

    private final JwtUtil jwtUtil;
    private final CourseReviewService courseReviewService;

    @GetMapping()
    public ResponseEntity<?> getReviewList(@RequestHeader(value = "Authorization", required = false) String token,
                                           @RequestParam(value = "sortType", defaultValue = "latest") String sortType,
                                           @PathVariable Long course_id
    ) {
        try {
            User user = jwtUtil.getUserFromToken(token);
            List<ReviewCardDTO> reviewPostList = courseReviewService.getReviewListByCourse(sortType, user.getUserId());

            return ResponseEntity.status(HttpStatus.OK).body(reviewPostList);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(new ErrorResponseDTO(400, e.getMessage()));
        } catch (IllegalArgumentException e) {
            // 토큰 관련 에러
            return ResponseEntity.status(401).body(new ErrorResponseDTO(401, e.getMessage()));
        } catch (Exception e) {
            // 알 수 없는 서버 에러
            return ResponseEntity.status(500).body(new ErrorResponseDTO(500, e.getMessage()));
        }
    }

}
