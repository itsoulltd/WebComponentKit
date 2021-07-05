package com.itsoul.lab.ledgerbook.services;

import com.itsoul.lab.generalledger.entities.Account;
import com.itsoul.lab.generalledger.entities.Money;
import com.itsoul.lab.generalledger.exception.AccountNotFoundException;
import com.itsoul.lab.generalledger.exception.InfrastructureException;
import com.itsoul.lab.generalledger.repositories.AccountRepository;
import com.itsoul.lab.generalledger.services.AccountService;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;

/**
 * Implements the methods of the account service.
 */
@Service
public class AccountServiceImpl implements AccountService {

  @Inject
  private AccountRepository accountRepository;

  @Override
  public void createAccount(String accountRef, Money amount) throws InfrastructureException {
    if (accountRepository.accountExists(accountRef)) {
      throw new InfrastructureException("Account already exists: " + accountRef);
    }
    accountRepository.createAccount(accountRepository.getClientRef(), accountRef, amount);
  }

  @Override
  public Money getAccountBalance(String accountRef) throws AccountNotFoundException {
    Account account = accountRepository.getAccount(accountRef);
    if (account == null || account.isNullAccount()) {
      throw new AccountNotFoundException(accountRef);
    }
    return account.getBalance();
  }

  public void setAccountRepository(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

}
