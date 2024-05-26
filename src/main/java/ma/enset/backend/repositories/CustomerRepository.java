package ma.enset.backend.repositories;

import ma.enset.backend.entities.BankAccount;
import ma.enset.backend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT c.bankAccounts FROM Customer c WHERE c.id = :customerId")
    List<BankAccount> findBankAccountsByCustomerId(Long customerId);

    
}
