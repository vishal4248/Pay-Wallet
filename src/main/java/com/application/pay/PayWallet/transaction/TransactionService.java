package com.application.pay.PayWallet.transaction;


import com.application.pay.PayWallet.notify.EmailRequest;
import com.application.pay.PayWallet.user.User;
import com.application.pay.PayWallet.wallet.WalletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    TransactionRepository transactionRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    RestTemplate restTemplate = new RestTemplate();




    public void createTransaction(TransactionRequest transactionRequest) {

        Transaction transaction = new Transaction();
        transaction.setFromUser(transactionRequest.getFromUser());
        transaction.setAmount(transactionRequest.getAmount().toString());
        transaction.setToUser(transactionRequest.getToUser());
        transaction.setPurpose(transactionRequest.getPurpose());
        transaction.setStatus(TransactionStatus.PENDING.toString());
        transaction.setExternalId(UUID.randomUUID().toString());
        transaction.setTransactionDateTime(new Date().toString());

        transactionRepository.save(transaction);

        /**
         * Now, there is two ways to doing wallet updating
         * 1.> make a RestTemplate & synchronously api call
         *      there is something issues comes that
         *      when it goes to transaction-> wallet,
         *      then respond back to wallet->transaction
         *      after that calling it will be very tedious work
         *
         * 2.> using kafka service (kafka sender & kafka listener)
         */

        // RestTemplate
        /*
        try {
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI("https://localhost:8080/walletService");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        */

        // Kafka

        // Publish event to wallet
        WalletRequest walletRequest = new WalletRequest();
        walletRequest.setFromUser(transactionRequest.getFromUser());
        walletRequest.setAmount(transactionRequest.getAmount());
        walletRequest.setToUser(transactionRequest.getToUser());
        walletRequest.setTransactionId(transaction.getExternalId());

        try {
            kafkaTemplate.send("wallet", "wallet-pay", objectMapper.writeValueAsString(walletRequest));
            // log.info("sent a message to topic := ","wallet");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "transaction",groupId = "transaction-pay")
    public void updateTransaction(String updateRequest) {

        try {

            TransactionUpdateRequest transactionUpdateRequest = objectMapper.readValue(updateRequest,TransactionUpdateRequest.class);
            Transaction transaction = transactionRepository.findByExternalId(transactionUpdateRequest.getTransactionId()).get();
            transaction.setStatus(transactionUpdateRequest.getTransactionStatus());

            transactionRepository.save(transaction);

            System.out.println("In Email section");

            //notify from user
            URI uriFrom = URI.create("http://localhost:8080/user/"+transaction.getFromUser());
            HttpHeaders httpHeadersFrom = new HttpHeaders();
            HttpEntity httpEntityFrom = new HttpEntity(httpHeadersFrom);
            User userFrom = restTemplate.exchange(uriFrom, HttpMethod.GET,httpEntityFrom,User.class).getBody();

            EmailRequest fromEmailRequest = new EmailRequest();
            fromEmailRequest.setTo(userFrom.getEmail());
            fromEmailRequest.setMessage(
                    String.format("# Hii %s,  # TransactionId : %s, # Transaction status : %s, # amount deducted : %s, # To : %s" ,
                            userFrom.getUserId(),
                            transaction.getExternalId(),
                            transaction.getStatus(),
                            transaction.getAmount(),
                            transaction.getToUser()
                    )
            );
            kafkaTemplate.send("email",objectMapper.writeValueAsString(fromEmailRequest));

            // notify to user
            URI uriTo = URI.create("http://localhost:8080/user/"+transaction.getToUser());
            HttpHeaders httpHeadersTo = new HttpHeaders();
            HttpEntity httpEntityTo = new HttpEntity(httpHeadersTo);
            User toUser = restTemplate.exchange(uriTo, HttpMethod.GET,httpEntityTo,User.class).getBody();

            EmailRequest toEmailRequest = new EmailRequest();
            toEmailRequest.setTo(toUser.getEmail());
            toEmailRequest.setMessage(
                    String.format("Hii %s, \n TransactionId : %s, \n Transaction status : %s. \n amount received : %s ,\n From : %s",
                            toUser.getUserId(),
                            transaction.getExternalId(),
                            transaction.getStatus(),
                            transaction.getAmount(),
                            transaction.getFromUser()
                    )
            );

            kafkaTemplate.send("email",objectMapper.writeValueAsString(toEmailRequest));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


}
