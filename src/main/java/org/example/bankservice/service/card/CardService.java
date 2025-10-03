package org.example.bankservice.service.card;

import org.example.bankservice.dto.CardDTO;
import org.example.bankservice.dto.CardResponseDTO;

public interface CardService {
    CardResponseDTO create(CardDTO cardDTO, String token);

}
