package com.theHydeProject.components.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignupDto {
    @NotNull
    @Email
    private String username;

    @NotNull
    @Pattern(regexp = "^[ A-Za-z0-9_@./#&+-]*$", message = "Password should contain only A-Za-z0-9_@./#&+-")
    private String password;
}
