package ru.clevertec.service;

import ru.clevertec.model.Account;
import ru.clevertec.model.Transaction;
import ru.clevertec.model.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.nio.file.Files;
import java.util.List;

public class CheckService {
    private static int checkNumber = 1;
    private static AccountService accountService = new AccountService();
    private static BankService bankService = new BankService();
    private static UserService userService = new UserService();
    private final int BANK_ACCOUNT_NUMBER_LENGTH = 10;
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    {
        String directoryPath = "check"; // Путь к директории в корне проекта
        Path path = Paths.get(directoryPath);
        int count = 0;

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, "*.txt")) {
            for (Path file : directoryStream) {
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        checkNumber = count + 1;
    }

    public void generateCheck(Transaction transaction) {
        String operationType = null;

        switch (transaction.getOperationType()) {
            case TRANSFER -> {
                operationType = "Перевод";
            }
            case REPLENISHMENT -> {
                operationType = "Пополнение";
            }
            case WITHDRAWAL -> {
                operationType = "Снятие";
            }
        }

        String directoryPath = "check"; // Относительный путь к директории "check"
        String fileName = "check" + checkNumber + ".txt"; // Название файла
        File directory = new File(directoryPath);

        File file = new File(directory, fileName);
        try (FileWriter writer = new FileWriter(file)) {
            file.createNewFile();

            writer.write("_".repeat(40) + "\n");
            writer.write("| " + " ".repeat(11) + "Банковский чек" + " ".repeat(11) + " |" + "\n");
            writer.write("| " + "Чек" +
                    " ".repeat(36 - 3 - String.valueOf(checkNumber).length()) + checkNumber + " |" + "\n");
            writer.write("| " + LocalDate.now() + " ".repeat(36 - LocalDate.now().toString().length()
                    - LocalTime.now().withNano(0).toString().length()) + LocalTime.now().withNano(0)
                    .toString() + " |" + "\n");
            writer.write("| " + "Тип транзакции:" +
                    " ".repeat(36 - 15 - operationType.length() / 2) + operationType + " |" + "\n");

            if (transaction.getOperationType() == Transaction.Operation.TRANSFER) {
                writer.write("| " + "Банк получателя:" +
                        " ".repeat(36 - 16 - bankService.findByAccountId(transaction.getBeneficiaryAccountId()).getName().length())
                        + bankService.findByAccountId(transaction.getBeneficiaryAccountId()).getName() + " |" + "\n");
                writer.write("| " + "Банк отправителя:" +
                        " ".repeat(36 - 17 - bankService.findByAccountId(transaction.getSenderAccountId()).getName().length())
                        + bankService.findByAccountId(transaction.getSenderAccountId()).getName() + " |" + "\n");
                writer.write("| " + "Счет получателя:" +
                        " ".repeat(36 - 16 - BANK_ACCOUNT_NUMBER_LENGTH)
                        + accountService.findById(transaction.getBeneficiaryAccountId()).getAccountNumber() + " |" + "\n");
                writer.write("| " + "Счет отправителя:" +
                        " ".repeat(36 - 17 - BANK_ACCOUNT_NUMBER_LENGTH)
                        + accountService.findById(transaction.getSenderAccountId()).getAccountNumber() + " |" + "\n");
            } else if (transaction.getOperationType() == Transaction.Operation.REPLENISHMENT) {
                writer.write("| " + "Банк клиента:" +
                        " ".repeat(36 - 13 - bankService.findByAccountId(transaction.getBeneficiaryAccountId()).getName().length())
                        + bankService.findByAccountId(transaction.getBeneficiaryAccountId()).getName() + " |" + "\n");
                writer.write("| " + "Счет клиента:" +
                        " ".repeat(36 - 13 - BANK_ACCOUNT_NUMBER_LENGTH)
                        + accountService.findById(transaction.getBeneficiaryAccountId()).getAccountNumber() + " |" + "\n");
            } else if (transaction.getOperationType() == Transaction.Operation.WITHDRAWAL) {
                writer.write("| " + "Банк клиента:" +
                        " ".repeat(36 - 13 - bankService.findByAccountId(transaction.getSenderAccountId()).getName().length())
                        + bankService.findByAccountId(transaction.getSenderAccountId()).getName() + " |" + "\n");
                writer.write("| " + "Счет клиента:" +
                        " ".repeat(36 - 13 - BANK_ACCOUNT_NUMBER_LENGTH)
                        + accountService.findById(transaction.getSenderAccountId()).getAccountNumber() + " |" + "\n");
            }

            writer.write("| " + "Сумма:"
                    + " ".repeat(36 - 6 - 4 - decimalFormat.format(transaction.getAmount()).length()) +
                    decimalFormat.format(transaction.getAmount()) + " BYN" + " |" + "\n");
            writer.write("|" + "_".repeat(38) + "|" + "\n");

            checkNumber++;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateTransactionStatement(Account account) {
        System.out.println(" ".repeat(40) + "Выписка");
        System.out.println(" ".repeat(44 - bankService.findById(account.getOwnerBankId()).getName().length() / 2)
                + bankService.findById(account.getOwnerBankId()).getName());
        System.out.println("Клиент" + " ".repeat(35 - 6) + "| " +
                userService.findById(account.getOwnerUserId()).getName());
        System.out.println("Счет" + " ".repeat(35 - 4) + "| " + account.getAccountNumber());
        System.out.println("Валюта" + " ".repeat(35 - 6) + "| BYN");
        System.out.println("Дата открытия" + " ".repeat(35 - 13) + "| " + account.getCreatedAt().toString());
        System.out.println("Период" + " ".repeat(35 - 6) + "| " + account.getCreatedAt().toString()
                + " - " + LocalDate.now());
        System.out.println("Дата и время формирования" + " ".repeat(35 - 25) + "| "
                + LocalDate.now() + ", " + LocalTime.now().withSecond(0).withNano(0));
        System.out.println("Остаток" + " ".repeat(35 - 7) + "| "
                + decimalFormat.format(account.getAmount()) + " BYN");
        System.out.println(" ".repeat(6) + "Дата" + " ".repeat(5) + "|"
                + " ".repeat(19)+ "Примечание" + " ".repeat(19) + "|" + " ".repeat(5) + "Сумма");
        System.out.println("-".repeat(80));

        List<Transaction> accountTransactions = accountService.getAccountRelatedTransactions(account.getId());

        for (Transaction transaction : accountTransactions) {
            switch (transaction.getOperationType()) {
                case REPLENISHMENT -> {
                    System.out.println(transaction.getCreatedAt().toString() + " ".repeat(5) + "| "
                            + "Пополнение средств" + " ".repeat(29) + "| "
                            + decimalFormat.format(transaction.getAmount()) + " BYN");
                }
                case WITHDRAWAL -> {
                    System.out.println(transaction.getCreatedAt().toString() + " ".repeat(5) + "| "
                            + "Снятие средств" + " ".repeat(33) + "| -"
                            + decimalFormat.format(transaction.getAmount()) + " BYN");
                }
                case TRANSFER -> {
                    if (transaction.getSenderAccountId() == account.getId()) {
                        System.out.println(transaction.getCreatedAt().toString() + " ".repeat(5) + "| "
                                + "Перевод пользователю " + userService.findById(transaction.getBeneficiaryAccountId()).getName()
                                + " ".repeat(26 - userService.findById(transaction.getBeneficiaryAccountId()).getName()
                                .length()) + "| -" + decimalFormat.format(transaction.getAmount()) + " BYN");
                    } else {
                        System.out.println(transaction.getCreatedAt().toString() + " ".repeat(5) + "| "
                                + "Перевод от пользователя " + userService.findById(transaction.getSenderAccountId()).getName()
                                + " ".repeat(24 - userService.findById(transaction.getSenderAccountId()).getName()
                                .length()) + "| " + decimalFormat.format(transaction.getAmount()) + " BYN");
                    }
                }
            }
        }
    }

    public void generateStatementMoney(Account account)
    {
        System.out.println(" ".repeat(40) + "Money statement");
        System.out.println(" ".repeat(44 - bankService.findById(account.getOwnerBankId()).getName().length() / 2)
                + bankService.findById(account.getOwnerBankId()).getName());
        System.out.println("Клиент" + " ".repeat(35 - 6) + "| " +
                userService.findById(account.getOwnerUserId()).getName());
        System.out.println("Счет" + " ".repeat(35 - 4) + "| " + account.getAccountNumber());
        System.out.println("Валюта" + " ".repeat(35 - 6) + "| BYN");
        System.out.println("Дата открытия" + " ".repeat(35 - 13) + "| " + account.getCreatedAt().toString());
        System.out.println("Период" + " ".repeat(35 - 6) + "| " + account.getCreatedAt().toString()
                + " - " + LocalDate.now());
        System.out.println("Дата и время формирования" + " ".repeat(35 - 25) + "| "
                + LocalDate.now() + ", " + LocalTime.now().withSecond(0).withNano(0));
        System.out.println("Остаток" + " ".repeat(35 - 7) + "| "
                + decimalFormat.format(account.getAmount()) + " BYN");

        List<Transaction> accountTransactions = accountService.getAccountRelatedTransactions(account.getId());
        int parish = 0, care = 0;

        for (Transaction transaction : accountTransactions) {
            switch (transaction.getOperationType()) {
                case REPLENISHMENT -> {
                    parish += transaction.getAmount();
                }
                case WITHDRAWAL -> {
                    care += transaction.getAmount();
                }
                case TRANSFER -> {
                    if (transaction.getSenderAccountId() == account.getId()) {
                        care += transaction.getAmount();
                    } else {
                        parish += transaction.getAmount();
                    }
                }
            }
        }

        System.out.println(" ".repeat(17) + "Приход" + " ".repeat(7) + "|" + " ".repeat(8) + "Уход");
        System.out.println(" ".repeat(10) + "-".repeat(41));
        System.out.println(" ".repeat(12) + decimalFormat.format(parish)
                + " ".repeat(18 - decimalFormat.format(parish).length()) + "|" + " ".repeat(2)
                + "-" + decimalFormat.format(care));
    }
}
