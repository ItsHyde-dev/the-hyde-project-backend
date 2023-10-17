package com.theHydeProject.models;

import java.util.HashSet;
import java.util.Set;

import com.theHydeProject.components.widgets.WidgetType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Widgets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WidgetType type;

    @OneToMany(mappedBy = "widget")
    private Set<WidgetData> widgetData = new HashSet<WidgetData>();

}
