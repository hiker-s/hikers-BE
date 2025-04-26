package com.hikers.hikemate.repository;

import com.hikers.hikemate.entity.Stamp;
import com.hikers.hikemate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StampRepository extends JpaRepository<Stamp, Long> {
    List<Stamp> findByUser(User user);
}
