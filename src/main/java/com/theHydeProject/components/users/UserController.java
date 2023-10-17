package com.theHydeProject.components.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.theHydeProject.models.Users;
import com.theHydeProject.repositories.UserRepository;
import com.theHydeProject.validations.GetAllUsersDto;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @GetMapping("getAllUsers")
  @ResponseBody
  public Page<Users> getAllUsers(@Valid @RequestBody GetAllUsersDto body) {
    int page = body.getPage();
    int limit = body.getLimit();

    PageRequest pageRequest = PageRequest.of(page, limit);
    return userRepository.findAll(pageRequest);
  }

  @GetMapping("getUser/{id}")
  @ResponseBody
  public Users getUser(@PathVariable Long id) {
    return userRepository.findById(id).orElse(null);
  }

}
