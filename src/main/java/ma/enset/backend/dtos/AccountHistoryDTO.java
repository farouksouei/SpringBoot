package ma.enset.backend.dtos;

import lombok.Data;

import java.util.List;

@Data
public class AccountHistoryDTO {
    private String accountId;
    private List<AccountOperationDTO> accountOperationDTOS;
    private double balance;

}
