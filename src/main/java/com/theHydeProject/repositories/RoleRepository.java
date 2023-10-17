package com.theHydeProject.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.theHydeProject.models.Roles;

public interface RoleRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByName(String role);
}
