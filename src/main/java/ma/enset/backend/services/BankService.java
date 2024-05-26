package ma.enset.backend.services;

import ma.enset.backend.dtos.*;
import ma.enset.backend.entities.*;
import ma.enset.backend.exceptions.BalanceNotSufficientException;
import ma.enset.backend.exceptions.BankAccountNotFoundException;
import ma.enset.backend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankService {
    Customer saveCustomer(Customer customer);

    CustomerDTO saveCustomerDTO(CustomerDTO customerDTO);

    List<Customer> getCustomers();

    List<CustomerDTO> getCustomersDTO();

    Customer getCustomer(Long id) throws CustomerNotFoundException;


    CustomerDTO getCustomerDTO(Long id) throws CustomerNotFoundException;

    Customer updateCustomer(Long id, Customer customer) throws CustomerNotFoundException;

    CustomerDTO updateCustomerDTO(Long id, CustomerDTO customerDTO) throws CustomerNotFoundException;

    void deleteCustomer(Long id);


    CurrentAccount saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;

    CurrentAccountDTO saveCurrentBankAccountDTO(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;

    SavingAccount saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;

    SavingAccountDTO saveSavingBankAccountDTO(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;

    BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException;

    BankAccountDTO getBankAccountDTO(String accountId) throws BankAccountNotFoundException;

    List<BankAccount> getBankAccounts();

    List<BankAccountDTO> getBankAccountsDTO();

    List<SavingAccountDTO> getSavingAccounts();

    List<CurrentAccountDTO> getCurrentAccounts();

    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String fromAccountId, String toAccountId, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;


    List<BankAccount> findBankAccountsByCustomerId(Long customerId);

    List<BankAccountDTO> findBankAccountsByCustomerIdDTO(Long customerId);

    List<String> findBankAccountIdsByCustomerId(Long customerId);

    List<AccountOperation> accountOperationsHistory(String accountId);

    List<AccountOperationDTO> accountOperationsHistoryDTO(String accountId);

    AccountHistoryDTO getAccountHistoryDTO(String accountId) throws BankAccountNotFoundException;

    List<String> getBankAccountIdsForTransaction();

    List<SavingAccountDTO> savingAccountsDTOByUserId(Long id);

    List<CurrentAccountDTO> currentAccountsDTOByUserId(Long id);

    void deleteAccount(String id);
}
