package ru.clevertec.service;

import ru.clevertec.model.Account;
import ru.clevertec.model.Transaction;
import org.yaml.snakeyaml.Yaml;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AccountService {
    private static Connection connection;
    private Thread timer;
    private static CheckService checkService = new CheckService();

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

    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();

        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Accounts");

            while (resultSet.next()) {
                Account account = new Account();

                account.setOwnerBankId(resultSet.getInt("owner_bank_id"));
                account.setOwnerUserId(resultSet.getInt("owner_user_id"));
                account.setAmount(resultSet.getDouble("amount"));
                account.setAccountNumber("0".repeat(10 - resultSet.getString("id").length())
                        + resultSet.getString("id"));
                account.setCreatedAt(resultSet.getDate("created_at").toLocalDate());

                accounts.add(account);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return accounts;
    }

    public Account findById(int id) {
        Account account = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE Accounts.id=?");
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            account = new Account();

            account.setOwnerUserId(resultSet.getInt("owner_user_id"));
            account.setOwnerBankId(resultSet.getInt("owner_bank_id"));
            account.setAmount(resultSet.getDouble("amount"));
            account.setAccountNumber("0".repeat(10 - resultSet.getString("id").length())
                    + resultSet.getString("id"));
            account.setCreatedAt(resultSet.getDate("created_at").toLocalDate());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return account;
    }

    public void create(Account newAccount) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Accounts(owner_bank_id," +
                    " owner_user_id, amount) VALUES(?, ?, ?)");

            preparedStatement.setInt(1, newAccount.getOwnerBankId());
            preparedStatement.setInt(2, newAccount.getOwnerUserId());
            preparedStatement.setDouble(3, newAccount.getAmount());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Account> findByBankId(int bankId) {
        List<Account> accounts = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE Accounts.owner_bank_id = ?");
            preparedStatement.setInt(1, bankId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Account account = new Account();

                account.setOwnerBankId(resultSet.getInt("owner_bank_id"));
                account.setOwnerUserId(resultSet.getInt("owner_user_id"));
                account.setAmount(resultSet.getDouble("amount"));
                account.setAccountNumber("0".repeat(10 - resultSet.getString("id").length())
                        + resultSet.getString("id"));
                account.setCreatedAt(resultSet.getDate("created_at").toLocalDate());

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

            while (resultSet.next()) {
                Account account = new Account();

                account.setOwnerBankId(resultSet.getInt("owner_bank_id"));
                account.setOwnerUserId(resultSet.getInt("owner_user_id"));
                account.setAmount(resultSet.getDouble("amount"));
                account.setAccountNumber("0".repeat(10 - resultSet.getString("id").length())
                        + resultSet.getString("id"));
                account.setCreatedAt(resultSet.getDate("created_at").toLocalDate());

                accounts.add(account);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return accounts;
    }

    public void delete(int id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Accounts WHERE Accounts.id=?");
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void replenishmentFunds(int id, double quantity) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "BEGIN;" +
                        "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;" +
                        "UPDATE Accounts SET amount = amount + ? WHERE id = ?;" +
                        "INSERT INTO Transactions(sender_account_id, beneficiary_account_id, amount, operation_type) VALUES(?, ?, ?, ?);" +
                        "COMMIT;");

            preparedStatement.setDouble(1, quantity);
            preparedStatement.setInt(2, id);
            preparedStatement.setNull(3, Types.INTEGER);
            preparedStatement.setInt(4, id);
            preparedStatement.setDouble(5, quantity);
            preparedStatement.setObject(6, Transaction.Operation.REPLENISHMENT, Types.OTHER);

            preparedStatement.executeUpdate();

            checkService.generateCheck(new Transaction(null, id, quantity, Transaction.Operation.REPLENISHMENT));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void withdrawalFunds(int id, double quantity) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "BEGIN;" +
                        "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;" +
                        "UPDATE Accounts SET amount = amount - ? WHERE id = ?;" +
                        "INSERT INTO Transactions(sender_account_id, beneficiary_account_id, amount, operation_type) VALUES(?, ?, ?, ?);" +
                        "COMMIT;");

            preparedStatement.setDouble(1, quantity);
            preparedStatement.setInt(2, id);
            preparedStatement.setInt(3, id);
            preparedStatement.setNull(4, Types.INTEGER);
            preparedStatement.setDouble(5, quantity);
            preparedStatement.setObject(6, Transaction.Operation.WITHDRAWAL, Types.OTHER);

            preparedStatement.executeUpdate();

            checkService.generateCheck(new Transaction(id, null, quantity, Transaction.Operation.WITHDRAWAL));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void transfer(int sender_id, int recipient_id, double quantity) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "BEGIN;" +
                        "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;" +
                        "UPDATE Accounts SET amount = amount - ? WHERE id = ?;" +
                        "UPDATE Accounts SET amount = amount + ? WHERE id = ?;" +
                        "INSERT INTO Transactions(sender_account_id, beneficiary_account_id, amount, operation_type) VALUES(?, ?, ?, ?);" +
                        "COMMIT;");

            preparedStatement.setDouble(1, quantity);
            preparedStatement.setInt(2, sender_id);
            preparedStatement.setDouble(3, quantity);
            preparedStatement.setInt(4, recipient_id);
            preparedStatement.setInt(5, sender_id);
            preparedStatement.setInt(6, recipient_id);
            preparedStatement.setDouble(7, quantity);
            preparedStatement.setObject(8, Transaction.Operation.TRANSFER, Types.OTHER);

            preparedStatement.executeUpdate();

            checkService.generateCheck(new Transaction(sender_id, recipient_id, quantity, Transaction.Operation.TRANSFER));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void startPercentage() {
        timer = new Thread(new Runnable() {
            private Month currentDate = LocalDate.now().getMonth();
            @Override
            public void run() {
                while (true) {
                    try {
                        if (currentDate.plus(1) == LocalDate.now().getMonth()) {
                            currentDate = LocalDate.now().getMonth();

                            PreparedStatement preparedStatement = connection.prepareStatement(
                                    "UPDATE Accounts SET amount = amount * (100 + ?) / 100;");

                            InputStream configurationInputStream
                                    = ClassLoader.getSystemResourceAsStream("application.yml");
                            if(configurationInputStream != null) {
                                Map<String, Object> map = new Yaml().load(configurationInputStream);

                                preparedStatement.setInt(1, (int) map.get("interest_rate"));

                                preparedStatement.executeUpdate();
                            } else {
                                throw new FileNotFoundException();
                            }
                        }

                        Thread.sleep(30 * 1000);
                    } catch (InterruptedException ex) {
                        break;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        timer.start();
    }

    public void stopPercentage()
    {
        timer.interrupt();
    }
}
