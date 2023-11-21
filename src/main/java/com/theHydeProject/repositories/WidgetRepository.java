package com.theHydeProject.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.theHydeProject.models.Widgets;

public interface WidgetRepository extends JpaRepository<Widgets, Long> {
    @Query("select w from Widgets w where w.id = :id")
    Optional<Widgets> findById(Long id);
}
