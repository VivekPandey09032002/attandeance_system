package com.vivek.java_testing.controller;

import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vivek.java_testing.dto.AdminDto;
import com.vivek.java_testing.dto.ResponseBody;
import com.vivek.java_testing.entity.Admin;
import com.vivek.java_testing.exception.CustomException;
import com.vivek.java_testing.repository.AdminRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminRepository adminRepository;

    @PostMapping("/sign-up")
    public ResponseEntity<ResponseBody<Object>> signUp(@RequestBody @Valid AdminDto adminDto, BindingResult results) {

        if (results.hasErrors()) {
            final var errors = results.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            throw new CustomException("cannot signed up admin", errors, HttpStatus.BAD_REQUEST);
        }

        Admin admin = new Admin();
        BeanUtils.copyProperties(adminDto, admin);
        if (adminDto.getAdminId() == null || adminRepository.existsById(adminDto.getAdminId())) {
            throw new CustomException("error in performing operation", List.of("Admin already exists"),
                    HttpStatus.BAD_REQUEST);
        }

        adminRepository.save(admin);
        final var responseBody = ResponseBody.builder().data(List.of(adminDto)).message("Admin register sucesfully")
                .status(HttpStatus.CREATED).build();
        return ResponseEntity.ok(responseBody);

    }

    @PostMapping("/login")
    public ResponseEntity<ResponseBody<Object>> loginIn(@RequestBody @Valid AdminDto adminDto, BindingResult results) {

        if (results.hasErrors()) {
            final var errors = results.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            throw new CustomException("cannot signed in admin", errors, HttpStatus.BAD_REQUEST);
        }

        if (adminDto.getAdminId() == null || !adminRepository.existsById(adminDto.getAdminId())) {
            throw new CustomException("error in performing operation", List.of("no user found with given user id"),
                    HttpStatus.BAD_REQUEST);
        }
        final var responseBody = ResponseBody.builder().data(List.of(adminDto)).message("Admin logged in sucesfully")
                .status(HttpStatus.CREATED).build();
        return ResponseEntity.ok(responseBody);
    }

}
