package com.application.pay.PayWallet.statements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatementController {

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @GetMapping("/getStatement/{id}")
    public void getStatement(@PathVariable("id") String uId) {

        kafkaTemplate.send("statement",uId);
    }
}
