package com.praise.incident.repo;

import com.praise.incident.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findById(Integer integer);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByEmail(String email);
}
