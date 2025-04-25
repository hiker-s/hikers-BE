package com.hikers.hikemate.repository;

import com.hikers.hikemate.entity.CrewPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrewPostRepository extends JpaRepository<CrewPost, Long> {

    List<CrewPost> findByAuthor_UserId(String userId);

}
