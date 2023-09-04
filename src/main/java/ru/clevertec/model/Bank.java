package ru.clevertec.model;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bank {
    private int id;
    @Expose
    String name;
    @Expose
    private List<Account> accountList;

    public Bank(String name) {
        this.name = name;
        accountList = new ArrayList<>();
    }
}
