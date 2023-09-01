package ru.clevertec;

import ru.clevertec.model.Account;
import ru.clevertec.model.Bank;
import ru.clevertec.model.User;
import ru.clevertec.service.*;

import java.util.ArrayList;

public class CleverBankCLI {

    private static TransactionService transactionService = new TransactionService();
    private static BankService bankService = new BankService();
    private static UserService userService = new UserService();
    private static AccountService accountService = new AccountService();
    private static CheckService checkService = new CheckService();

    public static void main(String[] args)
    {
        //accountService.startPercentage();

        //accountService.replenishmentFunds(1, 100);
        //accountService.withdrawalFunds(2, 200);
        //accountService.transfer(1, 2, 100);
        //userService.create(new User("Kirill", "12345678", new ArrayList<>()));
        //bankService.create(new Bank("Mega bank", new ArrayList<>()));
        //accountService.withdrawalFunds(1, 50);
        checkService.generateTransactionStatement(accountService.findById(1));
        checkService.generateStatementMoney(accountService.findById(1));
        //accountService.create(new Account(1, 1, 100));
        //accountService.replenishmentFunds(1, 100);
        //accountService.create(new Account(1, 2, 500));
        //accountService.replenishmentFunds(2, 300);
        //accountService.transfer(1, 2, 100);
        System.out.println(transactionService.findAll());
        System.out.println(accountService.findAll());
        System.out.println(accountService.getAccountRelatedTransactions(2));
        //accountService.stopPercentage();
    }
}
