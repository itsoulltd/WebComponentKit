package com.itsoul.lab.generalledger.entities;

import com.infoworks.objects.Ignore;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * Immutable value object representing an account
 *
 *
 */
/**
 * @author towhid
 * @since 19-Aug-19
 */
public class Account extends LedgerEntity {

  private final String accountRef;
  private final Money balance;
  @Ignore
  private static final Account NULL_ACCOUNT = new Account("",
      new Money(new BigDecimal("0.00"), Currency.getInstance("XXX")));

  public Account(String accountRef, Money balance) {
    this.accountRef = accountRef;
    this.balance = balance;
  }

  public static Account nullAccount() {
    return NULL_ACCOUNT;
  }

  public String getAccountRef() {
    return accountRef;
  }

  public Money getBalance() {
    return balance;
  }

  public Currency getCurrency() {
    return balance.getCurrency();
  }

  public boolean isOverdrawn() {
    return balance.getAmount().doubleValue() < 0.0;
  }

  public boolean isNullAccount() {
    return this.equals(NULL_ACCOUNT);
  }

}