package com.application.pay.PayWallet.notify;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmailRequest {

    private String to;
    private String message;

}
