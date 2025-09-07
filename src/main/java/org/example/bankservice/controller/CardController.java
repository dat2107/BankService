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
    public ResponseEntity<?> create(@RequestBody CardDTO cardDTO,
                                    @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Bỏ "Bearer "
        Card card = cardService.create(cardDTO, token);
        return ResponseEntity.ok(card);
    }

    @GetMapping
    public ResponseEntity<List<Card>> getAllCard(){
        List<Card> cards = cardService.getAllCard();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<Card> getCardById(@PathVariable Long cardId) {
        Card card = cardService.getById(cardId);
        return ResponseEntity.ok(card);
    }


    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getCardsByAccount(@PathVariable Long accountId) {
        List<Card> cards = cardService.getByAccountId(accountId);
        if (cards.isEmpty()) {
            return ResponseEntity.ok("Không có thẻ nào cho accountId = " + accountId);
        }
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/number/{cardNumber}")
    public ResponseEntity<Card> getByCardNumber(@PathVariable String cardNumber) {
        return ResponseEntity.ok(cardService.getByCardNumber(cardNumber));
    }

    @PutMapping("/{cardId}/status")
    public ResponseEntity<Card> updateStatus(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.updateStatus(cardId));
    }


    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.ok("Xóa thẻ thành công");
    }

}
