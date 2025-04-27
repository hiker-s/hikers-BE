package com.hikers.hikemate.service;
import com.hikers.hikemate.entity.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hikers.hikemate.entity.Stamp;


public class RankService {

    //랭킹 계산하기
    public static List<Map<String, Object>> getRanking(List<User> users) {
        // 1. 각 사용자의 총 점수를 계산 -> 스탬프 수를 포함하여 리스트로 저장
        List<Map<String, Object>> rankList = new ArrayList<>();

        for (User user : users) {
            int totalStampScore = user.getStamps().stream()
                    .mapToInt(Stamp::getLevelWeight)
                    .sum();
            int stampCount = user.getStamps().size();

            // 2. 계산된 점수, 스탬프 개수, 사용자 정보를 map으로 저장
            Map<String, Object> userRank = new HashMap<>();
            userRank.put("id", user.getId());
            userRank.put("name", user.getNickname());
            userRank.put("stampCount", stampCount);
            userRank.put("totalStampScore", totalStampScore);
            rankList.add(userRank);
        }

        // 3. 스탬프 수 기준 내림차순, 그 후 스탬프 총점 기준 내림차순으로 정렬 (기능명세서 기준으로 일단 구현함 -> 물어보기)
        rankList.sort((u1, u2) -> {
            // 스탬프 개수로 먼저 비교
            int stampCountComparison = Integer.compare((Integer) u2.get("stampCount"), (Integer) u1.get("stampCount"));
            if (stampCountComparison == 0) {
                // 스탬프 개수가 같으면 총점으로 비교
                return Integer.compare((Integer) u2.get("totalStampScore"), (Integer) u1.get("totalStampScore"));
            }
            return stampCountComparison;
        });

        // 4. 랭킹 부여
        for (int i = 0; i < rankList.size(); i++) {
            rankList.get(i).put("rank", i + 1);  // 랭킹은 1부터 시작
        }

        return rankList;
    }
}
