package ma.enset.backend.dtos;

import lombok.Data;

@Data
public class TransactionDTO {
    private String fromAccount;
    private String toAccount;
    private double amount;
}
