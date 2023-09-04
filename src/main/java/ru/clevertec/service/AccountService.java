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

    /**
     * Возвращает список всех счетов, находящихся в базе данных
     *
     * @return список обектов Account, всех счетов из базы данных
     */
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();

        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Accounts");

            while (resultSet.next()) {
                Account account = new Account();

                account.setId(resultSet.getInt("id"));
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

    /**
     * Возращает счет по заданному идентификатору
     *
     * @param id int, идентификатор нужного счета
     * @return объект Account, счет с заданным идентификатором
     */
    public Account findById(int id) {
        Account account = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM Accounts WHERE Accounts.id=?");
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            account = new Account();

            account.setId(resultSet.getInt("id"));
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

    /**
     * Добавляет счет в базу данных
     *
     * @param newAccount объект Account, который будет добавлен в базу данных
     */
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

    /**
     * Обновляет уже существующий счет, присваивая ему новые значения
     *
     * @param newAccount объект Account, которым будет замещен текущий
     * @param id int, индентификатор счета, который будет обновлен
     */
    public void update(Account newAccount, int id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Accounts SET owner_bank_id= ?," +
                    " owner_user_id = ?, amount = ? WHERE id = ?");

            preparedStatement.setInt(1, newAccount.getOwnerBankId());
            preparedStatement.setInt(2, newAccount.getOwnerUserId());
            preparedStatement.setDouble(3, newAccount.getAmount());
            preparedStatement.setInt(4, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Возвращает все счета, которые принадлежат банку с заданным идентификатором
     * (у которых ownerBankId равен заданному идентификатору)
     *
     * @param bankId int, идентификатор банка
     * @return список объектов Account
     */
    public List<Account> findByBankId(int bankId) {
        List<Account> accounts = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts " +
                    "WHERE Accounts.owner_bank_id = ?");
            preparedStatement.setInt(1, bankId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Account account = new Account();

                account.setId(resultSet.getInt("id"));
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

    /**
     * Возвращает все счета, которые принадлежат пользователю с заданным идентификатором
     * (у которых ownerUserId равен заданному идентификатору)
     *
     * @param userId int, идентификатор пользователя
     * @return список объектов Account
     */
    public List<Account> findByUserId(int userId)
    {
        List<Account> accounts = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts " +
                    "WHERE Accounts.owner_user_id = ?");
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Account account = new Account();

                account.setId(resultSet.getInt("id"));
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

    /**
     * Удаляет счет по заданному идентификатору
     *
     * @param id int, идентификатор счета
     */
    public void delete(int id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM Accounts WHERE Accounts.id=?");
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Производит операцию пополнения заданного счета на заданную сумму
     *
     * @param id int, идентификатор счета, на который поступает зачисление
     * @param quantity double, количество зачисленной валюты
     */
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

    /**
     * Производит операцию снятия заданной суммы с заданного счета
     *
     * @param id int, идентификатор счета, с которого происходит снятие
     * @param quantity double, количество снятой валюты
     */
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

    /**
     * производит операцию передачи валюты с одного счета на другой
     *
     * @param sender_id int, идентификатор отправителя
     * @param recipient_id int, идентификатор получателя
     * @param quantity double, сумма перевода
     */
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

    /**
     * Возвращает транзакции, в которых учавствовал счет с заданныхм идентификатором, произошедшие
     * в заданный промежуток времени
     *
     * @param id int, идентификатор счета
     * @param periodStart LocalDate, начало периода, в котором выбираются транзакции
     * @param periodEnd LocalDate, конец периода
     * @return список объектов Transaction, найденные транзакции
     */
    public List<Transaction> getAccountRelatedTransactions(int id, LocalDate periodStart, LocalDate periodEnd) {
        List<Transaction> transactionList = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Transactions " +
                    "WHERE (Transactions.sender_account_id = ? OR Transactions.beneficiary_account_id = ?) AND " +
                    "created_at BETWEEN ? AND ?;");

            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, id);
            preparedStatement.setDate(3, Date.valueOf(periodStart));
            preparedStatement.setDate(4, Date.valueOf(periodEnd));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                Transaction transaction = new Transaction();

                transaction.setId(resultSet.getInt("id"));
                transaction.setAmount(resultSet.getInt("amount"));
                transaction.setSenderAccountId((Integer) resultSet.getObject("sender_account_id"));
                transaction.setBeneficiaryAccountId((Integer) resultSet.getObject("beneficiary_account_id"));
                transaction.setCreatedAt(resultSet.getDate("created_at").toLocalDate());
                transaction.setOperationType(Transaction.Operation
                        .valueOf(resultSet.getObject("operation_type").toString()));

                transactionList.add(transaction);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return transactionList;
    }

    /**
     * Запускает проверки, которые будут происходить раз в 30 секунд, на конец месяца, в конце месяца зачисляет
     * на счет проценты от текущей суммы на счету. Процентная ставка указывается в конфигурационном
     * файле(переменная interest_rate). Требует закрытия при завершении работы функцией closePercentage.
     */
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

    /**
     * Функция, прекращающая проверки на конец месяца, вызванные функцией startPercentage
     */
    public void stopPercentage()
    {
        timer.interrupt();
    }
}
