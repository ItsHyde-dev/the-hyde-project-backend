package com.theHydeProject.components.auth;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public void signup(@Valid @RequestBody SignupDto body) {
        Users user = new Users();
        user.setUsername(body.getUsername());
        user.setPassword(passwordEncoder.encode(body.getPassword()));

        Optional<Roles> role = roleRepository.findByName("ADMIN");
        if (role.isPresent()) {
            user.addRole(role.get());
        }

        userRepository.save(user);
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
