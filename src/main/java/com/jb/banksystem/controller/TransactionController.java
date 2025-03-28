package com.jb.banksystem.controller;

import com.jb.banksystem.entity.Transaction;
import com.jb.banksystem.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/source/{accountNumber}")
    public ResponseEntity<List<Transaction>> getSentTransactions(@PathVariable String accountNumber) {
        List<Transaction> transactions = transactionService.getTransactionsBySourceAccountNumber(accountNumber);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/destination/{accountNumber}")
    public ResponseEntity<List<Transaction>> getReceivedTransactions(@PathVariable String accountNumber) {
        List<Transaction> transactions = transactionService.getTransactionsByDestinationAccountNumber(accountNumber);
        return ResponseEntity.ok(transactions);
    }
}