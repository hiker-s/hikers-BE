package com.hikers.hikemate.repository;

import com.hikers.hikemate.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
