package ru.clevertec;

import ru.clevertec.service.AccountService;
import ru.clevertec.service.BankService;
import ru.clevertec.service.TransactionService;
import ru.clevertec.service.UserService;

public class CleverBankCLI {

    private static TransactionService transactionService = new TransactionService();
    private static BankService bankService = new BankService();
    private static UserService userService = new UserService();
    private static AccountService accountService = new AccountService();

    public static void main(String[] args)
    {
        accountService.startPercentage();

        //accountService.replenishmentFunds(1, 100);
        //accountService.withdrawalFunds(2, 200);
        //accountService.transfer(1, 2, 100);
        //userService.create(new User("Egor", "12345678", new ArrayList<>()));
        //bankService.create(new Bank("CleverBank", new ArrayList<>()));

        //accountService.create(new Account(1, 1, 100));
        //accountService.replenishmentFunds(1, 100);
        System.out.println(transactionService.findById(1));
        System.out.println(accountService.findAll());

        accountService.stopPercentage();
    }
}
