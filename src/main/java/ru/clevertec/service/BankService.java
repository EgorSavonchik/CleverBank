package ru.clevertec.service;

import ru.clevertec.model.Account;
import ru.clevertec.model.Bank;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BankService {
    private static Connection connection;
    private static AccountService accountService = new AccountService();

    {
        ResourceBundle bundle = ResourceBundle.getBundle("database");

        String url = bundle.getString("url");
        String username = bundle.getString("username");
        String password = bundle.getString("password");

        try {
            Class.forName(bundle.getString("driver"));
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Bank> findAll() {
        List<Bank> banks = new ArrayList<>();

        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Banks");

            while (resultSet.next()) {
                banks.add(new Bank(resultSet.getString("name"),
                        new AccountService().findByBankId(resultSet.getInt("id"))));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return banks;
    }

    public Bank findById(int id) {
        Bank bank = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Banks WHERE Banks.id=?");
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            bank = new Bank();
            bank.setName(resultSet.getString("name"));

            List<Account> accountList = new ArrayList<>();
            PreparedStatement accountPreparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE owner_bank_id = ?");
            accountPreparedStatement.setInt(1, id);

            ResultSet accountResultSet = accountPreparedStatement.executeQuery();

            while (accountResultSet.next()) {
                accountList.add(new Account(id, accountResultSet.getInt("owner_user_id")
                        , accountResultSet.getInt("amount")));
            }

            bank.setAccountList(accountList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return bank;
    }

    public void create(Bank newBank) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Banks(name) VALUES(?)");
            preparedStatement.setString(1, newBank.getName());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Accounts WHERE Banks.id=?");
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Bank findByAccountId(int id) {
        return this.findById(accountService.findById(id).getOwnerBankId());
    }
}
