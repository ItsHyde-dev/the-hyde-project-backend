package com.theHydeProject.components.widgets.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateWidgetDto {

    @NotNull
    public Long widgetId;
    public Object data;

}
