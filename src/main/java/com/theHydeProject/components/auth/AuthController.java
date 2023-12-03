package com.theHydeProject.components.auth;

import java.util.Optional;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.theHydeProject.components.auth.dto.LoginDto;
import com.theHydeProject.components.auth.dto.SignupDto;
import com.theHydeProject.models.Roles;
import com.theHydeProject.models.Users;
import com.theHydeProject.repositories.RoleRepository;
import com.theHydeProject.repositories.UserRepository;
import com.theHydeProject.utils.JwtUtil;
import com.theHydeProject.utils.ResponseBuilderFactory;
import com.theHydeProject.utils.ResponseBody;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResponseBuilderFactory response;

    @PostMapping("signup")
    public ResponseEntity<ResponseBody> signup(@Valid @RequestBody SignupDto body) {
        Users user = new Users();
        user.setUsername(body.getUsername());
        user.setPassword(passwordEncoder.encode(body.getPassword()));

        Optional<Roles> role = roleRepository.findByName("ADMIN");
        if (role.isPresent()) {
            user.addRole(role.get());
        }

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            String columnName = null;
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException constraintViolationException = (ConstraintViolationException) e.getCause();
                columnName = constraintViolationException.getConstraintName();
                columnName = columnName.substring(columnName.indexOf("_") + 1, columnName.lastIndexOf("_"));
            } else {
                columnName = "User";
            }

            return response.builder().status(HttpStatus.BAD_REQUEST).message(columnName + " already exists")
                    .build();
        } catch (Exception e) {
            return response.builder().status(HttpStatus.BAD_REQUEST).message(e.getMessage()).build();
        }

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(body.getUsername());
        loginDto.setPassword(body.getPassword());

        return this.login(loginDto);
    }

    @PostMapping("login")
    public ResponseEntity<ResponseBody> login(@Valid @RequestBody LoginDto body) {

        System.out.println("Login started");

        if (body.getUsername() == null || body.getPassword() == null) {
            return response.builder()
                    .message("Missing username or password")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Optional<Users> user = userRepository.findByUsername(body.getUsername());

        System.out.println("Find User finished");

        if (user.isPresent()) {
            if (passwordEncoder.matches(body.getPassword(), user.get().getPassword())) {
                String token = jwtUtil.createToken(user.get());
                // set the token in the database with expiry

                return response.builder().status(HttpStatus.OK)
                        .message("Successfully Logged in")
                        .addToBody("token", token)
                        .build();
            }
        }
        System.out.println("Returning invalid password or username error");

        return response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Invalid Username or Password").build();
    }

}
