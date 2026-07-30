package com.bank.transaction_service.repository;

import com.bank.transaction_service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    @Query("""

            SELECT t
FROM Transaction t
WHERE t.fromAccountNumber = :accountNumber
   OR t.toAccountNumber = :accountNumber
ORDER BY t.createdAt DESC
""")
    List<Transaction> findAllByAccountNumber(
            @Param("accountNumber") String accountNumber);
}
