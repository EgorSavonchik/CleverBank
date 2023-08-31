package Service;

import Model.Account;
import Model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserService
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

    public List<User> findAll()
    {
        List<User> users = new ArrayList<>();

        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Users");

            while (resultSet.next())
            {
                User user = new User();

                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
                user.setAccountList(new AccountService().findByUserId(resultSet.getInt("id")));

                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    public User findById(int id)
    {
        User user = null;

        try {
            PreparedStatement userPreparedStatement = connection.prepareStatement("SELECT * FROM Users WHERE Users.id=?");
            userPreparedStatement.setInt(1, id);

            ResultSet resultSet = userPreparedStatement.executeQuery();

            user = new User();
            user.setName(resultSet.getString("name"));
            user.setPassword(resultSet.getString("password"));

            List<Account> accountList = new ArrayList<>();

            PreparedStatement accountPreparedStatement = connection.prepareStatement("SELECT * from accounts WHERE Accounts.owner_user_id=?");
            accountPreparedStatement.setInt(1, id);

            ResultSet accountResultSet = accountPreparedStatement.executeQuery();

            while (accountResultSet.next())
            {
                accountList.add(new Account(accountResultSet.getInt("owner_bank_id")
                        , id, accountResultSet.getDouble("amount")));
            }

            user.setAccountList(accountList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public void create(User newUser)
    {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Users(name, password) VALUES(?, ?)");
            preparedStatement.setString(1, newUser.getName());
            preparedStatement.setString(2, newUser.getPassword());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id)
    {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Users WHERE Users.id=?");
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
