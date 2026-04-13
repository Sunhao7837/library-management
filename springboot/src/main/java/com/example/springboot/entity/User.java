package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class User extends BaseEntity {
    private String name;
    private String username;
    private Integer age;
    private String sex;
    private String phone;
    private String address;
    private Integer account;
    private Integer score;
    private boolean status;
}
