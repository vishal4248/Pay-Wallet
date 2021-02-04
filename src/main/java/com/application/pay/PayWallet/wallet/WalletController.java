package com.application.pay.PayWallet.wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WalletController {

    @Autowired
    WalletService walletService;

    @GetMapping("/wallet/{id}")
    public Wallet getWallet(@PathVariable("id") String id) {
        return walletService.getWalletOfUserById(id);
    }
}
