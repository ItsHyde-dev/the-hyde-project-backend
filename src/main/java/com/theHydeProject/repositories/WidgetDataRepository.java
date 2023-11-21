package com.theHydeProject.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.theHydeProject.models.WidgetData;

import jakarta.transaction.Transactional;

public interface WidgetDataRepository extends JpaRepository<WidgetData, Long> {

  @Query("select wd from WidgetData wd where wd.user.id = :userId")
  List<WidgetData> findAllByUserId(Long userId);

  @Query("select wd from WidgetData wd where wd.user.id = :userId and wd.id = :widgetId")
  WidgetData findByUserIdAndWidgetId(Long userId, Long widgetId);

  @Transactional
  @Modifying
  @Query("delete from WidgetData wd where wd.user.id = :userId and wd.id = :widgetId")
  void deleteByUserIdAndWidgetId(Long userId, Long widgetId);

  @Transactional
  @Modifying
  @Query("update WidgetData wd set wd.name = :name where wd.user.id = :userId and wd.id = :widgetId")
  void updateNameByUserIdAndWidgetId(Long userId, Long widgetId, String name);
}
