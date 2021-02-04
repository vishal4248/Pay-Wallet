package com.application.pay.PayWallet.wallet;

import com.application.pay.PayWallet.transaction.TransactionStatus;
import com.application.pay.PayWallet.transaction.TransactionUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class WalletService {

    private Double ADD_BONUS = 100.0;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    public void newUser(String userId) {

        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(ADD_BONUS)
                .build();

        walletRepository.save(wallet);
    }

    public Wallet getWalletOfUserById(String userId) {

        return walletRepository.findWalletByUserId(userId)
                .orElseThrow(
                        ()->new NoSuchElementException(String.format("User %s not found ",userId))
                );
    }

    ObjectMapper objectMapper = new ObjectMapper();

    // Kafka listener

    @KafkaListener(topics = "wallet",groupId = "wallet-demo")
    public void updateWallet(String updateRequest) {

        try {
            WalletRequest walletRequest = objectMapper.readValue(updateRequest,WalletRequest.class);

            /**
             * make wallet entry so that whenever user signed up that have a wallet compulsory.
             */

            Wallet noFromWallet = new Wallet();
            noFromWallet.setBalance(0.0);
            noFromWallet.setUserId(walletRequest.getFromUser());

            Wallet noToWallet = new Wallet();
            noToWallet.setBalance(0.0);
            noToWallet.setUserId(walletRequest.getToUser());

            Wallet fromWallet = walletRepository.findWalletByUserId(walletRequest.getFromUser()).orElse(noFromWallet);
            Wallet toWallet = walletRepository.findWalletByUserId(walletRequest.getToUser()).orElse(noToWallet);
            String transactionId = walletRequest.getTransactionId();

            Double amount = walletRequest.getAmount();

            if( (fromWallet.getBalance()-amount) < 0.0) {

                TransactionUpdateRequest transactionUpdateRequest = new TransactionUpdateRequest();
                transactionUpdateRequest.setTransactionId(transactionId);
                transactionUpdateRequest.setTransactionStatus(TransactionStatus.REJECTED.toString());

                kafkaTemplate.send("transaction","transaction-demo",objectMapper.writeValueAsString(transactionUpdateRequest));

                return;
            }

//            fromWallet.setBalance(fromWallet.getBalance()-amount);
//            toWallet.setBalance(toWallet.getBalance()+amount);

            walletRepository.updateWallet(fromWallet.getUserId(),0-amount);
            walletRepository.updateWallet(toWallet.getUserId(),amount);

            TransactionUpdateRequest transactionUpdateRequest = new TransactionUpdateRequest();
            transactionUpdateRequest.setTransactionId(transactionId);
            transactionUpdateRequest.setTransactionStatus(TransactionStatus.APPROVED.toString());

            kafkaTemplate.send("transaction","transaction-pay",objectMapper.writeValueAsString(transactionUpdateRequest));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
}
