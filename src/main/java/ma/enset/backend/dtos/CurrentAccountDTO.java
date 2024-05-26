package ma.enset.backend.dtos;

import lombok.Data;
import ma.enset.backend.enums.AccountStatus;

import java.util.Date;

@Data
public class CurrentAccountDTO extends BankAccountDTO {
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus accountStatus;
    private CustomerDTO customerDTO;
    private double overDraft;

}
