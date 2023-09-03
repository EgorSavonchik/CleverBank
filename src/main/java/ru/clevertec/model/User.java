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
public class User {
    private int id;
    @Expose
    private String name;
    @Expose
    private String password;
    @Expose
    private List<Account> accountList;

    public User(String name, String password)
    {
        this.name = name;
        this.password = password;
        this.accountList = new ArrayList<>();
    }

    public User(String name, String password, List<Account> accountList)
    {
        this.name = name;
        this.password = password;
        this.accountList = accountList;
    }
}
