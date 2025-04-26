package com.hikers.hikemate.repository;

import com.hikers.hikemate.entity.Mountain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MountainRepository extends JpaRepository<Mountain, Long> {
    List<Mountain> findAllByOrderByViewCountDesc();
}
