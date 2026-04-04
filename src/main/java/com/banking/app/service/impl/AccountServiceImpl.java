package com.banking.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.banking.app.dto.AccountDto;
import com.banking.app.dto.AdminTransferRequestDto;
import com.banking.app.dto.TransactionDto;
import com.banking.app.dto.TransferRequestDto;
import com.banking.app.entity.Account;
import com.banking.app.entity.Transaction;
import com.banking.app.entity.User;
import com.banking.app.exception.InsufficientBalanceException;
import com.banking.app.exception.ResourceNotFoundException;
import com.banking.app.mapper.AccountMapper;
import com.banking.app.mapper.TransactionMapper;
import com.banking.app.repository.AccountRepository;
import com.banking.app.repository.TransactionRepository;
import com.banking.app.repository.UserRepository;
import com.banking.app.service.AccountService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {
	
	private AccountRepository accountRepository = null;
    private final TransactionRepository transactionRepository;
	private String username;
	private final UserRepository userRepository ;

    public AccountServiceImpl(AccountRepository accountRepository,
                              TransactionRepository transactionRepository,UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
		this.userRepository = userRepository;
    }
	
	
    @Override
    public AccountDto createAccount(AccountDto accountDto, Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = new Account();
        account.setAccountHolderName(accountDto.getAccountHolderName());
        account.setBalance(0.0);

        // 🔥 REAL FIX
        account.setUser(user);

        accountRepository.save(account);

        return AccountMapper.mapToAccountDto(account);
    }

	@Transactional(readOnly = true)
	@Override
	public AccountDto getAccountById(Long id) {

		Account account = accountRepository.findById(id)
		        .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));		return AccountMapper.mapToAccountDto(account);
	}

	@Transactional
	@Override
	public AccountDto deposit(Long id, double amount) {
		Account account = accountRepository.findById(id)
		        .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
		
		double totalBalance=account.getBalance()+amount;
		account.setBalance(totalBalance);
		Account savedAccount=accountRepository.save(account);
		transactionRepository.save(
			    new Transaction(
			        account.getId(),
			        "DEPOSIT",
			        amount,
			        LocalDateTime.now()
			    )
			    );
		return AccountMapper.mapToAccountDto(savedAccount);
	}

	@Transactional
	@Override
	public AccountDto withdraw(Long id, double amount) {
        
		Account account = accountRepository.findById(id)
		        .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id)); 
		if (amount <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }
		if(account.getBalance()<amount) {
        	 
        	 throw new InsufficientBalanceException(" insuffiencent balance");
         }
		        
         double totalBalance=account.getBalance()-amount;
		 account.setBalance(totalBalance);
		 Account savedAccount=accountRepository.save(account);
		 transactionRepository.save(
				    new Transaction(
				        account.getId(),
				        "WITHDRAW",
				        amount,
				        LocalDateTime.now()
				    )
				);
		return AccountMapper.mapToAccountDto(savedAccount);
	}


	@Override
	public List<AccountDto> getAllAccounts() {
		return accountRepository.findAll().stream().map(account->AccountMapper.mapToAccountDto(account)).
		collect(Collectors.toList());
		
	}


	@Override
	public void deleteAccount(long id) {
		Account account = accountRepository.findById(id)
		        .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));		
		accountRepository.delete(account);
	}
	
	@Transactional
	@Override
	public AccountDto transfer(AdminTransferRequestDto request) {

	    Account fromAccount = accountRepository.findById(request.getFromAccountId())
	            .orElseThrow(() -> new ResourceNotFoundException("Sender account not found"));

	    Account toAccount = accountRepository.findById(request.getToAccountId())
	            .orElseThrow(() -> new ResourceNotFoundException("Receiver account not found"));

	    if (request.getAmount() <= 0) {
	        throw new IllegalArgumentException("Transfer amount must be positive");
	    }

	    if (fromAccount.getBalance() < request.getAmount()) {
	        throw new InsufficientBalanceException("Insufficient balance");
	    }

	    // 💸 Transfer logic
	    fromAccount.setBalance(fromAccount.getBalance() - request.getAmount());
	    toAccount.setBalance(toAccount.getBalance() + request.getAmount());

	    accountRepository.save(fromAccount);
	    accountRepository.save(toAccount);
	    transactionRepository.save(
	    	    new Transaction(
	    	        fromAccount.getId(),
	    	        "TRANSFER_OUT",
	    	        request.getAmount(),
	    	        LocalDateTime.now()
	    	    )
	    	);

	    	// 🔥 receiver transaction
	    	transactionRepository.save(
	    	    new Transaction(
	    	        toAccount.getId(),
	    	        "TRANSFER_IN",
	    	        request.getAmount(),
	    	        LocalDateTime.now()
	    	    )
	    	);

	    return AccountMapper.mapToAccountDto(fromAccount);
	}
	
	@Override
	public AccountDto getAccountByUsername(String username) {

	    Account account = accountRepository.findByUserUsername(username)
	            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

	    return AccountMapper.mapToAccountDto(account);
	}
	
	@Override
	public AccountDto depositByUsername(String username, double amount) {
		if (amount <= 0) {
	        throw new IllegalArgumentException("Deposit amount must be positive");
	    }

	    Account account = accountRepository.findByUserUsername(username)
	            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

	    account.setBalance(account.getBalance() + amount);

	    accountRepository.save(account);
	    transactionRepository.save(
	            new Transaction(
	                account.getId(),
	                "DEPOSIT",
	                amount,
	                LocalDateTime.now()
	            )
	        );


	    return AccountMapper.mapToAccountDto(account);
	}
	
	@Override
	public AccountDto withdrawByUsername(String username, double amount) {

		if (amount <= 0) {
	        throw new IllegalArgumentException("Withdraw amount must be positive");
	    }
		
	    Account account = accountRepository.findByUserUsername(username)
	            .orElseThrow(() -> new RuntimeException("Account not found"));

	    if (account.getBalance() < amount) {
	        throw new InsufficientBalanceException("Insufficient balance");
	    }
	    
	    

	    account.setBalance(account.getBalance() - amount);

	    accountRepository.save(account);
	    transactionRepository.save(
	            new Transaction(
	                account.getId(),
	                "WTIHDRAW",
	                amount,
	                LocalDateTime.now()
	            )
	        );


	    return AccountMapper.mapToAccountDto(account);
	}
	
	@Override
	public AccountDto transferByUsername(String username, TransferRequestDto request) {
		
		if (request.getAmount()<= 0) {
	        throw new IllegalArgumentException(" amount must be positive");
	    }

	    Account fromAccount = accountRepository.findByUserUsername(username)
	            .orElseThrow(() -> new RuntimeException("Sender account not found"));

	    Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
	            .orElseThrow(() -> new RuntimeException("Receiver not found"));

	   

	    if (fromAccount.getBalance() < request.getAmount()) {
	        throw new InsufficientBalanceException("Insufficient balance");
	    }

	    fromAccount.setBalance(fromAccount.getBalance() - request.getAmount());
	    toAccount.setBalance(toAccount.getBalance() + request.getAmount());

	    accountRepository.save(fromAccount);
	    accountRepository.save(toAccount);

	    transactionRepository.save(
	            new Transaction(
	                fromAccount.getId(),
	                "TRANSFER_OUT",
	                request.getAmount(),
	                LocalDateTime.now()
	            )
	        );

	        transactionRepository.save(
	            new Transaction(
	                toAccount.getId(),
	                "TRANSFER_IN",
	                request.getAmount(),
	                LocalDateTime.now()
	            )
	        );
	        System.out.println("INPUT: " + request.getToAccountNumber());

	    return AccountMapper.mapToAccountDto(fromAccount);
	}
	@Override
	public List<TransactionDto> getTransactionsByUsername(String username) {

	    Account account = accountRepository.findByUserUsername(username)
	            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

	    return transactionRepository.findByAccountId(account.getId())
	            .stream()
	            .map(TransactionMapper::mapToDto)
	            .toList();
	}


}
	


	
	

