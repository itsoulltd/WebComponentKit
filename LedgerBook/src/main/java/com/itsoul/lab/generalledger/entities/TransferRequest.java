package com.itsoul.lab.generalledger.entities;

import com.infoworks.objects.Ignore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Request object describing a balanced, multi-legged monetary transaction between two
 * or more accounts. A request must have at least two legs. Different money currencies
 * are allowed as long as the total balance for the legs with the same currency is zero.
 * <p>
 * This class uses a nested builder state machine to provide a clear building path with
 * compile-time safety and coding guidance.
 *
 *
 */
/**
 * @author towhid
 * @since 19-Aug-19
 */
public final class TransferRequest extends LedgerEntity {

  @Ignore
  private static final long serialVersionUID = 1L;

  private String transactionRef;

  private String transactionType;

  private final List<TransactionLeg> legs = new ArrayList<>();

  private TransferRequest() {
  }

  /**
   * Returns a new builder for creating a transfer request.
   *
   * @return the first build step
   */
  public static ReferenceStep builder() {
    return new Builder();
  }

  @FunctionalInterface
  public interface ReferenceStep {

    /**
     * @param transactionRef client defined transaction reference
     * @return the next build step
     */
    TypeStep reference(String transactionRef);
  }

  @FunctionalInterface
  public interface TypeStep {

    /**
     * @param transactionType the transaction type for grouping transactions or other purposes
     * @return the next build step
     */
    default AccountStep type(String transactionType) {
      return type(transactionType, AccountingType.Liability);
    }

    /**
     * Universal Accounting Type:-
     * Asset, Income, Contingent-Asset : Debit (+)
     * Liability, Expense, Contingent-Liability : Debit (-)
     * @param transactionType
     * @param acType
     * @return
     */
    AccountStep type(String transactionType, AccountingType acType);
  }

  @FunctionalInterface
  public interface AccountStep {

    /**
     * @param accountRef the client defined account reference
     * @return the next build step
     */
    AmountStep account(String accountRef);
  }

  public interface AmountStep {

    /**
     * @param money the transfer amount for the account
     * @return the final build step
     */
    BuildStep amount(Money money);

    BuildStep debit(String amount, String currency);

    BuildStep credit(String amount, String currency);
  }

  public interface BuildStep {

    AmountStep account(String accountRef);

    TransferRequest build();
  }

  private static final class Builder implements ReferenceStep, TypeStep, AccountStep, AmountStep,
      BuildStep {

    private final TransferRequest request = new TransferRequest();

    private String accountRef;
    private AccountingType acType;

    @Override
    public TypeStep reference(String transactionRef) {
      request.transactionRef = transactionRef;
      return this;
    }

    @Override
    public AccountStep type(String transactionType) {
      return type(transactionType, AccountingType.Liability);
    }

    @Override
    public AccountStep type(String transactionType, AccountingType acType) {
      request.transactionType = transactionType;
      this.acType = acType;
      return this;
    }

    @Override
    public AmountStep account(String accountRef) {
      this.accountRef = accountRef;
      return this;
    }

    @Override
    public BuildStep amount(Money money) {
      request.legs.add(new TransactionLeg(accountRef, money, "dr"));
      accountRef = null;
      return this;
    }

    @Override
    public BuildStep debit(String amount, String currency) {
      String debitAmount = acType.sign() + amount;
      request.legs.add(new TransactionLeg(accountRef, Money.toMoney(debitAmount, currency), "dr"));
      accountRef = null;
      return this;
    }

    @Override
    public BuildStep credit(String amount, String currency) {
      String creditAmount = acType.reverseSign() + amount;
      request.legs.add(new TransactionLeg(accountRef, Money.toMoney(creditAmount, currency), "cr"));
      accountRef = null;
      return this;
    }

    @Override
    public TransferRequest build() {
      return request;
    }
  }

  public List<TransactionLeg> getLegs() {
    return Collections.unmodifiableList(legs);
  }

  public String getTransactionRef() {
    return transactionRef;
  }

  public String getTransactionType() {
    return transactionType;
  }
}
