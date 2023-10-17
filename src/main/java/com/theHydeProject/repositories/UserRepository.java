package com.theHydeProject.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.theHydeProject.models.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
}
