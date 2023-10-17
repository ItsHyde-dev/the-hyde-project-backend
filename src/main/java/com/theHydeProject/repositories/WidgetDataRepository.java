package com.theHydeProject.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.theHydeProject.models.WidgetData;

public interface WidgetDataRepository extends JpaRepository<WidgetData, Long> {

    List<WidgetData> findAllByUser_Id(Long userId);

    @Query("select max(position) from WidgetData wd where wd.user.id = :userId")
    int findMaxPossiblePosition(Long userId);

    @Query("select position from WidgetData wd where id = :widgetId")
    int findCurrentPosition(Long widgetId);

    @Query("update WidgetData wd set wd.position = wd.position + :increment where wd.position >= :lowerLimit and wd.position <= :upperLimit")
    void incrementRange(int lowerLimit, int upperLimit, int increment);

}
