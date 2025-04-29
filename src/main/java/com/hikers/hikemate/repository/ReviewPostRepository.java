package com.hikers.hikemate.repository;

import com.hikers.hikemate.entity.ReviewPost;
import com.hikers.hikemate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewPostRepository extends JpaRepository<ReviewPost, Long> {
    @Query("SELECT r FROM ReviewPost r ORDER BY r.createdAt DESC")
    List<ReviewPost> findAllByOrderByCreatedAtDesc();

    @Query("SELECT r FROM ReviewPost r LEFT JOIN r.likes l GROUP BY r.id ORDER BY COUNT(l) DESC")
    List<ReviewPost> findAllByOrderByLikesDesc();

    List<ReviewPost> findByAuthor(User author);

    // 코스 별 조회
    @Query("SELECT r FROM ReviewPost r WHERE r.course.id = :courseId ORDER BY r.createdAt DESC")
    List<ReviewPost> findByCourseIdOrderByCreatedAtDesc(Long courseId);

    @Query("SELECT r FROM ReviewPost r LEFT JOIN r.likes l WHERE r.course.id = :courseId GROUP BY r.id ORDER BY COUNT(l) DESC")
    List<ReviewPost> findByCourseIdOrderByLikesDesc(Long courseId);

    // 산 별 조회
    @Query("SELECT r FROM ReviewPost r WHERE r.course.mountain.id = :mountainId ORDER BY r.createdAt DESC")
    List<ReviewPost> findByMountainIdOrderByCreatedAtDesc(Long mountainId);

    @Query("SELECT r FROM ReviewPost r LEFT JOIN r.likes l WHERE r.course.mountain.id = :mountainId GROUP BY r.id ORDER BY COUNT(l) DESC")
    List<ReviewPost> findByMountainIdOrderByLikesDesc(Long mountainId);

    List<ReviewPost> findByAuthorOrderByCreatedAtDesc(User user); // 최신순
    @Query("SELECT r FROM ReviewPost r LEFT JOIN r.likes l WHERE r.author = :author GROUP BY r.id ORDER BY COUNT(l) DESC")
    List<ReviewPost> findByAuthorOrderByLikesDesc(User user);
}
