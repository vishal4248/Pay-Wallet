package com.application.pay.PayWallet.notify;

import com.application.pay.PayWallet.statements.StatementService;
import com.application.pay.PayWallet.user.User;
import com.application.pay.PayWallet.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
public class EmailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    SimpleMailMessage simpleMailMessage;

    @Autowired
    MimeMessageHelper mimeMessageHelper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StatementService statementService;

    ObjectMapper objectMapper = new ObjectMapper();

    // Kafka listener

    @KafkaListener(topics = "email",groupId = "email-demo")
    public void sendEmail(String request) {

        try {

            System.out.println("In Email KafkaListener");

            EmailRequest emailRequest = objectMapper.readValue(request,EmailRequest.class);

            simpleMailMessage.setFrom("vp.developer4248@gmail.com");
            simpleMailMessage.setSubject("Pay-Wallet :: Transaction report");
            simpleMailMessage.setTo(emailRequest.getTo());
            simpleMailMessage.setText(emailRequest.getMessage());

            javaMailSender.send(simpleMailMessage);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "statement",groupId = "statement-demo")
    public void sendStatementOfTransaction(String uId) {

        statementService.getStatement(uId);

        try {

            mimeMessageHelper.setFrom("vp.developer4248@gmail.com");
            mimeMessageHelper.setSubject("Pay-Wallet : Statement Report");

            Optional<User> user = userRepository.findUserByUserId(uId);
            if(user.isPresent()) {
                mimeMessageHelper.setTo(user.get().getEmail());
            } else {
                return;
            }

            mimeMessageHelper.setText("Your summary of wallet is as below :-");
            mimeMessageHelper.addAttachment("summary", new File("D://Pay-Wallet//"+uId + "_statement.csv"));
            mimeMessageHelper.addAttachment("debited",new File("D://Pay-Wallet//"+uId + "_debit_statement.csv"));
            mimeMessageHelper.addAttachment("credited",new File("D://Pay-Wallet//"+uId + "_credit_statement.csv"));

            javaMailSender.send(mimeMessageHelper.getMimeMessage());

            System.out.println("Summary sent successfully");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }








    }

}
