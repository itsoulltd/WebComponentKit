package com.itsoul.lab.generalledger.entities;

public enum AccountingType {

    Asset("+", 1)
    , Income("+", 1)
    , Contingent_Asset("+", 1)
    , Liability("-", 0)
    , Expense("-", 0)
    , Contingent_Liability("-", 0);

    private String sign;
    private int order;

    AccountingType(String sign, int order) {
        this.sign = sign;
        this.order = order;
    }

    public String sign() {return sign;}

    public String reverseSign() {
        return order == 0 ? "+" : "-";
    }
}
