package com.itsoul.lab.ledgerbook.validation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.itsoul.lab.generalledger.entities.Account;
import com.itsoul.lab.generalledger.entities.TransactionLeg;
import com.itsoul.lab.generalledger.entities.TransferRequest;
import com.itsoul.lab.generalledger.exception.*;
import com.itsoul.lab.generalledger.repositories.AccountRepository;
import com.itsoul.lab.generalledger.repositories.TransactionRepository;
import com.itsoul.lab.generalledger.validation.TransferValidationException;
import com.itsoul.lab.generalledger.validation.TransferValidator;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

/**
 * Provides methods for validation of the business rules.
 */
@Service
public class TransferValidatorImpl implements TransferValidator {

  @Inject
  private AccountRepository accountRepository;
  @Inject
  private TransactionRepository transactionRepository;

  @Override
  public void validateTransferRequest(TransferRequest transferRequest) {
    if (transferRequest.getTransactionRef() == null
        ||
        transferRequest.getTransactionType() == null
        ||
        transferRequest.getLegs().size() < 2) {
      throw new IllegalArgumentException("Incomplete transaction request");
    }
  }

  @Override
  public void transferRequestExists(String transactionRef) {
    if (transactionRepository.getTransactionByRef(transactionRef) != null) {
      throw new TransferRequestExistsException(transactionRef);
    }
  }

  @Override
  public void isTransactionBalanced(Iterable<TransactionLeg> legs) throws UnbalancedLegsException {
    Multimap<Currency, TransactionLeg> legsByCurrency = sortByCurrency(legs);
    for (Currency currency : legsByCurrency.keySet()) {
      checkTransactionLegsBalanced(legsByCurrency.get(currency));
    }
  }

  @Override
  public void currenciesMatch(Iterable<TransactionLeg> legs)
      throws TransferValidationException, AccountNotFoundException {
    for (TransactionLeg leg : legs) {
      Account account = accountRepository.getAccount(leg.getAccountRef());
      if (account.isNullAccount()) {
        throw new AccountNotFoundException(leg.getAccountRef());
      }
      if (!account.getCurrency().equals(leg.getAmount().getCurrency())) {
        throw new TransferValidationException(
            "Currency from transaction leg does not match account's currency for one or more legs");
      }
    }
  }

  @Override
  public void validBalance(Iterable<TransactionLeg> legs) throws InsufficientFundsException {
    for (String accountRef : getAccountRefs(legs)) {
      Account account = accountRepository.getAccount(accountRef);
      if (account.isOverdrawn()) {
        throw new InsufficientFundsException(accountRef);
      }
    }
  }

  private void checkTransactionLegsBalanced(Iterable<TransactionLeg> legs)
      throws UnbalancedLegsException {
    BigDecimal sum = BigDecimal.ZERO;
    for (TransactionLeg leg : legs) {
      sum = sum.add(leg.getAmount().getAmount());
    }
    if (!sum.equals(new BigDecimal("0.00"))) {
      throw new UnbalancedLegsException("Transaction legs not balanced: Value is " + sum.toPlainString());
    }
  }

  private Multimap<Currency, TransactionLeg> sortByCurrency(Iterable<TransactionLeg> legs) {
    Multimap<Currency, TransactionLeg> legsByCurrency = ArrayListMultimap.create();
    for (TransactionLeg leg : legs) {
      legsByCurrency.put(leg.getAmount().getCurrency(), leg);
    }
    return legsByCurrency;
  }

  private Set<String> getAccountRefs(Iterable<TransactionLeg> legs) {
    Set<String> accounts = Sets.newHashSet();
    for (TransactionLeg leg : legs) {
      accounts.add(leg.getAccountRef());
    }
    return accounts;
  }

  public void setAccountRepository(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public void setTransactionRepository(TransactionRepository transactionRepository) {
    this.transactionRepository = transactionRepository;
  }
}