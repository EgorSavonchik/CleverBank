package Service;

import Model.Account;
import Model.Bank;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AccountService
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

    public List<Account> findAll()
    {
        List<Account> accounts = new ArrayList<>();

        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Accounts");

            while (resultSet.next())
            {
                Account account = new Account();

                account.setOwnerBankId(resultSet.getInt("owner_bank_id"));
                account.setOwnerUserId(resultSet.getInt("owner_user_id"));
                account.setAmount(resultSet.getInt("amount"));

                accounts.add(account);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return accounts;
    }

    public Account findById(int id)
    {
        Account account = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE Accounts.id=?");
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            account = new Account();

            account.setOwnerUserId(resultSet.getInt("owner_user_id"));
            account.setOwnerBankId(resultSet.getInt("owner_bank_id"));
            account.setAmount(resultSet.getInt("amount"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return account;
    }

    public void create(Account newAccount)
    {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Accounts(owner_bank_id," +
                    " owner_user_id, amount) VALUES(?, ?, ?)");

            preparedStatement.setInt(1, newAccount.getOwnerBankId());
            preparedStatement.setInt(2, newAccount.getOwnerUserId());
            preparedStatement.setInt(3, newAccount.getAmount());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Account> findByBankId(int bankId)
    {
        List<Account> accounts = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE Accounts.owner_bank_id = ?");
            preparedStatement.setInt(1, bankId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                Account account = new Account();

                account.setOwnerBankId(resultSet.getInt("owner_bank_id"));
                account.setOwnerUserId(resultSet.getInt("owner_user_id"));
                account.setAmount(resultSet.getInt("amount"));

                accounts.add(account);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return accounts;
    }

    public List<Account> findByUserId(int userId)
    {
        List<Account> accounts = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE Accounts.owner_user_id = ?");
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                Account account = new Account();

                account.setOwnerBankId(resultSet.getInt("owner_bank_id"));
                account.setOwnerUserId(resultSet.getInt("owner_user_id"));
                account.setAmount(resultSet.getInt("amount"));

                accounts.add(account);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return accounts;
    }

    public void delete(int id)
    {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Accounts WHERE Accounts.id=?");
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void replenishmentFunds(int id, int quantity)
    {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE Accounts SET amount = amount + ? WHERE id = ?");
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void withdrawalFunds(int id, int quantity)
    {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE Accounts SET amount = amount - ? WHERE id = ?");
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
