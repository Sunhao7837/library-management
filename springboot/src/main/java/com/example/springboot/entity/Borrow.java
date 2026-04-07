package com.example.springboot.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class Borrow extends BaseEntity implements Serializable {
    private String bookName;
    private String bookNo;
    private String userNo;
    private String userName;
    private String userPhone;
    private Integer score;
    private String status;
    private Integer days;
    private LocalDate returnDate;
    // 提醒状态 即将到期(-1) 已到期(当天) 已过期(超过归还日期以后)
    private String note;
}
