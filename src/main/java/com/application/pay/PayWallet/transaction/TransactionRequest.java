package com.application.pay.PayWallet.transaction;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TransactionRequest {

    private String fromUser;
    private Double amount;
    private String toUser;
    private String purpose;

}
