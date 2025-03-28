package com.jb.banksystem.repository;

import com.jb.banksystem.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceAccountNumber(String sourceAccountNumber);
    List<Transaction> findByDestinationAccountNumber(String destinationAccountNumber);
}