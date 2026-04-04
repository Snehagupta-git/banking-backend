package com.banking.app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.app.dto.AccountDto;
import com.banking.app.dto.AdminTransferRequestDto;
import com.banking.app.dto.AmountRequestDto;
import com.banking.app.dto.TransactionDto;
import com.banking.app.dto.TransferRequestDto;
import com.banking.app.entity.Account;
import com.banking.app.entity.Transaction;
import com.banking.app.mapper.AccountMapper;
import com.banking.app.mapper.TransactionMapper;
import com.banking.app.repository.TransactionRepository;
import com.banking.app.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
	
	private AccountService accountService;
	
	private final TransactionRepository transactionRepository;

	public AccountController(AccountService accountService,
	                         TransactionRepository transactionRepository) {
	    this.accountService = accountService;
	    this.transactionRepository = transactionRepository;
	}

	
	
// add account rest api
	
	
	@GetMapping("/me")
	public ResponseEntity<AccountDto> getMyAccount(Authentication authentication) {

	    String username = authentication.getName(); // 🔥 current logged user

	    AccountDto account = accountService.getAccountByUsername(username);

	    return ResponseEntity.ok(account);
	}

@GetMapping("/{id}")
public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id){
	
	AccountDto accountDto=accountService.getAccountById(id);
	
	return ResponseEntity.ok(accountDto);
	
}
@PutMapping("/{id}/deposit")
public ResponseEntity<AccountDto> deposit(@PathVariable Long id,@RequestBody Map<String,Double >request){
	Double amount=request.get("amount");
	if(amount==null) {
		throw new RuntimeException("Amount is required");
	}
	AccountDto accountDto=accountService.deposit(id, amount);
	return ResponseEntity.ok(accountDto);
}


@PutMapping("{id}/withdraw")
public ResponseEntity<AccountDto> withdraw(@PathVariable Long id,@RequestBody Map<String,Double >request){
	Double amount=request.get("amount");
		AccountDto accountDto=accountService.withdraw(id, amount);
	return ResponseEntity.ok(accountDto);

	
}
@GetMapping
public ResponseEntity<List<AccountDto>>getAllAccounts(){
	List<AccountDto> accountDto=accountService.getAllAccounts();
	return ResponseEntity.ok(accountDto);

}

@DeleteMapping("/{id}")
public ResponseEntity<String>deleteAccount(@PathVariable Long id){
accountService.deleteAccount(id);
return ResponseEntity.ok("Account Deleted Successfully");
}


@PostMapping("/transfer")
public ResponseEntity<AccountDto> transfer(@Valid @RequestBody AdminTransferRequestDto request) {
    return new ResponseEntity<>(accountService.transfer(request), HttpStatus.OK);
}


@GetMapping("/{id}/transactions")
public ResponseEntity<List<TransactionDto>> getTransactions(@PathVariable Long id) {

    List<TransactionDto> transactions = transactionRepository.findByAccountId(id)
            .stream()
            .map(TransactionMapper::mapToDto)
            .toList();

    return ResponseEntity.ok(transactions);
}


@PutMapping("/me/deposit")
public ResponseEntity<AccountDto> deposit(
        Authentication authentication,
        @Valid @RequestBody AmountRequestDto request) {


    String username = authentication.getName();
    //Double amount = request.getAmount();

    AccountDto account = accountService.depositByUsername(username, request.getAmount());

    return ResponseEntity.ok(account);
}
@PutMapping("/me/withdraw")
public ResponseEntity<AccountDto> withdraw(
        Authentication authentication,
        @Valid @RequestBody AmountRequestDto request) {

    String username = authentication.getName();
   // Double amount = request.getAmount();

    AccountDto account = accountService.withdrawByUsername(username, request.getAmount());

    return ResponseEntity.ok(account);
}
@PostMapping("/me/transfer")
public ResponseEntity<AccountDto> transfer(
        Authentication authentication,
        @RequestBody TransferRequestDto request) {

    String username = authentication.getName();

    return ResponseEntity.ok(
        accountService.transferByUsername(username, request)
    );
}

@GetMapping("/me/transactions")
public ResponseEntity<List<TransactionDto>> getMyTransactions(Authentication authentication) {

    String username = authentication.getName();

    return ResponseEntity.ok(
        accountService.getTransactionsByUsername(username)
    );
}

}