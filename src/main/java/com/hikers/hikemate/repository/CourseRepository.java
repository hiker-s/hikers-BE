package com.hikers.hikemate.repository;

import com.hikers.hikemate.dto.course.CourseDetailDto;
import com.hikers.hikemate.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Query("SELECT c FROM Course c JOIN c.mountain m WHERE m.id = :mntId ORDER BY c.courseName ASC")
    List<Course> findByMountainIdOrderByTitleAsc(@Param("mntId") Long mntId);

    @Query("SELECT c FROM Course c JOIN c.mountain m WHERE m.id = :mntId ORDER BY c.level ASC")
    List<Course> findByMountainIdOrderByLevelAsc(@Param("mntId") Long mntId);

    @Query("SELECT c FROM Course c JOIN c.mountain m WHERE m.id = :mntId ORDER BY SIZE(c.reviews) DESC")
    List<Course> findByMountainIdOrderByReviewCountDesc(@Param("mntId") Long mntId);

    @Query("SELECT c FROM Course c JOIN c.mountain m WHERE m.id = :mntId ORDER BY SIZE(c.scraps) DESC")
    List<Course> findByMountainIdOrderByScrapCountDesc(@Param("mntId") Long mntId);
}