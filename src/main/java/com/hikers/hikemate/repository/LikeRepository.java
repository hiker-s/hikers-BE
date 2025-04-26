package com.hikers.hikemate.repository;

import com.hikers.hikemate.entity.Like;
import com.hikers.hikemate.entity.ReviewPost;
import com.hikers.hikemate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndReviewPost(User user, ReviewPost reviewPost);
    boolean existsByUserAndReviewPost(User user, ReviewPost reviewPost);
    int countByReviewPost(ReviewPost reviewPost);
}
