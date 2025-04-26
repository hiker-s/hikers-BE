package com.hikers.hikemate.repository;

import com.hikers.hikemate.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
