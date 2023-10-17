package com.theHydeProject.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseBuilder {

    String message;
    Map<String, Object> data = new HashMap<>();
    HttpStatusCode statusCode = HttpStatus.OK;

    public ResponseEntity<ResponseBody> build() {
        return ResponseEntity.status(statusCode).body(new ResponseBody(message, data));
    }

    public ResponseBuilder message(String message) {
        this.message = message;
        return this;
    }

    public ResponseBuilder addToBody(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public ResponseBuilder status(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

}
