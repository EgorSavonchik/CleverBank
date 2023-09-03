package ru.clevertec.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import lombok.*;
import ru.clevertec.util.LocalDateAdapter;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private int id;
    private int ownerBankId;
    private int ownerUserId;
    @Expose
    private double amount;
    @Expose
    @JsonAdapter(LocalDateAdapter.class)
    private LocalDate createdAt;
    @Expose
    private String accountNumber;

    public Account(int id, int ownerBankId, int ownerUserId, double amount) {
        this.id = id;
        this.ownerBankId = ownerBankId;
        this.ownerUserId = ownerUserId;
        this.amount = amount;
        this.accountNumber = null;
        this.createdAt = LocalDate.now();
    }

    public Account(int ownerBankId, int ownerUserId, double amount, LocalDate createdAt) {
        this.ownerBankId = ownerBankId;
        this.ownerUserId = ownerUserId;
        this.amount = amount;
        this.accountNumber = null;
        this.createdAt = createdAt;
    }
}
