package com.hikers.hikemate.repository;

import com.hikers.hikemate.entity.Course;
import com.hikers.hikemate.entity.Scrap;
import com.hikers.hikemate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    // user와 course로 Scrap 객체가 있는지 확인
    Optional<Scrap> findByUserAndCourse(User user, Course course);

    // 내가 스크랩한 코스
    List<Scrap> findByUser(User user);
}
