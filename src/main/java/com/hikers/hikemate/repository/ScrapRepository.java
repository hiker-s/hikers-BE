package com.hikers.hikemate.repository;

import com.hikers.hikemate.entity.Course;
import com.hikers.hikemate.entity.Scrap;
import com.hikers.hikemate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    // user와 course로 Scrap 객체가 있는지 확인
    Optional<Scrap> findByUserAndCourse(User user, Course course);

    // 내가 스크랩한 코스
    List<Scrap> findByUser(User user);

    //이름 순 정렬
    @Query("SELECT s FROM Scrap s JOIN FETCH s.course c WHERE s.user = :user ORDER BY c.courseName ASC")
    List<Scrap> findByUserOrderByCourseName(@Param("user") User user);
    //스크랩 많은 순으로 정렬
    @Query("SELECT s FROM Scrap s JOIN FETCH s.course c WHERE s.user = :user ORDER BY SIZE(c.scraps) DESC")
    List<Scrap> findByUserOrderByScrapCount(@Param("user") User user);
    //리뷰 많은 순으로 정렬
    @Query("SELECT s FROM Scrap s JOIN FETCH s.course c WHERE s.user = :user ORDER BY SIZE(c.reviews) DESC")
    List<Scrap> findByUserOrderByReviewCount(@Param("user") User user);
    // 난이도순
    @Query("SELECT s FROM Scrap s JOIN FETCH s.course c WHERE s.user = :user ORDER BY c.level ASC")
    List<Scrap> findByUserOrderByLevel(@Param("user") User user);
}
