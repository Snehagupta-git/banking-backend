package com.banking.app.mapper;

import com.banking.app.dto.AccountDto;
import com.banking.app.entity.Account;

public class AccountMapper {
	
	public static Account mapToAccount(AccountDto accountDto) {

	    Account account = new Account();

	    account.setAccountHolderName(accountDto.getAccountHolderName());
	    account.setBalance(accountDto.getBalance());
	    account.setAccountNumber(accountDto.getAccountNumber());

	    return account;
	}
	 public static AccountDto mapToAccountDto(Account account) {
		 AccountDto accountDto=new AccountDto(
				 account.getId(),
				 account.getAccountHolderName(),
				 account.getBalance(),
				 account.getAccountNumber()

				 );   
		 //accountDto.setAccountNumber(account.getAccountNumber());

		 return accountDto;
				 
	 }
}