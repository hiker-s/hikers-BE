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
import java.time.LocalDate;
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
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        // 코스의 목표 지점 위경도 정보
        BigDecimal courseLat = course.getCourseLastLat();
        BigDecimal courseLng = course.getCourseLastLng();

        //사용자가 하루에 같은 코스는 한번만 인증받도록 검층 로직 추가
        LocalDate today = LocalDate.now();
        boolean alreadyStamped = stampRepository.existsByUserAndCourseAndStampDate(user,course,today);
        if(alreadyStamped){
            throw new IllegalArgumentException("오늘은 이미 이 코스에서 인증을 완료했습니다.");
        }

        // 사용자 위치가 코스와 가까운지 확인
        if (isWithinRange(new BigDecimal(userLat), new BigDecimal(userLng), courseLat, courseLng)) {
            // 인증이 성공하면 levelWeight 계산
            int levelWeight = calculateLevelWeight(course.getLevel());

            // Stamp 엔티티 생성
            Stamp stamp = new Stamp();
            stamp.setUser(user);
            stamp.setCourse(course);
            stamp.setLevelWeight(levelWeight);
            stamp.setStampDate(LocalDate.now()); // 현재 날짜 설정

            // Stamp 저장 후 반환
            return stampRepository.save(stamp);
        } else {
            throw new IllegalArgumentException("인증에 실패하였습니다. 목표지점 50m 이내로 이동하여 다시 인증해주세요.");
        }
    }

    private boolean isWithinRange(BigDecimal userLat, BigDecimal userLng, BigDecimal courseLat, BigDecimal courseLng) {
        if (courseLat == null || courseLng == null) {
            throw new IllegalArgumentException("목표 지점 50m 이내로 이동해 주세요.");
        }

        double distance = GeoUtil.calculateDistance(userLat.doubleValue(), userLng.doubleValue(),
                courseLat.doubleValue(), courseLng.doubleValue());

        // 거리 출력
        System.out.println("Calculated Distance: " + distance);

        if (distance > 50) {
            throw new IllegalArgumentException("인증에 실패하였습니다. 목표지점 50m 이내로 이동하여 다시 인증해주세요.");
        }

        return true;  // 50m 이내로 인증이 가능
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

    public List<Stamp> getStampsByCourse(Integer courseId) {
        // 코스 정보 조회
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));

        // 해당 코스에 대한 스탬프 목록 반환
        return stampRepository.findByCourse(course);
    }

    public List<Stamp> getStampsByCourseAndDate(Integer courseId, LocalDate date) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        // 해당 날짜에 인증된 스탬프 목록 반환
        return stampRepository.findByCourseAndStampDate(course, date);
    }

    public Stamp stampForServer(String userId, int courseId) {
        // 사용자 정보 조회
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 코스 정보 조회
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));

        int levelWeight = calculateLevelWeight(course.getLevel());

        // Stamp 엔티티 생성
        Stamp stamp = new Stamp();
        stamp.setUser(user);
        stamp.setCourse(course);
        stamp.setLevelWeight(levelWeight);
        stamp.setStampDate(LocalDate.now()); // 현재 날짜 설정

        return stampRepository.save(stamp);
    }

}
