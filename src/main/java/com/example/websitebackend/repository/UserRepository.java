package com.example.websitebackend.repository;

import java.util.Optional;
import com.example.websitebackend.model.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<CustomUser, Long> {
    Optional<CustomUser> findByEmail(String email);
    boolean existsByEmail(String email);
}