package com.application.pay.PayWallet.statements;

import com.application.pay.PayWallet.transaction.Transaction;
import com.application.pay.PayWallet.transaction.TransactionRepository;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.util.List;

@Service
public class StatementService {

    @Autowired
    TransactionRepository transactionRepository;

    public void getStatement(String uId) {
        List<Transaction> senderTransaction = transactionRepository.getStatementByUserIdAsSender(uId);
        List<Transaction> receiverTransaction = transactionRepository.getStatementByUserIdAsReceiver(uId);
        List<Transaction> statement = transactionRepository.getFullStatement(uId);


        try {

            // For debit part
            String[] senderIndex = {"s.no.","amount","to_user","status","purpose","transaction_id","transaction_date_time"};
            String senderName = uId + "_debit_statement";
            CSVWriter sendCsvWriter = new CSVWriter(new FileWriter("D://Pay-Wallet//"+senderName+".csv"));
            sendCsvWriter.writeNext(senderIndex);

            for(Transaction val : senderTransaction) {

                String[] line = new String[7];
                line[0] = val.getId().toString();
                line[1] = val.getAmount();
                line[2] = val.getToUser();
                line[3] = val.getStatus();
                line[4] = val.getPurpose();
                line[5] = val.getExternalId();
                line[6] = val.getTransactionDateTime();

                sendCsvWriter.writeNext(line);
            }
            sendCsvWriter.flush();



            // For credit part
            String[] receiverIndex = {"s.no.","amount","from_user","status","purpose","transaction_id","transaction_date_time"};
            String receiverName = uId + "_credit_statement";
            CSVWriter receiveCsvWriter = new CSVWriter(new FileWriter("D://Pay-Wallet//"+receiverName+".csv"));
            receiveCsvWriter.writeNext(receiverIndex);

            for(Transaction val : receiverTransaction) {

                String[] line = new String[7];
                line[0] = val.getId().toString();
                line[1] = val.getAmount();
                line[2] = val.getFromUser();
                line[3] = val.getStatus();
                line[4] = val.getPurpose();
                line[5] = val.getExternalId();
                line[6] = val.getTransactionDateTime();

                receiveCsvWriter.writeNext(line);
            }
            receiveCsvWriter.flush();


            // Complete Statement
            String[] index = {"s.no.","debit/credit","amount","from_user","to_user","status","purpose","transaction_id","transaction_date_time"};
            String name = uId + "_statement";
            CSVWriter csvWriter = new CSVWriter(new FileWriter("D://Pay-Wallet//"+name+".csv"));
            csvWriter.writeNext(index);

            System.out.println(statement);

            for(Transaction val : statement) {

                String[] line = new String[9];
                line[0] = val.getId().toString();

                if(val.getFromUser().equals(uId)) {
                    line[1] = "DEBIT";
                    line[2] = "-"+val.getAmount();
                    line[3] = "..........";
                    line[4] = val.getToUser();
                } else if(val.getToUser().equals(uId)) {
                    line[1] = "CREDIT";
                    line[2] = "+"+val.getAmount();
                    line[3] = val.getFromUser();
                    line[4] = "..........";
                } else {
                    System.out.println("Not Found............................");
                    System.out.println("Something went wrong in statement, Please check asap");
                }

                line[5] = val.getStatus();
                line[6] = val.getPurpose();
                line[7] = val.getExternalId();
                line[8] = val.getTransactionDateTime();

                csvWriter.writeNext(line);
            }
            csvWriter.flush();


            System.out.println("Completion the statement generation work ");
            System.out.println("*****************************");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
