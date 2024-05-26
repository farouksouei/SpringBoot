package ma.enset.backend.services;

import lombok.AllArgsConstructor;
import ma.enset.backend.dtos.*;
import ma.enset.backend.entities.*;
import ma.enset.backend.enums.AccountStatus;
import ma.enset.backend.enums.OperationType;
import ma.enset.backend.exceptions.BalanceNotSufficientException;
import ma.enset.backend.exceptions.BankAccountNotFoundException;
import ma.enset.backend.exceptions.CustomerNotFoundException;
import ma.enset.backend.mappers.BankMapper;
import ma.enset.backend.repositories.AccountOperationRepository;
import ma.enset.backend.repositories.BankAccountRepository;
import ma.enset.backend.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class BankServiceImpl implements BankService {
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankMapper bankMapper;

    @Override
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public CustomerDTO saveCustomerDTO(CustomerDTO customerDTO) {
        Customer customer = customerRepository.save(bankMapper.fromCustomerDTO(customerDTO));
        return bankMapper.fromCustomer(customer);
    }

    @Override
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public List<CustomerDTO> getCustomersDTO() {
        return getCustomers().stream()
                .map(customer -> bankMapper.fromCustomer(customer))
                .collect(Collectors.toList());
    }

    @Override
    public Customer getCustomer(Long id) throws CustomerNotFoundException {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found!!!"));
    }

    @Override
    public CustomerDTO getCustomerDTO(Long id) throws CustomerNotFoundException {
        return bankMapper.fromCustomer(getCustomer(id));
    }

    @Override
    public Customer updateCustomer(Long id, Customer customer) throws CustomerNotFoundException {
        Customer oldCustomer = getCustomer(id);
        oldCustomer.setName(customer.getName());
        oldCustomer.setEmail(customer.getEmail());
        return customerRepository.save(oldCustomer);
    }

    @Override
    public CustomerDTO updateCustomerDTO(Long id, CustomerDTO customerDTO) throws CustomerNotFoundException {
        Customer oldCustomer = getCustomer(id);
        oldCustomer.setName(customerDTO.getName());
        oldCustomer.setEmail(customerDTO.getEmail());
        customerRepository.save(oldCustomer);
        return bankMapper.fromCustomer(oldCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }


    @Override
    public CurrentAccount saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer = getCustomer(customerId);
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCreatedAt(new Date());
        currentAccount.setCustomer(customer);
        currentAccount.setAccountStatus(AccountStatus.CREATED);
        currentAccount.setOverDraft(overDraft);
        return bankAccountRepository.save(currentAccount);
    }

    @Override
    public CurrentAccountDTO saveCurrentBankAccountDTO(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {

        return bankMapper.fromCurrentBankAccount(saveCurrentBankAccount(initialBalance, overDraft, customerId));
    }

    @Override
    public SavingAccount saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer = getCustomer(customerId);
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCreatedAt(new Date());
        savingAccount.setCustomer(customer);
        savingAccount.setAccountStatus(AccountStatus.CREATED);
        savingAccount.setInterestRate(interestRate);
        return bankAccountRepository.save(savingAccount);
    }

    @Override
    public SavingAccountDTO saveSavingBankAccountDTO(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        return bankMapper.fromSavingBankAccount(saveSavingBankAccount(initialBalance, interestRate, customerId));
    }

    @Override
    public BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException {
        return bankAccountRepository.findById(accountId).orElseThrow(() -> new BankAccountNotFoundException("Bank Account Not Found!!!"));
    }

    @Override
    public BankAccountDTO getBankAccountDTO(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = getBankAccount(accountId);
        if (bankAccount instanceof SavingAccount) {
            return bankMapper.fromSavingBankAccount((SavingAccount) bankAccount);
        } else {
            return bankMapper.fromCurrentBankAccount((CurrentAccount) bankAccount);
        }
    }


    @Override
    public List<BankAccount> getBankAccounts() {
        return bankAccountRepository.findAll();
    }

    @Override
    public List<BankAccountDTO> getBankAccountsDTO() {
        return bankAccountRepository.findAll().stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                return bankMapper.fromSavingBankAccount((SavingAccount) bankAccount);
            } else {
                return bankMapper.fromCurrentBankAccount((CurrentAccount) bankAccount);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public List<SavingAccountDTO> getSavingAccounts() {
        return bankAccountRepository.findAll().stream()
                .filter(bankAccount -> bankAccount instanceof SavingAccount)
                .map(bankAccount -> bankMapper.fromSavingBankAccount((SavingAccount) bankAccount))
                .collect(Collectors.toList());
    }

    @Override
    public List<CurrentAccountDTO> getCurrentAccounts() {
        return bankAccountRepository.findAll().stream()
                .filter(bankAccount -> bankAccount instanceof CurrentAccount)
                .map(bankAccount -> bankMapper.fromCurrentBankAccount((CurrentAccount) bankAccount))
                .collect(Collectors.toList());
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = getBankAccount(accountId);
        if (bankAccount.getBalance() < amount) {
            throw new BalanceNotSufficientException("Not enough bank account");
        }

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setOperationType(OperationType.DEBIT);
        accountOperation.setDateOperation(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setDescription(description);
        accountOperation.setAmount(amount);
        accountOperationRepository.save(accountOperation);

        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = getBankAccount(accountId);

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setOperationType(OperationType.CREDIT);
        accountOperation.setDateOperation(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setDescription(description);
        accountOperation.setAmount(amount);
        accountOperationRepository.save(accountOperation);

        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String fromAccountId, String toAccountId, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(fromAccountId, amount, "Transferring the amount " + amount + " to " + toAccountId);

        credit(toAccountId, amount, "Receiving " + amount + " from " + fromAccountId);
    }

    @Override
    public List<BankAccount> findBankAccountsByCustomerId(Long customerId) {
        return customerRepository.findBankAccountsByCustomerId(customerId);
    }

    @Override
    public List<BankAccountDTO> findBankAccountsByCustomerIdDTO(Long customerId) {
        return customerRepository.findBankAccountsByCustomerId(customerId).stream()
                .map(bankAccount -> bankMapper.fromBankAccount(bankAccount))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findBankAccountIdsByCustomerId(Long customerId) {
        return customerRepository.findBankAccountsByCustomerId(customerId).stream()
                .map(BankAccount::getId)
                .collect(Collectors.toList());
    }


    @Override
    public List<AccountOperation> accountOperationsHistory(String accountId) {
        return accountOperationRepository.findByBankAccount_Id(accountId);

    }

    @Override
    public List<AccountOperationDTO> accountOperationsHistoryDTO(String accountId) {
        return accountOperationsHistory(accountId).stream()
                .map(accountOperation -> bankMapper.fromAccountOperation(accountOperation))
                .collect(Collectors.toList());

    }

    @Override
    public AccountHistoryDTO getAccountHistoryDTO(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = getBankAccount(accountId);
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        accountHistoryDTO.setAccountId(accountId);
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationsHistoryDTO(accountId));
        return accountHistoryDTO;
    }

    @Override
    public List<String> getBankAccountIdsForTransaction() {
        return bankAccountRepository.findAll().stream()
                .map(BankAccount::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<SavingAccountDTO> savingAccountsDTOByUserId(Long id){
        List<String> bankAccountIds = findBankAccountIdsByCustomerId(id);
        List<SavingAccountDTO> savingAccountDTOs = new ArrayList<>();

        for(String bankAccountId : bankAccountIds) {
            try {
                BankAccountDTO bankAccountDTO = getBankAccountDTO(bankAccountId);
                if(bankAccountDTO instanceof SavingAccountDTO){
                    savingAccountDTOs.add((SavingAccountDTO) bankAccountDTO);
                }
            } catch (BankAccountNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return savingAccountDTOs;
    }

    @Override
    public List<CurrentAccountDTO> currentAccountsDTOByUserId(Long id){
        List<String> bankAccountIds = findBankAccountIdsByCustomerId(id);
        List<CurrentAccountDTO> currentAccountDTOS = new ArrayList<>();

        for(String bankAccountId : bankAccountIds) {
            try {
                BankAccountDTO bankAccountDTO = getBankAccountDTO(bankAccountId);
                if(bankAccountDTO instanceof CurrentAccountDTO){
                    currentAccountDTOS.add((CurrentAccountDTO) bankAccountDTO);
                }
            } catch (BankAccountNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return currentAccountDTOS;
    }

    @Override
    public void deleteAccount(String id){
        bankAccountRepository.deleteById(id);
    }
}
