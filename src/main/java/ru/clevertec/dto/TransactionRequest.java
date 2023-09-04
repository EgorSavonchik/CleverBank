package ru.clevertec.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.clevertec.model.Transaction;
import ru.clevertec.util.LocalDateAdapter;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest implements Serializable {
    @Expose
    private Integer senderAccountId;
    @Expose
    private Integer beneficiaryAccountId;
    @Expose
    private double amount;
    @Expose
    @JsonAdapter(LocalDateAdapter.class)
    private LocalDate createdAt;
    @Expose
    private Transaction.Operation operationType;
}
