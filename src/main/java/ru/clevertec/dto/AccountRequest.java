package ru.clevertec.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.clevertec.util.LocalDateAdapter;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest implements Serializable {
    @Expose
    private int ownerBankId;
    @Expose
    private int ownerUserId;
    @Expose
    private double amount;
    @Expose
    @JsonAdapter(LocalDateAdapter.class)
    private LocalDate createdAt;
}
