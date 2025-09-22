package org.example.bankservice.mapper;

import org.example.bankservice.dto.BalanceDTO;
import org.example.bankservice.model.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceMapper {
    @Mapping(source = "account.accountId", target = "accountId")
    BalanceDTO toDto(Balance balance);
}
