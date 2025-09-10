package org.example.bankservice.service;

import org.example.bankservice.dto.PaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {
    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendPayment(PaymentRequest payment) {
        jmsTemplate.convertAndSend("payment-queue", payment);
        System.out.println("📤 Sent payment request to queue: " + payment.getPaymentId());
    }
}
