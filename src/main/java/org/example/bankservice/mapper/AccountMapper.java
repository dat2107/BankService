package org.example.bankservice.mapper;

import org.example.bankservice.dto.*;
import org.example.bankservice.model.Account;
import org.example.bankservice.model.Balance;
import org.example.bankservice.model.Card;
import org.example.bankservice.model.UserLevel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(source = "balance.availableBalance", target = "balance.availableBalance")
    @Mapping(source = "balance.holdBalance", target = "balance.holdBalance")
    @Mapping(source = "userLevel.levelName", target = "userLevel.levelName")
    @Mapping(source = "userLevel.cardLimit", target = "userLevel.cardLimit")
    @Mapping(source = "userLevel.dailyTransferLimit", target = "userLevel.dailyTransferLimit")
    AccountResponseDTO toDto(Account account);
    void updateAccountFromDto(AccountDTO dto, @MappingTarget Account account);
    List<CardDTO> toCardDtos(List<Card> cards);
    UserLevelDTO toUserLevelDto(UserLevel userLevel);
    BalanceDTO toBalanceDto(Balance balance);
}
