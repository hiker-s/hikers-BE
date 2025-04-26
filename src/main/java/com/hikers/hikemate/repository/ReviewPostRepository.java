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
}
