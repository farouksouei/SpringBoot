package ma.enset.backend.dtos;

import lombok.Data;
import ma.enset.backend.enums.OperationType;

import java.util.Date;

@Data

public class AccountOperationDTO {
    private Long id;
    private Date dateOperation;
    private double amount;
    private String description;
    private OperationType operationType;
}
