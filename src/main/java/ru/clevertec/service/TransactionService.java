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

    /**
     * Возвращает список всех транзакций, находящихся в базе данных
     *
     * @return список обектов Transaction
     */
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

    /**
     * Возращает транзакцию по заданному идентификатору
     *
     * @param id int, идентификатор нужной транзакции
     * @return объект Transaction
     */
    public Transaction findById(int id) {
        Transaction transaction = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM Transactions WHERE id = ?");
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

    /**
     * Добавляет транзакцию в базу данных
     *
     * @param newTransaction объект Transaction, который будет добавлен в базу данных
     */
    public void create(Transaction newTransaction)
    {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO Transactions(senderAccountId, beneficiaryAccountId, amount, " +
                            "createdAt, operationType) VALUES(?, ?, ?, ?, ?)");
            preparedStatement.setObject(1, newTransaction.getSenderAccountId(), Types.INTEGER);
            preparedStatement.setObject(2, newTransaction.getBeneficiaryAccountId(), Types.INTEGER);
            preparedStatement.setDouble(3, newTransaction.getAmount());
            preparedStatement.setDate(4, Date.valueOf(newTransaction.getCreatedAt()));
            preparedStatement.setObject(5, newTransaction.getOperationType(), Types.OTHER);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Обновляет уже существующую транзакцию, присваивая ей новые значения
     *
     * @param newTransaction объект Transaction, которым будет замещена текущая транзакия
     * @param id int, индентификатор транзакции, которая будет обновлена
     */
    public void update(Transaction newTransaction, int id)
    {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE Transactions SET senderAccountId = ?, beneficiaryAccountId = ?, amount = ?, " +
                            "createdAt = ?, operationType = ? WHERE id = ?");
            preparedStatement.setObject(1, newTransaction.getSenderAccountId(), Types.INTEGER);
            preparedStatement.setObject(2, newTransaction.getBeneficiaryAccountId(), Types.INTEGER);
            preparedStatement.setDouble(3, newTransaction.getAmount());
            preparedStatement.setDate(4, Date.valueOf(newTransaction.getCreatedAt()));
            preparedStatement.setObject(5, newTransaction.getOperationType(), Types.OTHER);
            preparedStatement.setInt(6, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Удаляет транзакцию по заданному идентификатору
     *
     * @param id int, идентификатор транзакции
     */
    public void delete(int id)
    {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM Transactions WHERE id = ?");
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
