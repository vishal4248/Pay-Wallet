package com.application.pay.PayWallet.transaction;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransactionUpdateRequest {

    private String transactionId;
    private String transactionStatus;

}
