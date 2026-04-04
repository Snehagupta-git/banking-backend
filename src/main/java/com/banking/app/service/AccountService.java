package com.banking.app.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.banking.app.dto.AccountDto;
import com.banking.app.dto.AdminTransferRequestDto;
import com.banking.app.dto.TransactionDto;
import com.banking.app.dto.TransferRequestDto;
import com.banking.app.entity.Account;

public interface AccountService {
	AccountDto createAccount(AccountDto account,Authentication authentication);
	
	AccountDto getAccountById(Long id);
         
	AccountDto deposit(Long id,double amount);
	
	AccountDto withdraw(Long id, double amount);
	
	List<AccountDto> getAllAccounts();
	
	void deleteAccount(long id);
	
	
	
	AccountDto transfer(AdminTransferRequestDto request);
	
	AccountDto getAccountByUsername(String username);
	
	AccountDto depositByUsername(String username, double amount);

	AccountDto withdrawByUsername(String username, double amount);
	
	AccountDto transferByUsername(String username, TransferRequestDto request);
	
	List<TransactionDto> getTransactionsByUsername(String username);
}
