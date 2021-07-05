package com.itsoul.lab.generalledger.entities;

import com.it.soul.lab.sql.entity.Ignore;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Value object representing a monetary transaction between two or more accounts. Each
 * account transaction is represented by a transaction leg.
 *
 * @author towhid
 * @see TransactionLeg
 * @since 19-Aug-19
 */
public final class Transaction extends LedgerEntity {

  @Ignore
  private static final long serialVersionUID = 1L;

  private final String transactionRef;

  private final String transactionType;

  private final Date transactionDate;

  private final List<TransactionLeg> legs;

  public Transaction(String transactionRef, String transactionType, Date transactionDate,
      List<TransactionLeg> legs) {
    this.transactionRef = transactionRef;
    this.transactionType = transactionType;
    this.transactionDate = transactionDate;
    this.legs = legs;
  }

  public String getTransactionRef() {
    return transactionRef;
  }

  public String getTransactionType() {
    return transactionType;
  }

  public List<TransactionLeg> getLegs() {
    return Collections.unmodifiableList(legs);
  }

  public Date getTransactionDate() {
    return new Date(transactionDate.getTime());
  }
}
