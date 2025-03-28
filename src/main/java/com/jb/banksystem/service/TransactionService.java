package com.jb.banksystem.service;

import com.jb.banksystem.entity.Transaction;
import com.jb.banksystem.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getTransactionsBySourceAccountNumber(String accountNumber) {
        return transactionRepository.findBySourceAccountNumber(accountNumber);
    }

    public List<Transaction> getTransactionsByDestinationAccountNumber(String accountNumber) {
        return transactionRepository.findByDestinationAccountNumber(accountNumber);
    }
}