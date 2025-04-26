package com.hikers.hikemate.service;

import com.hikers.hikemate.entity.Course;
import com.hikers.hikemate.entity.Stamp;
import com.hikers.hikemate.entity.User;
import com.hikers.hikemate.repository.CourseRepository;
import com.hikers.hikemate.repository.StampRepository;
import com.hikers.hikemate.repository.UserRepository;
import com.hikers.hikemate.util.GeoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StampService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StampRepository stampRepository;

    public Stamp authenticateUserLocation(String userId, int courseId, double userLat, double userLng) {
        // 사용자 정보 조회
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 코스 정보 조회
        Course course = courseRepository.findById((long)courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));

        // 코스의 목표 지점 위경도 정보
        BigDecimal courseLat = course.getCourseLastLat();
        BigDecimal courseLng = course.getCourseLatLng();

        // 사용자 위치가 코스와 가까운지 확인
        if (isWithinRange(new BigDecimal(userLat), new BigDecimal(userLng), courseLat, courseLng)) {
            // 인증이 성공하면 levelWeight 계산
            int levelWeight = calculateLevelWeight(course.getLevel());

            // Stamp 엔티티 생성
            Stamp stamp = new Stamp();
            stamp.setUser(user);
            stamp.setCourse(course);
            stamp.setLevelWeight(levelWeight);

            // Stamp 저장 후 반환
            return stampRepository.save(stamp);
        } else {
            throw new IllegalArgumentException("인증에 실패하였습니다. 정확한 위치로 가서 다시 인증해주세요.");
        }
    }


    private boolean isWithinRange(BigDecimal userLat, BigDecimal userLng, BigDecimal courseLat, BigDecimal courseLng) {
        // 위경도 차이를 GeoUtil을 사용하여 거리로 변환
        double distance = GeoUtil.calculateDistance(userLat.doubleValue(), userLng.doubleValue(),
                courseLat.doubleValue(), courseLng.doubleValue());

        // 50m 이내이면 인증이 가능
        return distance <= 50;  // 50m 이내
    }

    private int calculateLevelWeight(String level) {
        switch (level) {
            case "상":
                return 3;
            case "중":
                return 2;
            case "하":
                return 1;
            default:
                throw new IllegalArgumentException("Invalid course level");
        }
    }
    public List<Stamp> getStampsByUser(String userId) {
        // 사용자 정보 조회
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 사용자에 해당하는 스탬프 목록 반환
        return stampRepository.findByUser(user);
    }
}
