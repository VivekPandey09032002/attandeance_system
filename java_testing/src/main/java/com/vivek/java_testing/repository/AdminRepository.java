package com.vivek.java_testing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vivek.java_testing.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

}
