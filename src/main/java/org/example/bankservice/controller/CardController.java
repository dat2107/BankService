package org.example.bankservice.controller;

import org.example.bankservice.dto.CardDTO;
import org.example.bankservice.model.Card;
import org.example.bankservice.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/card")
public class CardController {
    @Autowired
    private CardService cardService;

    @PostMapping
    public ResponseEntity<?> create(CardDTO cardDTO){
        Card card = cardService.create(cardDTO);
        return ResponseEntity.ok("Tạo thẻ thành công "+ card);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getCardsByAccount(@PathVariable Long accountId) {
        List<Card> cards = cardService.getByAccountId(accountId);
        if (cards.isEmpty()) {
            return ResponseEntity.ok("Không có thẻ nào cho accountId = " + accountId);
        }
        return ResponseEntity.ok(cards);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.ok("Xóa thẻ thành công");
    }
}
