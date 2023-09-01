package ru.clevertec.model;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private int id;
    private int ownerBankId;
    private int ownerUserId;
    private double amount;
    private LocalDate createdAt;
    private String accountNumber;

    public Account(int id, int ownerBankId, int ownerUserId, double amount) {
        this.id = id;
        this.ownerBankId = ownerBankId;
        this.ownerUserId = ownerUserId;
        this.amount = amount;
        this.accountNumber = null;
        this.createdAt = LocalDate.now();
    }
}
