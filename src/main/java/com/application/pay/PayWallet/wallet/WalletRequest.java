package com.application.pay.PayWallet.wallet;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class WalletRequest {

    private String transactionId;
    private String fromUser;
    private Double amount;
    private String toUser;

}
