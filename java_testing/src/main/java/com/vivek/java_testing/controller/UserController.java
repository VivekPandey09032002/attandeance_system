package com.vivek.java_testing.controller;

import java.util.Collections;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
                        final var errors = result.getAllErrors().stream()
                                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                        .toList();
                        throw new CustomException("cannot register the user", errors, HttpStatus.BAD_REQUEST);
                }
                if (userRepository.existsById(userDto.getUserId())) {
                        throw new CustomException("cannot register the user, already exist",
                                        List.of("user id already exists"),
                                        HttpStatus.FOUND);
                }
                User user = mapper.map(userDto, User.class);
                userRepository.save(user);
                final var responseBody = ResponseBody.builder().data(List.of(userDto))
                                .message("user saved successfully")
                                .data(List.of(userDto)).status(HttpStatus.CREATED).build();
                return ResponseEntity.ok(responseBody);
        }

        @GetMapping
        public ResponseEntity<ResponseBody<Object>> getUserAllUser() {
                List<User> users = userRepository.findAll();

                final var usersDto = users.stream().map((user) -> mapper.map(user, RequestUser.class)).toList();

                final var responseBody = ResponseBody.builder().data(List.of(usersDto)).message("users list success")
                                .status(HttpStatus.OK).build();
                return ResponseEntity.ok(responseBody);
        }

        @GetMapping("{userId}")
        public ResponseEntity<ResponseBody<Object>> getUser(@PathVariable String userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new CustomException("cannot get the user,no user found",
                                                List.of("no a valid userId"), HttpStatus.NOT_FOUND));
                RequestUser userDto = mapper.map(user, RequestUser.class);
                final var responseBody = ResponseBody.builder().data(List.of(userDto)).message("user respone success")
                                .data(List.of(userDto)).status(HttpStatus.CREATED).build();
                return ResponseEntity.ok(responseBody);
        }

}
