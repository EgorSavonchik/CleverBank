package Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private Integer senderAccountId;
    private Integer beneficiaryAccountId;
    private double amount;
    private Operation operationType;


    public enum Operation
    {
        REPLENISHMENT,
        WITHDRAWAL,
        TRANSFER;
    }
}

