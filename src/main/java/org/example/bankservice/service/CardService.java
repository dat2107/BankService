package org.example.bankservice.service;

import org.example.bankservice.dto.CardDTO;
import org.example.bankservice.model.Account;
import org.example.bankservice.model.Balance;
import org.example.bankservice.model.Card;
import org.example.bankservice.repository.AccountRepository;
import org.example.bankservice.repository.BalanceRepository;
import org.example.bankservice.repository.CardRepository;
import org.example.bankservice.security.JwtUtil;
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
    @Autowired
    private JwtUtil jwtUtil;

    public Card create(CardDTO cardDTO, String token) {
        Long accountId = jwtUtil.extractAccountId(token);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        Card card = new Card();
        card.setAccount(account);
        card.setCardType(cardDTO.getCardType());
        card.setExpiryDate(cardDTO.getExpiryDate());
        card.setStatus(cardDTO.getStatus());
        String cardNumber = generateCardNumber();
        card.setCardNumber(cardNumber);

        return cardRepository.save(card);
    }

    private String generateCardNumber() {
        String bin = "411111"; // 6 số đầu: BIN giả định của ngân hàng
        String accountPart = String.format("%09d", new java.util.Random().nextInt(1_000_000_000));
        String partial = bin + accountPart; // 15 số
        return partial + calculateLuhnCheckDigit(partial); // thêm số kiểm tra cuối
    }

    private int calculateLuhnCheckDigit(String number) {
        int sum = 0;
        boolean alternate = true; // bắt đầu từ số cuối
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(number.charAt(i));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1; // cộng lại hai chữ số
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (10 - (sum % 10)) % 10;
    }

    public List<Card> getByAccountId(Long accountId){
        return cardRepository.findByAccount_AccountId(accountId);
    }

    public List<Card> getAllCard(){
        return cardRepository.findAll();
    }

    public Card getById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thẻ"));
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
