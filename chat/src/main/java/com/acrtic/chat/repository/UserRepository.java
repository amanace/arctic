package com.acrtic.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acrtic.chat.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity , Long> {
    Optional<UserEntity> findByUsername(String username);
}
