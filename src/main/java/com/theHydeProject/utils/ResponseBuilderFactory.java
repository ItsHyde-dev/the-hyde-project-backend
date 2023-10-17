package com.theHydeProject.utils;

import org.springframework.stereotype.Component;

@Component
public class ResponseBuilderFactory {

    public ResponseBuilder builder() {
        return new ResponseBuilder();
    }

}
