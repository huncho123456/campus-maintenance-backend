package com.praise.incident.repo;

import com.praise.incident.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findById(Long id);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByEmail(String email);
}
