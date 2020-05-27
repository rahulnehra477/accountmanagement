package com.example.accountmanagement.Services;

import com.example.accountmanagement.Dao.exception.DAOException;
import com.example.accountmanagement.Model.Account;
import com.example.accountmanagement.Model.AccountDTO;
import com.example.accountmanagement.Services.exception.ServiceException;
import reactor.core.publisher.Mono;

public interface IAccountService {
    Mono<Account> createAccount(AccountDTO customer) throws ServiceException;

    Mono<Account> getAccountByCustomerId(String customerId) throws DAOException;

    Mono<Account> updateAccountDetails(Account existing) throws ServiceException;

    Mono<Object> deleteAccountByCustomerId(String customerId) throws DAOException;

    Mono<String> deleteCustomerByCustomerId(String customerId) throws ServiceException;

    Mono<Account> validateByCustomerIdAndAccountNum(String customerId, String senderAccount) throws DAOException;

    Mono<Account> validateByAccountNum(String receiverAccount) throws DAOException;
}
