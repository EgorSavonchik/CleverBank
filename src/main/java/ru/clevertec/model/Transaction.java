package ru.clevertec.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.clevertec.util.LocalDateAdapter;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private int id;
    private Integer senderAccountId;
    private Integer beneficiaryAccountId;
    @Expose
    private double amount;
    @Expose
    @JsonAdapter(LocalDateAdapter.class)
    private LocalDate createdAt;
    @Expose
    private Operation operationType;

    public Transaction(Integer senderAccountId, Integer beneficiaryAccountId, double amount, Operation operationType)
    {
        this.senderAccountId = senderAccountId;
        this.beneficiaryAccountId = beneficiaryAccountId;
        this.amount = amount;
        this.operationType = operationType;
    }

    public Transaction(Integer senderAccountId, Integer beneficiaryAccountId, double amount, LocalDate createdAt,
                       Operation operationType)
    {
        this.senderAccountId = senderAccountId;
        this.beneficiaryAccountId = beneficiaryAccountId;
        this.amount = amount;
        this.createdAt = createdAt;
        this.operationType = operationType;
    }

    public enum Operation
    {
        REPLENISHMENT,
        WITHDRAWAL,
        TRANSFER;
    }
}

