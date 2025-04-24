package com.hikers.hikemate.repository;

import com.hikers.hikemate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //아이디 또는 이메일로 사용자 찾기
    Optional<User> findByUserId(String user_id);
}
