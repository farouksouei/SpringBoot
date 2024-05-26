package ma.enset.backend.web;

import lombok.AllArgsConstructor;
import ma.enset.backend.dtos.*;
import ma.enset.backend.exceptions.BalanceNotSufficientException;
import ma.enset.backend.exceptions.BankAccountNotFoundException;
import ma.enset.backend.services.BankService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@AllArgsConstructor
public class BankAccountRestController {
    private BankService bankService;

    @GetMapping("/bank-accounts")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<BankAccountDTO> getAllBankAccounts() {
        return bankService.getBankAccountsDTO();
    }

    @GetMapping("/saving-accounts")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<SavingAccountDTO> getAllSavingAccountsDTO() {
        return bankService.getSavingAccounts();
    }

    @GetMapping("/current-accounts")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<CurrentAccountDTO> getAllCurrentAccountsDTO() {
        return bankService.getCurrentAccounts();
    }

    @GetMapping("/bank-accounts/userId/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<BankAccountDTO> getBankAccountByUserId(@PathVariable Long id) {
        return bankService.findBankAccountsByCustomerIdDTO(id);
    }

    @GetMapping("/saving-accounts/userId/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<SavingAccountDTO> savingAccountsDTOByUserId(@PathVariable Long id) {
        return bankService.savingAccountsDTOByUserId(id);
    }

    @GetMapping("/current-accounts/userId/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<CurrentAccountDTO> currentAccountsDTOByUserId(@PathVariable Long id) {
        return bankService.currentAccountsDTOByUserId(id);
    }

    @GetMapping("/bank-accounts/{id}")
    public BankAccountDTO getBankAccount(@PathVariable String id) throws BankAccountNotFoundException {
        return bankService.getBankAccountDTO(id);
    }

    @GetMapping("/accounts-by-id/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<String> getBanAccountsIdsFromCustomerId(@PathVariable Long id) {
        return bankService.findBankAccountIdsByCustomerId(id);
    }

    @GetMapping("/accounts-ids-transaction")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<String> getBankIdsForTransaction(){
        return bankService.getBankAccountIdsForTransaction();
    }

    @GetMapping("/account-operations/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<AccountOperationDTO> getAccountOperationsHistoryDTO(@PathVariable String id) throws BankAccountNotFoundException {
        return bankService.accountOperationsHistoryDTO(id);
    }

    @GetMapping("/account-operations/history/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public AccountHistoryDTO getAccountHistoryDTO(@PathVariable String id) throws BankAccountNotFoundException {
        return bankService.getAccountHistoryDTO(id);
    }

    @PostMapping("/submit-transaction")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public void transaction(@RequestBody TransactionDTO transactionDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankService.transfer(transactionDTO.getFromAccount(), transactionDTO.getToAccount(), transactionDTO.getAmount());
    }

    @DeleteMapping("/delete-account/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public void deleteAccount(@PathVariable String id){
       bankService.deleteAccount(id);
    }
}
