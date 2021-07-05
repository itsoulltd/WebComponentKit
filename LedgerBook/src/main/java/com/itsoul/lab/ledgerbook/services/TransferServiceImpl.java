package com.itsoul.lab.ledgerbook.services;

import com.google.common.collect.Lists;
import com.itsoul.lab.generalledger.entities.Transaction;
import com.itsoul.lab.generalledger.entities.TransactionLeg;
import com.itsoul.lab.generalledger.entities.TransferRequest;
import com.itsoul.lab.generalledger.exception.AccountNotFoundException;
import com.itsoul.lab.generalledger.exception.InsufficientFundsException;
import com.itsoul.lab.generalledger.repositories.AccountRepository;
import com.itsoul.lab.generalledger.repositories.TransactionRepository;
import com.itsoul.lab.generalledger.services.TransferService;
import com.itsoul.lab.generalledger.validation.TransferValidator;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Implements the methods of the transfer service.
 */
@Service
public class TransferServiceImpl implements TransferService {

    private Logger log = Logger.getLogger(this.getClass().getSimpleName());

    @Inject
    private TransferValidator validator;
    @Inject
    private AccountRepository accountRepository;
    @Inject
    private TransactionRepository transactionRepository;

    @Override
    public void transferFunds(TransferRequest transferRequest)
            throws InsufficientFundsException, AccountNotFoundException {
        //
        try {
            accountRepository.begin();
            validateRequest(transferRequest);
            for (TransactionLeg leg : transferRequest.getLegs()) {
                accountRepository.updateBalance(leg);
            }
            validator.validBalance(transferRequest.getLegs());
            storeTransaction(transferRequest
                            , accountRepository.getQueryExecutor() != transactionRepository.getQueryExecutor());
            accountRepository.end();
        } catch (SQLException e) {
            try {
                accountRepository.abort();
            } catch (SQLException e1) {log.warning(e1.getMessage());}
            log.warning(e.getMessage());
        }
    }

    private void validateRequest(TransferRequest request) {
        validator.validateTransferRequest(request);
        validator.isTransactionBalanced(request.getLegs());
        validator.transferRequestExists(request.getTransactionRef());
        validator.currenciesMatch(request.getLegs());
    }

    private void storeTransaction(TransferRequest request, boolean enableTransaction) throws SQLException {
        Transaction transaction = new Transaction(
                request.getTransactionRef(),
                request.getTransactionType(),
                new Date(),
                request.getLegs()
        );
        try {
            if(enableTransaction) transactionRepository.begin();
            transactionRepository.storeTransaction(transaction);
            if(enableTransaction) transactionRepository.end();
        } catch (SQLException e) {
            if(enableTransaction) transactionRepository.abort();
            throw new SQLException(e.getMessage());
        }
    }

    @Override
    public List<Transaction> findTransactionsByAccountRef(String accountRef)
            throws AccountNotFoundException {
        if (!accountRepository.accountExists(accountRef)) {
            throw new AccountNotFoundException(accountRef);
        }
        Set<String> transactionRefs = transactionRepository.getTransactionRefsForAccount(accountRef);
        if (transactionRefs.isEmpty()) {
            return Lists.newArrayList();
        }
        return transactionRepository.getTransactions(Lists.newArrayList(transactionRefs));
    }

    @Override
    public Transaction getTransactionByRef(String transactionRef) {
        return transactionRepository.getTransactionByRef(transactionRef);
    }

    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void setValidator(TransferValidator validator) {
        this.validator = validator;
    }

}


