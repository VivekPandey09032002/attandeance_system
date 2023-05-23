package com.vivek.java_testing.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vivek.java_testing.dto.RequestUser;
import com.vivek.java_testing.dto.ResponseBody;
import com.vivek.java_testing.entity.User;
import com.vivek.java_testing.exception.CustomException;
import com.vivek.java_testing.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @PostMapping
    public ResponseEntity<ResponseBody<Object>> saveUser(@RequestBody @Valid RequestUser userDto,
            BindingResult result) {
        if (result.hasErrors()) {
            final var errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            throw new CustomException("cannot register the user", errors, HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsById(userDto.getEmail())) {
            throw new CustomException("cannot register the user, already exist", List.of("email id already exists"),
                    HttpStatus.FOUND);
        }
        User user = mapper.map(userDto, User.class);
        userRepository.save(user);
        final var responseBody = ResponseBody.builder().data(List.of(userDto)).message("user saved successfully")
                .data(List.of(userDto)).status(HttpStatus.CREATED).build();
        return ResponseEntity.ok(responseBody);
    }

}