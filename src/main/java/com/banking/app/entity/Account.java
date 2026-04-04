package com.banking.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;



@Entity
@Table(name="accounts")
public class Account {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;
	
	@Column(name = "balance", nullable = false)
    private Double balance = 0.0;
	
	@Column(name="account_holder_name", nullable=false)
    private String accountHolderName;
	
	@ManyToOne
	@JoinColumn(name = "user_id",nullable=false)
	private User user;
	
	@Column(name = "account_number", unique = true, nullable = false)
	private String accountNumber;
	
	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Account() {
	}
  
  public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public String getAccountHolderName() {
	return accountHolderName;
}

public void setAccountHolderName(String accountHolderName) {
	this.accountHolderName = accountHolderName;
}

public Double getBalance() {
	return balance;
}

public void setBalance(Double balance) {
	this.balance = balance;
}



public Account(Long id, String accountHolderName, Double balance) {
	super();
	this.id = id;
	this.accountHolderName = accountHolderName;
	this.balance = balance;
}


}
