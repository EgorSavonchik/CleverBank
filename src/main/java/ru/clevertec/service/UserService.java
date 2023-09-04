package ru.clevertec.service;

import ru.clevertec.model.Account;
import ru.clevertec.model.User;

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

    /**
     * Возвращает список всех пользователей, находящихся в базе данных
     *
     * @return список обектов User
     */
    public List<User> findAll()
    {
        List<User> users = new ArrayList<>();

        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Users");

            while (resultSet.next())
            {
                User user = new User();

                user.setId(resultSet.getInt("id"));
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

    /**
     * Возращает пользователя по заданному идентификатору
     *
     * @param id int, идентификатор нужного пользователя
     * @return объект User, счет с заданным идентификатором
     */
    public User findById(int id)
    {
        User user = null;

        try {
            PreparedStatement userPreparedStatement = connection.prepareStatement("SELECT * FROM Users WHERE Users.id=?");
            userPreparedStatement.setInt(1, id);

            ResultSet resultSet = userPreparedStatement.executeQuery();
            resultSet.next();

            user = new User();
            user.setId(resultSet.getInt("id"));
            user.setName(resultSet.getString("name"));
            user.setPassword(resultSet.getString("password"));

            List<Account> accountList = new ArrayList<>();

            PreparedStatement accountPreparedStatement = connection.prepareStatement("SELECT * from accounts WHERE Accounts.owner_user_id=?");
            accountPreparedStatement.setInt(1, id);

            ResultSet accountResultSet = accountPreparedStatement.executeQuery();

            while (accountResultSet.next()) {
                accountList.add(new Account(accountResultSet.getInt("id"),
                        accountResultSet.getInt("owner_bank_id"),
                        id, accountResultSet.getDouble("amount")));
            }

            user.setAccountList(accountList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    /**
     * Добавляет нового пользователя в базу данных
     *
     * @param newUser объект User, который будет добавлен в базу данных
     */
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

    /**
     * Обновляет уже существующего пользователя, присваивая ему новые значения
     *
     * @param newUser объект User, которым будет замещен текущий
     * @param id int, индентификатор пользователя, который будет обновлен
     */
    public void update(User newUser, int id)
    {
        try {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE Users SET name = ?, password = ? WHERE id = ?");
        preparedStatement.setString(1, newUser.getName());
        preparedStatement.setString(2, newUser.getPassword());
        preparedStatement.setInt(3, id);

        preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Удаляет пользователя по заданному идентификатору
     *
     * @param id int, идентификатор пользователя
     */
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
