package com.jb.banksystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sourceAccountNumber; // Số tài khoản nguồn
    private String destinationAccountNumber; // Số tài khoản đích
    private Double amount; // Số tiền giao dịch
    private String status; // Trạng thái giao dịch (thành công, thất bại)
    private LocalDateTime timestamp; // Thời gian giao dịch
}