package com.vivek.java_testing.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admin_table")
@Getter
@Setter
public class Admin {

    @Id
    private int adminId;

    @Column(name = "admin_name")
    private String adminName;

    @Column(name = "admin_password")
    private String password;

}
