package org.example.bankservice.controller;

import org.example.bankservice.dto.CardDTO;
import org.example.bankservice.dto.CardResponseDTO;
import org.example.bankservice.service.CardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/card")
public class CardController {
    @Autowired
    private CardServiceImpl cardService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CardDTO cardDTO,
                                    @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Bỏ "Bearer "
        CardResponseDTO card = cardService.create(cardDTO, token);
        return ResponseEntity.ok(card);
    }

    @GetMapping
    public ResponseEntity<List<CardResponseDTO>> getAllCard(){
        List<CardResponseDTO> cards = cardService.getAllCard();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponseDTO> getCardById(@PathVariable Long cardId) {
        CardResponseDTO card = cardService.getById(cardId);
        return ResponseEntity.ok(card);
    }


    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getCardsByAccount(@PathVariable Long accountId) {
        List<CardResponseDTO> cards = cardService.getByAccountId(accountId);
        if (cards.isEmpty()) {
            return ResponseEntity.ok("Không có thẻ nào cho accountId = " + accountId);
        }
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/number/{cardNumber}")
    public ResponseEntity<CardResponseDTO> getByCardNumber(@PathVariable String cardNumber) {
        return ResponseEntity.ok(cardService.getByCardNumber(cardNumber));
    }

    @PutMapping("/{cardId}/status")
    public ResponseEntity<CardResponseDTO> updateStatus(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.updateStatus(cardId));
    }


    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.ok("Xóa thẻ thành công");
    }

}
