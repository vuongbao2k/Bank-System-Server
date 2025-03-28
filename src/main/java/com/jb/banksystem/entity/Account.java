package com.jb.banksystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber; // Số tài khoản
    private String accountType;   // Loại tài khoản (e.g., Savings, Checking)
    private Double balance;       // Số dư
    private String currency;      // Loại tiền tệ (e.g., USD, VND)

    @ManyToOne
    @JoinColumn(name = "user_id") // Khóa ngoại liên kết với bảng OurUsers
    @JsonBackReference // Đánh dấu đây là tham chiếu ngược
    private OurUsers user;        // Người sở hữu tài khoản
}
