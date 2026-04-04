package com.banking.app.mapper;

import com.banking.app.dto.TransactionDto;
import com.banking.app.entity.Transaction;

public class TransactionMapper {

    public static TransactionDto mapToDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setType(transaction.getType());
        dto.setAmount(transaction.getAmount());
        dto.setTimestamp(transaction.getTimestamp());
        return dto;
    }
}