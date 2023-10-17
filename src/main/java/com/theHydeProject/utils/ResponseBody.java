package com.theHydeProject.utils;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResponseBody {

    public ResponseBody(String message, Map<String, Object> data) {
        this.message = message;
        this.data = data;
    }

    @NotNull
    private String message;

    private Map<String, Object> data;
}
