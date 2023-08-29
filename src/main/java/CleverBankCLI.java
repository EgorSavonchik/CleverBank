import Model.Account;
import Model.Bank;
import Model.User;
import Service.AccountService;
import Service.BankService;
import Service.TransactionService;
import Service.UserService;

import java.util.ArrayList;

public class CleverBankCLI {
    public static void main(String[] args)
    {
        TransactionService transactionService = new TransactionService();
        BankService bankService = new BankService();
        UserService userService = new UserService();
        AccountService accountService = new AccountService();
        System.out.println("START WORK");
        //accountService.create(new Account(1, 8, 100));
        accountService.replenishmentFunds(2, 100);
        //userService.delete(7);
        //userService.create(new User("Egor", "12345678", new ArrayList<Account>()));
        //bankService.create(new Bank("CleverBank", new ArrayList<Account>()));
        System.out.println(bankService.findAll());
        System.out.println(userService.findAll());
        System.out.println("END WORK");
    }
}
