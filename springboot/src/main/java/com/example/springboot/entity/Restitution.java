package com.example.springboot.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class Restitution extends BaseEntity implements Serializable {
    private String bookName;
    private String bookNo;
    private String userNo;
    private String userName;
    private String userPhone;
    private Integer score;
    private String status;
    private Integer days;
    private LocalDate returnDate;
    private LocalDate realDate;
}
