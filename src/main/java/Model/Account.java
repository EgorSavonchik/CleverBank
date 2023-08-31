package Model;

import Service.AccountService;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account
{
    private int ownerBankId;
    private int ownerUserId;
    private double amount;
    private String accountNumber;

    public Account(int ownerBankId, int ownerUserId, double amount)
    {
        this.ownerBankId = ownerBankId;
        this.ownerUserId = ownerUserId;
        this.amount = amount;
        this.accountNumber = null;
    }
}
