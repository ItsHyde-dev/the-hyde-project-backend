package com.theHydeProject.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.theHydeProject.models.Users;
import com.theHydeProject.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> user = userRepository.findByUsername(username);
        Set<String> roles = new HashSet<String>();

        if (user.isPresent()) {

            user.get().getRoles().forEach(r -> roles.add(r.getName()));
            String[] roleNames = roles.toArray(new String[0]);
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.get().getUsername())
                    .password(user.get().getPassword())
                    .roles(roleNames)
                    .build();
            return userDetails;
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

}
