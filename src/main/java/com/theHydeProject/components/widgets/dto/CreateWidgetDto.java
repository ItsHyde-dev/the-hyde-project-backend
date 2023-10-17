package com.theHydeProject.components.widgets.dto;

import lombok.Data;

@Data
public class CreateWidgetDto {

    public Long widgetId;
    public Long userId;
    public String data;
    public int position;

}
