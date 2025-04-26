package com.hikers.hikemate.controller;

import com.hikers.hikemate.dto.base.ErrorResponseDTO;
import com.hikers.hikemate.dto.base.SuccessResponseDTO;
import com.hikers.hikemate.dto.review.CardReviewDTO;
import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.service.CardReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/mountain/review/{mnt_id}")
public class CardReviewMountainController {

    private final JwtUtil jwtUtil;
    private final CardReviewService courseReviewService;

    @GetMapping()
    public ResponseEntity<?> getReviewListByMountain(@RequestHeader(value = "Authorization", required = false) String token,
                                           @RequestParam(value = "sortType", defaultValue = "latest") String sortType,
                                           @PathVariable Long mnt_id
    ) {
        try {
            User user = jwtUtil.getUserFromToken(token);
            List<CardReviewDTO> reviewPostList = courseReviewService.getReviewListByMountain(sortType, user.getUserId(), mnt_id);

            return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponseDTO<>(
                    200,
                    ("likes".equals(sortType) ? "좋아요순으로 " : "최신순으로 ") + "정렬된 산 별 리뷰 조회에 성공하였습니다.",
                    reviewPostList
            ));

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

