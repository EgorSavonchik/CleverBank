package ru.clevertec.service;

import ru.clevertec.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionService {
    private static Connection connection;

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

    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();

        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Transactions");

            while (resultSet.next())
            {
                Transaction transaction = new Transaction();

                transaction = new Transaction();
                transaction.setId(resultSet.getInt("id"));
                transaction.setAmount(resultSet.getInt("amount"));
                transaction.setSenderAccountId((Integer) resultSet.getObject("sender_account_id"));
                transaction.setBeneficiaryAccountId((Integer) resultSet.getObject("beneficiary_account_id"));
                transaction.setCreatedAt(resultSet.getDate("created_at").toLocalDate());
                transaction.setOperationType(Transaction.Operation
                        .valueOf(resultSet.getObject("operation_type").toString()));

                transactions.add(transaction);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return transactions;
    }

    public Transaction findById(int id) {
        Transaction transaction = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Transactions WHERE id = ?");
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            transaction = new Transaction();
            transaction.setId(resultSet.getInt("id"));
            transaction.setAmount(resultSet.getInt("amount"));
            transaction.setSenderAccountId((Integer) resultSet.getObject("sender_account_id"));
            transaction.setBeneficiaryAccountId((Integer) resultSet.getObject("beneficiary_account_id"));
            transaction.setCreatedAt(resultSet.getDate("created_at").toLocalDate());
            transaction.setOperationType(Transaction.Operation
                    .valueOf(resultSet.getObject("operation_type").toString()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return transaction;
    }


}
