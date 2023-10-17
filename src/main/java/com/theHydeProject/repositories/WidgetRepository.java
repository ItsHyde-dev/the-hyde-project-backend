package com.theHydeProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.theHydeProject.models.Widgets;

public interface WidgetRepository extends JpaRepository<Widgets, Long> {
}
