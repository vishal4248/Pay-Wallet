package com.application.pay.PayWallet.user;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UserRequest {

    private String userId;
    private String email;
    private String mobile;
}
