package com.theHydeProject.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class WidgetData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "widget_id")
    private Widgets widget;

    @Column(nullable = false)
    @Lob
    private String data;

    @Column(nullable = false)
    private int position;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    public WidgetData(Widgets widget, String data, int position, Users user) {
        this.widget = widget;
        this.data = data;
        this.position = position;
        this.user = user;
    }

}
