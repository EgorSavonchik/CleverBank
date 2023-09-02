package ru.clevertec.service;



import com.itextpdf.text.*;
import ru.clevertec.model.Account;
import ru.clevertec.model.Transaction;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.*;
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
        String directoryPath = "check"; // Относительный путь к директории "check"
        String fileName = "check" + checkNumber + ".txt"; // Название файла
        File directory = new File(directoryPath);

        File file = new File(directory, fileName);
        try (FileWriter writer = new FileWriter(file)) {
            file.createNewFile();

            writer.write("_".repeat(40) + "\n");
            writer.write("| " + " ".repeat(13) + "Bank check" + " ".repeat(13) + " |" + "\n");
            writer.write("| " + "Check" +
                    " ".repeat(36 - 5 - String.valueOf(checkNumber).length()) + checkNumber + " |" + "\n");
            writer.write("| " + LocalDate.now() + " ".repeat(36 - LocalDate.now().toString().length()
                    - LocalTime.now().withNano(0).toString().length()) + LocalTime.now().withNano(0)
                    .toString() + " |" + "\n");
            writer.write("| " + "Transaction type:" +
                    " ".repeat(36 - 17 - transaction.getOperationType().toString().length()) + transaction.getOperationType().toString() + " |" + "\n");

            if (transaction.getOperationType() == Transaction.Operation.TRANSFER) {
                writer.write("| " + "Payee's bank:" +
                        " ".repeat(36 - 13 - bankService.findByAccountId(transaction.getBeneficiaryAccountId()).getName().length())
                        + bankService.findByAccountId(transaction.getBeneficiaryAccountId()).getName() + " |" + "\n");
                writer.write("| " + "Sender's bank:" +
                        " ".repeat(36 - 14 - bankService.findByAccountId(transaction.getSenderAccountId()).getName().length())
                        + bankService.findByAccountId(transaction.getSenderAccountId()).getName() + " |" + "\n");
                writer.write("| " + "Beneficiary's account:" +
                        " ".repeat(36 - 22 - BANK_ACCOUNT_NUMBER_LENGTH)
                        + accountService.findById(transaction.getBeneficiaryAccountId()).getAccountNumber() + " |" + "\n");
                writer.write("| " + "Sender's account:" +
                        " ".repeat(36 - 17 - BANK_ACCOUNT_NUMBER_LENGTH)
                        + accountService.findById(transaction.getSenderAccountId()).getAccountNumber() + " |" + "\n");
            } else if (transaction.getOperationType() == Transaction.Operation.REPLENISHMENT) {
                writer.write("| " + "Client bank:" +
                        " ".repeat(36 - 12 - bankService.findByAccountId(transaction.getBeneficiaryAccountId()).getName().length())
                        + bankService.findByAccountId(transaction.getBeneficiaryAccountId()).getName() + " |" + "\n");
                writer.write("| " + "Client account:" +
                        " ".repeat(36 - 15 - BANK_ACCOUNT_NUMBER_LENGTH)
                        + accountService.findById(transaction.getBeneficiaryAccountId()).getAccountNumber() + " |" + "\n");
            } else if (transaction.getOperationType() == Transaction.Operation.WITHDRAWAL) {
                writer.write("| " + "Client bank:" +
                        " ".repeat(36 - 12 - bankService.findByAccountId(transaction.getSenderAccountId()).getName().length())
                        + bankService.findByAccountId(transaction.getSenderAccountId()).getName() + " |" + "\n");
                writer.write("| " + "Client account:" +
                        " ".repeat(36 - 15 - BANK_ACCOUNT_NUMBER_LENGTH)
                        + accountService.findById(transaction.getSenderAccountId()).getAccountNumber() + " |" + "\n");
            }

            writer.write("| " + "Amount:"
                    + " ".repeat(36 - 7 - 4 - decimalFormat.format(transaction.getAmount()).length()) +
                    decimalFormat.format(transaction.getAmount()) + " BYN" + " |" + "\n");
            writer.write("|" + "_".repeat(38) + "|" + "\n");

            checkNumber++;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void generateTransactionStatementTxt(Account account, StatementFormat statementFormat) {
        LocalDate period = account.getCreatedAt();

        if (statementFormat == StatementFormat.MONTH && period.isBefore(LocalDate.now().minusMonths(1))) {
            period = LocalDate.now().minusMonths(1);
        } else if (statementFormat == StatementFormat.YEAR && period.isBefore(LocalDate.now().minusYears(1))) {
            period = LocalDate.now().minusYears(1);
        }

        String directoryPath = "statement-transaction"; // Относительный путь к директории "check"
        String fileName = account.getAccountNumber() + "_" + period + "_" + LocalDate.now() +  ".txt"; // Название файла
        File directory = new File(directoryPath);

        File file = new File(directory, fileName);
        try (FileWriter writer = new FileWriter(file)) {
            file.createNewFile();

            writer.write(" ".repeat(40) + "Statement" + "\n");
            writer.write(" ".repeat(44 - bankService.findById(account.getOwnerBankId()).getName().length() / 2)
                    + bankService.findById(account.getOwnerBankId()).getName() + "\n");
            writer.write("Client" + " ".repeat(35 - 6) + "| " +
                    userService.findById(account.getOwnerUserId()).getName() + "\n");
            writer.write("Account" + " ".repeat(35 - 7) + "| " + account.getAccountNumber() + "\n");
            writer.write("Currency" + " ".repeat(35 - 8) + "| BYN" + "\n");
            writer.write("Opening date" + " ".repeat(35 - 12) + "| " + account.getCreatedAt().toString() + "\n");
            writer.write("Period" + " ".repeat(35 - 6) + "| " + period + " - " + LocalDate.now() + "\n");
            writer.write("Date and time of formation" + " ".repeat(35 - 26) + "| "
                    + LocalDate.now() + ", " + LocalTime.now().withSecond(0).withNano(0) + "\n");
            writer.write("Remainder" + " ".repeat(35 - 9) + "| "
                    + decimalFormat.format(account.getAmount()) + " BYN" + "\n");
            writer.write(" ".repeat(6) + "Date" + " ".repeat(5) + "|"
                    + " ".repeat(22)+ "Note" + " ".repeat(22) + "|" + " ".repeat(4) + "Amount" + "\n");
            writer.write("-".repeat(80) + "\n");

            List<Transaction> accountTransactions = accountService.getAccountRelatedTransactions(account.getId(), period, LocalDate.now());

            for (Transaction transaction : accountTransactions) {
                switch (transaction.getOperationType()) {
                    case REPLENISHMENT -> {
                        writer.write(transaction.getCreatedAt().toString() + " ".repeat(5) + "| "
                                + "Replenishment of funds" + " ".repeat(47 - 22) + "| "
                                + decimalFormat.format(transaction.getAmount()) + " BYN" + "\n");
                    }
                    case WITHDRAWAL -> {
                        writer.write(transaction.getCreatedAt().toString() + " ".repeat(5) + "| "
                                + "Withdrawals" + " ".repeat(47 - 11) + "| -"
                                + decimalFormat.format(transaction.getAmount()) + " BYN" + "\n");
                    }
                    case TRANSFER -> {
                        if (transaction.getSenderAccountId() == account.getId()) {
                            writer.write(transaction.getCreatedAt().toString() + " ".repeat(5) + "| "
                                    + "Transfer to user " + userService.findById(transaction.getBeneficiaryAccountId()).getName()
                                    + " ".repeat(47 - 17 - userService.findById(transaction.getBeneficiaryAccountId()).getName()
                                    .length()) + "| -" + decimalFormat.format(transaction.getAmount()) + " BYN" + "\n");
                        } else {
                            writer.write(transaction.getCreatedAt().toString() + " ".repeat(5) + "| "
                                    + "Transfer by user " + userService.findById(transaction.getSenderAccountId()).getName()
                                    + " ".repeat(47 - 17 - userService.findById(transaction.getSenderAccountId()).getName()
                                    .length()) + "| " + decimalFormat.format(transaction.getAmount()) + " BYN" + "\n");
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateTransactionStatementPdf(Account account, StatementFormat statementFormat) {
        LocalDate period = account.getCreatedAt();

        if (statementFormat == StatementFormat.MONTH && period.isBefore(LocalDate.now().minusMonths(1))) {
            period = LocalDate.now().minusMonths(1);
        } else if (statementFormat == StatementFormat.YEAR && period.isBefore(LocalDate.now().minusYears(1))) {
            period = LocalDate.now().minusYears(1);
        }

        String directoryPath = "statement-transaction"; // Относительный путь к директории
        String fileName = account.getAccountNumber() + "_" + period + "_" + LocalDate.now() +  ".pdf"; // Название файла

        Font font = FontFactory.getFont("fonts/ofont.ru_Courier New.ttf", "Identity-H", true);
        try (FileOutputStream fs = new FileOutputStream(new File(directoryPath, fileName))) {
            Document document = new Document();
            PdfWriter.getInstance(document, fs);
            document.open();

            document.add(new Paragraph(" ".repeat(32) + "Statement" + "\n", font));
            document.add(new Paragraph(" ".repeat(36 - bankService.findById(account.getOwnerBankId()).getName().length() / 2)
                    + bankService.findById(account.getOwnerBankId()).getName() + "\n", font));
            document.add(new Paragraph("Client" + " ".repeat(35 - 6) + "| " +
                    userService.findById(account.getOwnerUserId()).getName() + "\n", font));
            document.add(new Paragraph("Account" + " ".repeat(35 - 7) + "| " + account.getAccountNumber() + "\n", font));
            document.add(new Paragraph("Currency" + " ".repeat(35 - 8) + "| BYN" + "\n", font));
            document.add(new Paragraph("Opening date" + " ".repeat(35 - 12) + "| " + account.getCreatedAt().toString() + "\n", font));
            document.add(new Paragraph("Period" + " ".repeat(35 - 6) + "| " + period + " - " + LocalDate.now() + "\n", font));
            document.add(new Paragraph("Date and time of formation" + " ".repeat(35 - 26) + "| "
                    + LocalDate.now() + ", " + LocalTime.now().withSecond(0).withNano(0) + "\n", font));
            document.add(new Paragraph("Remainder" + " ".repeat(35 - 9) + "| "
                    + decimalFormat.format(account.getAmount()) + " BYN" + "\n", font));
            document.add(new Paragraph(" ".repeat(6) + "Date" + " ".repeat(5) + "|"
                    + " ".repeat(18)+ "Note" + " ".repeat(18) + "|" + " ".repeat(4) + "Amount" + "\n", font));
            document.add(new Paragraph("-".repeat(72) + "\n", font));

            List<Transaction> accountTransactions = accountService.getAccountRelatedTransactions(account.getId(), period, LocalDate.now());

            for (Transaction transaction : accountTransactions) {
                switch (transaction.getOperationType()) {
                    case REPLENISHMENT -> {
                        document.add(new Paragraph(transaction.getCreatedAt().toString() + " ".repeat(5) + "| "
                                + "Replenishment of funds" + " ".repeat(39 - 22) + "| "
                                + decimalFormat.format(transaction.getAmount()) + " BYN" + "\n", font));
                    }
                    case WITHDRAWAL -> {
                        document.add(new Paragraph(transaction.getCreatedAt().toString() + " ".repeat(5) + "| "
                                + "Withdrawals" + " ".repeat(39 - 11) + "| -"
                                + decimalFormat.format(transaction.getAmount()) + " BYN" + "\n", font));
                    }
                    case TRANSFER -> {
                        if (transaction.getSenderAccountId() == account.getId()) {
                            document.add(new Paragraph(transaction.getCreatedAt().toString() + " ".repeat(5) + "| "
                                    + "Transfer to user " + userService.findById(transaction.getBeneficiaryAccountId()).getName()
                                    + " ".repeat(39 - 17 - userService.findById(transaction.getBeneficiaryAccountId()).getName()
                                    .length()) + "| -" + decimalFormat.format(transaction.getAmount()) + " BYN" + "\n", font));
                        } else {
                            document.add(new Paragraph(transaction.getCreatedAt().toString() + " ".repeat(5) + "| "
                                    + "Transfer by user " + userService.findById(transaction.getSenderAccountId()).getName()
                                    + " ".repeat(39 - 17 - userService.findById(transaction.getSenderAccountId()).getName()
                                    .length()) + "| " + decimalFormat.format(transaction.getAmount()) + " BYN" + "\n", font));
                        }
                    }
                }
            }

            document.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

    }

    public void generateStatementMoney(Account account, LocalDate startPeriod, LocalDate endPeriod) {
        String filePath = "statement-money";

        if (startPeriod.isBefore(account.getCreatedAt())) {
            startPeriod = account.getCreatedAt();
        }

        String fileName = account.getAccountNumber() + "_" + startPeriod + "_" + endPeriod + ".pdf";

        Font font = FontFactory.getFont("fonts/ofont.ru_Courier New.ttf", "Identity-H", true);
        try (FileOutputStream fs = new FileOutputStream(new File(filePath, fileName))) {
            Document document = new Document();
            PdfWriter.getInstance(document, fs);
            document.open();

            document.add(new Paragraph(" ".repeat(40) + "Money statement", font));
            document.add(new Paragraph(" ".repeat(47 - bankService.findById(account.getOwnerBankId()).getName().length() / 2)
                    + bankService.findById(account.getOwnerBankId()).getName(), font));
            document.add(new Paragraph("Client" + " ".repeat(35 - 6) + "| " +
                    userService.findById(account.getOwnerUserId()).getName(), font));
            document.add(new Paragraph("Account" + " ".repeat(35 - 7) + "| " + account.getAccountNumber(), font));
            document.add(new Paragraph("Currency" + " ".repeat(35 - 8) + "| BYN", font));
            document.add(new Paragraph("Opening date" + " ".repeat(35 - 12) + "| " + account.getCreatedAt().toString(), font));
            document.add(new Paragraph("Period" + " ".repeat(35 - 6) + "| " + account.getCreatedAt() + " - " + LocalDate.now(), font));
            document.add(new Paragraph("Date and time of formation" + " ".repeat(35 - 26) + "| "
                    + LocalDate.now() + ", " + LocalTime.now().withSecond(0).withNano(0), font));
            document.add(new Paragraph("Remainder" + " ".repeat(35 - 9) + "| "
                    + decimalFormat.format(account.getAmount()) + " BYN", font));

            List<Transaction> accountTransactions = accountService.getAccountRelatedTransactions(account.getId(), account.getCreatedAt(),
                    LocalDate.now());
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

            document.add(new Paragraph(" ".repeat(17) + "Parish" + " ".repeat(7) + "|" + " ".repeat(8) + "Care", font));
            document.add(new Paragraph(" ".repeat(10) + "-".repeat(41), font));
            document.add(new Paragraph(" ".repeat(12) + decimalFormat.format(parish)
                    + " ".repeat(18 - decimalFormat.format(parish).length()) + "|" + " ".repeat(2)
                    + "-" + decimalFormat.format(care), font));

            document.close();
        }
        catch (DocumentException exc) {} catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public enum StatementFormat {
        MONTH, YEAR, ALL_TIME
    }
}
