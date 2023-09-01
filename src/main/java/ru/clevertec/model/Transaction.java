package ru.clevertec.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private Integer senderAccountId;
    private Integer beneficiaryAccountId;
    private double amount;
    private LocalDate createdAt;
    private Operation operationType;

    public Transaction(Integer senderAccountId, Integer beneficiaryAccountId, double amount, Operation operationType)
    {
        this.senderAccountId = senderAccountId;
        this.beneficiaryAccountId = beneficiaryAccountId;
        this.amount = amount;
        this.operationType = operationType;
    }

    public enum Operation
    {
        REPLENISHMENT,
        WITHDRAWAL,
        TRANSFER;
    }
}

