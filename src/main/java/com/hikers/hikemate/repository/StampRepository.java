package com.hikers.hikemate.repository;

import com.hikers.hikemate.entity.Course;
import com.hikers.hikemate.entity.Stamp;
import com.hikers.hikemate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StampRepository extends JpaRepository<Stamp, Long> {
    List<Stamp> findByUser(User user);

    // 코스로 검색
    List<Stamp> findByCourse(Course course);

    // 코스와 날짜로 검색
    List<Stamp> findByCourseAndStampDate(Course course, LocalDate stampDate);

    // 사용자가 특정 코스에서 특정 날짜에 인증을 받았는지 확인하는 메서드 추가
    boolean existsByUserAndCourseAndStampDate(User user, Course course, LocalDate stampDate);
}