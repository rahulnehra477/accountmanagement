package com.example.accountmanagement.Controller;

import com.example.accountmanagement.Dao.exception.DAOException;
import com.example.accountmanagement.Model.*;

import com.example.accountmanagement.Services.IAccountService;
import com.example.accountmanagement.Services.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/account/")
public class AccountManageController {

    @Autowired
    IAccountService iAccountService;


    @PostMapping(value = "/create")
    Mono<Account> createAccount(@Valid @RequestBody AccountDTO accountDTO) throws ServiceException {
        return iAccountService.createAccount(accountDTO);
    }


    @GetMapping(value = "/getaccount/{customerId}")
    Mono<ResponseEntity<Account>> getAccountByCustomerId(@PathVariable String customerId) throws DAOException {
        return iAccountService.getAccountByCustomerId(customerId).map(customer -> new ResponseEntity<>(customer,HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @PutMapping(value = "/update/{customerId}")
    Mono<ResponseEntity<Account>> updateCustomer(@PathVariable String customerId,@RequestBody Account account) throws DAOException {
        return iAccountService.getAccountByCustomerId(customerId).flatMap(existing-> {
            existing.setAccountStatus(account.getAccountStatus());
            existing.setBalance(account.getBalance());
            try {
                return iAccountService.updateAccountDetails(existing);
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return null;
        }).map(custo-> new ResponseEntity<>(custo,HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED));

    }

    @DeleteMapping(value = "/delete/{customerId}")
    Mono<ResponseEntity<String>> deleteByCustomerId(@PathVariable String customerId) throws DAOException {
        return iAccountService.deleteAccountByCustomerId(customerId)
                .flatMap(obj-> {
                    try {
                        return iAccountService.deleteCustomerByCustomerId(customerId);
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .map(customer -> new ResponseEntity<>("Account and Customer deleted successfully",HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>("Customer does not exist",HttpStatus.NOT_FOUND));

    }

    @PostMapping(value = "/validate/update")
    Mono<Boolean> validateAndUpdateAccountForTransaction(@RequestBody Transaction transaction) throws DAOException {
        return iAccountService.validateByCustomerIdAndAccountNum(transaction.getCustomerId(),transaction.getSenderAccount()).flatMap(obj-> {
            try {
                obj.setBalance(obj.getBalance() - transaction.getAmount());
                return iAccountService.updateAccountDetails(obj);
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return null;
        }).hasElement()
                .filter(obj->obj.booleanValue())
                .flatMap( obj-> {
                    try {
                        return iAccountService.validateByAccountNum(transaction.getReceiverAccount());
                    } catch (DAOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).flatMap(obj-> {
                    try {
                        obj.setBalance(obj.getBalance() + transaction.getAmount());
                        return iAccountService.updateAccountDetails(obj);
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).hasElement();
    }

   /* @PutMapping(value = "/update/balance/")
    Mono<Boolean> updateAccountBalance(@RequestBody Transaction transaction) throws DAOException {
        return iAccountService.validateByCustomerIdAndAccountNum(transaction.getCustomerId(),transaction.getSenderAccount()).map(obj-> {
            try {
                obj.setBalance(obj.getBalance() - transaction.getAmount());
                return iAccountService.updateAccountDetails(obj);
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return null;
        }).hasElement()
                .filter(obj->obj.booleanValue())
                .flatMap( obj-> {
                    try {
                        return iAccountService.validateByAccountNum(transaction.getReceiverAccount());
                    } catch (DAOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).map(obj-> {
                    try {
                        obj.setBalance(obj.getBalance() + transaction.getAmount());
                        return iAccountService.updateAccountDetails(obj);
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).hasElement();

    }
*/

    /*@PostMapping(value = "/validate/update")
    Mono<Boolean> validateAccountForTransaction(@RequestBody Transaction transaction) throws DAOException {
        return iAccountService.validateByCustomerIdAndAccountNum(transaction.getCustomerId(),transaction.getSenderAccount()).hasElement()
                .filter(obj->obj.booleanValue())
                .flatMap(obj-> {
                    try {
                        return iAccountService.validateByAccountNum(transaction.getReceiverAccount()).hasElement();
                    } catch (DAOException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }*/

}
