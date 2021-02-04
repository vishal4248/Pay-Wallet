package com.application.pay.PayWallet.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/transaction")
    public void makeTransaction(@RequestBody TransactionRequest transactionRequest) {

        try {
            transactionService.createTransaction(transactionRequest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
