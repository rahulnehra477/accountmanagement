package com.example.accountmanagement.Dao;

import com.example.accountmanagement.Dao.exception.DAOException;
import com.example.accountmanagement.Model.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {

    Mono<Account> findByCustomerId(String customerId) throws DAOException;

    Mono<Object> deleteByCustomerId(String customerId) throws DAOException;

    Mono<Account> findByCustomerIdAndAccountNumber(String customerId, String senderAccount) throws DAOException;

    Mono<Account> findByAccountNumber(String receiverAccount) throws DAOException;
}
