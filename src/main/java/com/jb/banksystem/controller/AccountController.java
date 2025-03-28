package com.jb.banksystem.controller;

import com.jb.banksystem.dto.TransferRequest;
import com.jb.banksystem.entity.Account;
import com.jb.banksystem.entity.Transaction;
import com.jb.banksystem.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<?> getAccountsForCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        List<Account> accounts = accountService.getAccountsByUsername(username);
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(@RequestBody TransferRequest transferRequest, Authentication authentication) {
        try {
            Transaction transaction = accountService.transfer(
                    transferRequest.getSourceAccountNumber(),
                    transferRequest.getDestinationAccountNumber(),
                    transferRequest.getAmount(),
                    authentication
            );
            return ResponseEntity.ok(transaction);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }
}