package com.application.pay.PayWallet.transaction;

import lombok.*;
import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "internal_id")
    private Long id;

    private String externalId = UUID.randomUUID().toString();
    private String transactionDateTime;
    private String fromUser;
    private String amount;
    private String toUser;
    private String purpose;
    private String status;
}
