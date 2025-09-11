package org.example.bankservice.service;

import org.example.bankservice.dto.AccountResponseDTO;
import org.example.bankservice.dto.CardDTO;
import org.example.bankservice.dto.CardResponseDTO;
import org.example.bankservice.model.Account;
import org.example.bankservice.model.Balance;
import org.example.bankservice.model.Card;
import org.example.bankservice.repository.AccountRepository;
import org.example.bankservice.repository.BalanceRepository;
import org.example.bankservice.repository.CardRepository;
import org.example.bankservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private AccountService accountService;
    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "accounts_dto", allEntries = true),       // clear cache account cụ thể
            @CacheEvict(value = "accounts_all_dto", allEntries = true),   // clear danh sách accounts
            @CacheEvict(value = "cards_dto", allEntries = true)           // clear danh sách thẻ (nếu có cache)
    })
    public CardResponseDTO create(CardDTO cardDTO, String token) {
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

        Card saved = cardRepository.save(card);
        return mapToDTO(saved);
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

    public List<CardResponseDTO> getByAccountId(Long accountId){
        return cardRepository.findByAccount_AccountId(accountId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<CardResponseDTO> getAllCard(){
        return cardRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }


    public CardResponseDTO getById(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thẻ"));
        return mapToDTO(card); // ✅ khớp kiểu dữ liệu
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "accounts_dto", allEntries = true),       // clear cache account cụ thể
            @CacheEvict(value = "accounts_all_dto", allEntries = true),   // clear danh sách accounts
            @CacheEvict(value = "cards_dto", allEntries = true)           // clear danh sách thẻ (nếu có cache)
    })
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

    // 🔹 1. Tìm thẻ theo số thẻ
    public CardResponseDTO getByCardNumber(String cardNumber) {
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thẻ với số: " + cardNumber));
        return mapToDTO(card);
    }


//    // 🔹 2. Cập nhật trạng thái thẻ
//    public Card updateStatus(Long cardId) {
//        Card card = cardRepository.findById(cardId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy thẻ với id = " + cardId));
//
//        card.setStatus(Card.Status.INACTIVE); // giả sử field `status` trong Card là String (ACTIVE/INACTIVE)
//        return cardRepository.save(card);
//    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "accounts_dto", allEntries = true),       // clear cache account cụ thể
            @CacheEvict(value = "accounts_all_dto", allEntries = true),   // clear danh sách accounts
            @CacheEvict(value = "cards_dto", allEntries = true)           // clear danh sách thẻ (nếu có cache)
    })
    public CardResponseDTO updateStatus(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thẻ với id = " + cardId));

        card.setStatus(Card.Status.INACTIVE);
        Card updated = cardRepository.save(card);
        return mapToDTO(updated);
    }


    private CardResponseDTO mapToDTO(Card card) {
        CardResponseDTO dto = new CardResponseDTO();
        dto.setCardId(card.getCardId());
        dto.setCardNumber(card.getCardNumber());
        dto.setCardType(card.getCardType().name());
        dto.setStatus(card.getStatus().name());
        dto.setExpiryDate(card.getExpiryDate());

        AccountResponseDTO accDto = accountService.mapToDTO(card.getAccount());
        dto.setAccount(accDto);

        return dto;
    }
}
