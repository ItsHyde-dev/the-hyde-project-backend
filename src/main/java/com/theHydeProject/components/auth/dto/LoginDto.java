package com.theHydeProject.components.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginDto {
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_]*$")
    private String username;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_]*$")
    private String password;
}
