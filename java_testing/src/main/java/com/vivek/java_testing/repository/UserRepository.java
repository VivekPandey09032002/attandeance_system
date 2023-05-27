package com.vivek.java_testing.repository;

import com.vivek.java_testing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
