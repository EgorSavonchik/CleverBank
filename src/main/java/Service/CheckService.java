package Service;

import Model.Transaction;

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
public class CheckService
{
    private static int checkNumber = 1;
    private static AccountService accountService = new AccountService();
    private static BankService bankService = new BankService();
    private final int bankAccountNumberLength = 10;
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
                        " ".repeat(36 - 16 - bankAccountNumberLength)
                        + accountService.findById(transaction.getBeneficiaryAccountId()).getAccountNumber() + " |" + "\n");
                writer.write("| " + "Счет отправителя:" +
                        " ".repeat(36 - 17 - bankAccountNumberLength)
                        + accountService.findById(transaction.getSenderAccountId()).getAccountNumber() + " |" + "\n");
            } else if (transaction.getOperationType() == Transaction.Operation.REPLENISHMENT) {
                writer.write("| " + "Банк клиента:" +
                        " ".repeat(36 - 13 - bankService.findByAccountId(transaction.getBeneficiaryAccountId()).getName().length())
                        + bankService.findByAccountId(transaction.getBeneficiaryAccountId()).getName() + " |" + "\n");
                writer.write("| " + "Счет клиента:" +
                        " ".repeat(36 - 13 - bankAccountNumberLength)
                        + accountService.findById(transaction.getBeneficiaryAccountId()).getAccountNumber() + " |" + "\n");
            } else if (transaction.getOperationType() == Transaction.Operation.WITHDRAWAL) {
                writer.write("| " + "Банк клиента:" +
                        " ".repeat(36 - 13 - bankService.findByAccountId(transaction.getSenderAccountId()).getName().length())
                        + bankService.findByAccountId(transaction.getSenderAccountId()).getName() + " |" + "\n");
                writer.write("| " + "Счет клиента:" +
                        " ".repeat(36 - 13 - bankAccountNumberLength)
                        + accountService.findById(transaction.getSenderAccountId()).getAccountNumber() + " |" + "\n");
            }

            writer.write("| " + "Сумма:"
                    + " ".repeat(36 - 6 - 4 - decimalFormat.format(transaction.getAmount()).length()) +
                    decimalFormat.format(transaction.getAmount()) + " BYN" + " |" + "\n");
            writer.write("|" + "_".repeat(38) + "|" + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
