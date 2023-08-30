import Model.Account;
import Model.Bank;
import Model.User;
import Service.AccountService;
import Service.BankService;
import Service.TransactionService;
import Service.UserService;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class CleverBankCLI {

    private static TransactionService transactionService = new TransactionService();
    private static BankService bankService = new BankService();
    private static UserService userService = new UserService();
    private static AccountService accountService = new AccountService();

    public static void main(String[] args)
    {
        accountService.startPercentage();

        System.out.println("START WORK");
        //accountService.create(new Account(1, 8, 500));
        //accountService.transfer(2, 3, 100);
        //userService.delete(7);
        //userService.create(new User("Egor", "12345678", new ArrayList<Account>()));
        //bankService.create(new Bank("CleverBank", new ArrayList<Account>()));
        System.out.println(bankService.findAll());
        System.out.println(userService.findAll());
        System.out.println("END WORK");

        System.out.println(new Scanner(System.in).nextLine());
        accountService.stopPercentage();
    }
}
