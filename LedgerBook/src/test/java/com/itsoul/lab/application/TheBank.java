package com.itsoul.lab.application;

public interface TheBank extends AutoCloseable{
    long getBalance(String iban);
    void addBalance(String iban, long balance);
    void newAccount(String iban, long balance);
    void transfer(String fromIban, String toIban, long transferAmount);

    @Override
    default void close() throws Exception {}
}
