package com.example.accountmanagement.Services;

import com.example.accountmanagement.Dao.AccountRepository;
import com.example.accountmanagement.Dao.exception.DAOException;
import com.example.accountmanagement.Model.Account;
import com.example.accountmanagement.Model.AccountDTO;
import com.example.accountmanagement.Services.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

@Service
public class AccountService implements IAccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public Mono<Account> createAccount(AccountDTO accountDTO) throws ServiceException {

        Account account=new Account();
        account.setCustomerId(accountDTO.getCustomerId());
        account.setUserName(accountDTO.getUserName());
        //Generate account number
        account.setAccountNumber(this.generateAccountNumber());
        account.setAccountStatus("Active");
        account.setBalance(0);
        Mono<Account> accountMono=accountRepository.save(account);
        return accountMono;
    }

    @Override
    public Mono<Account> getAccountByCustomerId(String customerId) throws DAOException {
        return accountRepository.findByCustomerId(customerId);
    }

    @Override
    public Mono<Account> updateAccountDetails(Account existing)throws ServiceException {
        return accountRepository.save(existing);
    }

    @Override
    public Mono<Object> deleteAccountByCustomerId(String customerId) throws DAOException {
        return accountRepository.deleteByCustomerId(customerId);
    }

    @Override
    public Mono<String> deleteCustomerByCustomerId(String customerId) throws ServiceException {
        Mono<String> result = webClientBuilder.build().delete()
                .uri( "http://customer/customer/delete/" + customerId)
                .retrieve()
                .bodyToMono(String.class);
        return result;
    }

    @Override
    public Mono<Account> validateByCustomerIdAndAccountNum(String customerId, String senderAccount) throws DAOException {
        return accountRepository.findByCustomerIdAndAccountNumber(customerId,senderAccount);
    }

    @Override
    public Mono<Account> validateByAccountNum(String receiverAccount) throws DAOException {
        return accountRepository.findByAccountNumber(receiverAccount);
    }

    private String generateAccountNumber() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return "SBS000" + String.valueOf(timestamp.getTime()).substring(3);
    }


}
