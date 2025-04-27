package com.hikers.hikemate.controller;

import com.hikers.hikemate.dto.base.SuccessResponseDTO;
import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.jwt.JwtUtil;
import com.hikers.hikemate.repository.UserRepository;
import com.hikers.hikemate.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rank")
public class RankingController {

    private final RankService rankService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public RankingController(RankService rankService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.rankService = rankService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // 랭킹을 반환
    @GetMapping
    public SuccessResponseDTO<Map<String, Object>> getRanking(@RequestHeader("Authorization") String token) {

        User currentUser = jwtUtil.getUserFromToken(token);
        List<User> users = userRepository.findAll();

        // 랭킹 계산
        List<Map<String, Object>> ranking = rankService.getRanking(users);

        // 현재 사용자의 랭킹 정보 계산
        int currentUserRank = -1;
        for (int i = 0; i < ranking.size(); i++) {
            Map<String, Object> rankInfo = ranking.get(i);
            Long userId = (Long) rankInfo.get("id");
            if (userId.equals(currentUser.getId())) {
                currentUserRank = i + 1; // 랭킹은 1부터 시작하므로!!!!!! 인덱스 + 1
                break;
            }
        }

        // 랭킹 응답에 현재 사용자의 랭킹도 추가
        Map<String, Object> myRank = Map.of(
                "id", currentUser.getId(),
                "rank", currentUserRank,
                "name", currentUser.getNickname(),
                "stamp", currentUser.getStamps().size()
        );

        // 랭킹 데이터와 현재 사용자 랭킹을 포함하여 응답 내놓기
        return new SuccessResponseDTO<>(200, "랭킹을 불러오는데 성공하였습니다.", Map.of(
                "myRank", myRank,
                "ranker", ranking
        ));
    }
}
