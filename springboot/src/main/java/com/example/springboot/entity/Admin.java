package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Admin extends BaseEntity {
    private String username;
    private String phone;
    private String email;
    private String password;
    private boolean status;
}
