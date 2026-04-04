package com.banking.app.dto;

import jakarta.validation.constraints.*;

public class TransferRequestDto {

    @NotNull
    private String toAccountNumber;

    
    @NotNull
    @Positive
    private Double amount;

	

	public String getToAccountNumber() {
		return toAccountNumber;
	}

	public void setToAccountNumber(String toAccountNumber) {
		this.toAccountNumber = toAccountNumber;
	}

	/*public Long getToAccountId() {
		return toAccountId;
	}

	public void setToAccountId(Long toAccountId) {
		this.toAccountId = toAccountId;
	}*/

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	

    // getters & setters
}