package org.example.bankservice.service;

import org.example.bankservice.dto.PaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentClient {
    private final RestTemplate restTemplate;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;  // ví dụ: http://localhost:8081/api/payments

    public PaymentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String sendPayment(PaymentRequest request) {
        return restTemplate.postForObject(paymentServiceUrl, request, String.class);
    }
}
