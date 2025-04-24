package com.hikers.hikemate.repository;

import com.hikers.hikemate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //아이디 또는 이메일로 사용자 찾기 ( 이메일도 포함인가?)
    Optional<User> findByUsername(String user_id);

    Optional<User> fingByEmail(String email);

    boolean existsByUserId(String user_id);
    boolean existsByEmail(String email);
}
