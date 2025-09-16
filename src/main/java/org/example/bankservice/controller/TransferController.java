package org.example.bankservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.bankservice.dto.TransactionDTO;
import org.example.bankservice.dto.TransferDTO;
import org.example.bankservice.dto.OtpConfirmDTO;
import org.example.bankservice.model.Transaction;
import org.example.bankservice.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {
    @Autowired
    private TransferService transferService;

    // User tạo yêu cầu chuyển khoản -> sinh OTP gửi email
    @PostMapping("/request")
    public ResponseEntity<?> requestTransfer(@RequestBody TransferDTO dto) {
        Transaction tx = transferService.createTransferRequest(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("transactionId", tx.getTransactionId());
        response.put("message", "Mã OTP đã được gửi đến email của bạn.");

        return ResponseEntity.ok(response);
    }


    //User nhập OTP để xác nhận giao dịch
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmOtp(@RequestBody OtpConfirmDTO dto) {
        TransactionDTO tx = transferService.confirmOtp(dto);
        return ResponseEntity.ok(tx);
    }
}
