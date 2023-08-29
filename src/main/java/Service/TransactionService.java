package Service;

import Model.Transaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionService
{
    private static Connection connection;

    {
        ResourceBundle bundle = ResourceBundle.getBundle("database");

        String url = bundle.getString("url");
        String username = bundle.getString("username");
        String password = bundle.getString("password");

        try
        {
            Class.forName(bundle.getString("driver"));
        }
        catch (ClassNotFoundException exception)
        {
            exception.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Transaction> findAll()
    {
        List<Transaction> transactions = new ArrayList<>();

        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Transactions");

            while (resultSet.next())
            {
                Transaction transaction = new Transaction();

                transaction = new Transaction();
                transaction.setAmount(resultSet.getInt("amount"));
                transaction.setSenderAccountId(resultSet.getInt("sender_account_id"));
                transaction.setBeneficiaryAccountId(resultSet.getInt("beneficiary_account_id"));

                transactions.add(transaction);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return transactions;
    }

    public Transaction findById(int id)
    {
        Transaction transaction = null;

        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Transactions");

            transaction = new Transaction();
            transaction.setAmount(resultSet.getInt("amount"));
            transaction.setSenderAccountId(resultSet.getInt("sender_account_id"));
            transaction.setBeneficiaryAccountId(resultSet.getInt("beneficiary_account_id"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return transaction;
    }


}
