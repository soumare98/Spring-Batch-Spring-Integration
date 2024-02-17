package org.id.bankspringbatch;

import java.text.SimpleDateFormat;

import org.id.bankspringbatch.dao.BankTransaction;
import org.springframework.batch.item.ItemProcessor;



//@Component Parceque on va l'instancier directement dans la classe de configuration


public class BankTransactionItemProcessor implements ItemProcessor<BankTransaction,BankTransaction> {
  
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");

    @Override
    public BankTransaction process(BankTransaction bankTransaction) throws Exception {
        bankTransaction.setTransactionDate(dateFormat.parse(bankTransaction.getStrTransactionDate()));
        return bankTransaction;
        
    }
    
}

