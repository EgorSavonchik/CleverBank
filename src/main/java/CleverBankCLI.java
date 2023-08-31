import Model.Account;
import Model.Bank;
import Model.Transaction;
import Model.User;
import Service.*;
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

        accountService.replenishmentFunds(1, 100);
        accountService.withdrawalFunds(2, 200);
        accountService.transfer(1, 2, 100);

        accountService.stopPercentage();
    }
}
