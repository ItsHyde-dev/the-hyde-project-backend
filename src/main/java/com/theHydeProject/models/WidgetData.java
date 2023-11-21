package com.theHydeProject.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "widget_data")
@ToString
public class WidgetData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "widget_id")
    private Widgets widget;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String data;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    // @Column(name = "layouts")
    // private String layouts;

    @Column(name = "link_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long linkId;
}
