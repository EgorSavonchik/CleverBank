package ru.clevertec;

import ru.clevertec.service.*;

import java.time.LocalDate;

public class CleverBankConsole {

    private static TransactionService transactionService = new TransactionService();
    private static BankService bankService = new BankService();
    private static UserService userService = new UserService();
    private static AccountService accountService = new AccountService();
    private static CheckService checkService = new CheckService();

    public static void main(String[] args)
    {
        accountService.startPercentage();

        checkService.generateTransactionStatementPdf(accountService.findById(1), CheckService.StatementFormat.YEAR);
        checkService.generateTransactionStatementPdf(accountService.findById(2), CheckService.StatementFormat.YEAR);
        checkService.generateStatementMoney(accountService.findById(1), LocalDate.now().minusMonths(1),
                .now());

        System.out.println(accountService.findAll());
        System.out.println(bankService.findAll());
        System.out.println(userService.findAll());
        accountService.stopPercentage();
    }
}
