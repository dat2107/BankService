package org.example.bankservice.service;

import org.example.bankservice.dto.CardDTO;
import org.example.bankservice.model.Account;
import org.example.bankservice.model.Balance;
import org.example.bankservice.model.Card;
import org.example.bankservice.repository.AccountRepository;
import org.example.bankservice.repository.BalanceRepository;
import org.example.bankservice.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CardService {
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BalanceRepository balanceRepository;

    public Card create(CardDTO cardDTO){
        Card card = new Card();
        Account account = accountRepository.findById(cardDTO.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        card.setAccount(account);
        card.setCardType(cardDTO.getCardtype());
        card.setExpiryDate(cardDTO.getExpiryDate());
        card.setStatus(cardDTO.getStatus());
        return cardRepository.save(card);
    }

    public List<Card> getByAccountId(Long accountId){
        return cardRepository.findByAccount_AccountId(accountId);
    }

    public void deleteCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thẻ"));

        Balance balance = balanceRepository.findByAccount_AccountId(card.getAccount().getAccountId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy số dư cho account"));

        if (balance.getHoldBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Không thể xóa thẻ vì có giao dịch đang chờ xử lý (holdBalance > 0)");
        }

        cardRepository.delete(card);
    }
}
