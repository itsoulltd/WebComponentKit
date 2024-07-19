package com.infoworks.lab.rest.models.validation;

import com.infoworks.lab.client.data.rest.Any;
import com.infoworks.lab.rest.validation.CurrencyCode.IsValidCurrencyCode;
import com.infoworks.lab.rest.validation.Email.EmailPattern;
import com.infoworks.lab.rest.validation.MoneyFormat.Money;
import com.infoworks.lab.rest.validation.Password.PasswordRule;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

public class User extends Any<Long> {

    @NotEmpty(message = "TenantID must not be null or empty!")
    private String tenantID;

    @EmailPattern(message = "Invalid email-address!")
    @NotEmpty(message = "Email must not be null or empty!")
    private String email;

    @PasswordRule(message = "PasswordRule Violation."
            , minLengthRule = 4, maxLengthRule = 12, maxUpperCaseCharRule = 1, maxAlphaSequenceRule = 5)
    private String password;

    //@Size() or @Length()
    @Length(max = 6, min = 1, message = "accountPrefix has to be 1<=length<=6")
    private String accountPrefix;

    @Money(message = "amount has to be 0.00 or any combination with at least 2 digit after precision. e.g. 1002001.00 or 1200933.97 etc")
    private String amount = "0.00";

    @IsValidCurrencyCode(message = "currency is invalid. e.g. BDT, USD, EUR etc")
    private String currency;

    @Size(min = 1, max = 20, message = "fromAccount has to be 1<=length<=20. e.g. CASH@<Account-Name>")
    @Pattern(regexp = ".*@.*", message = "pattern of 'fromAccount' must be as follow: prefix@<username/account> e.g CASH@Master, REVENUE@Master, CASH@user-name, bKash@user-name, NAGAD@user-name")
    private String account;

    @Min(value = 18, message = "Age min value is 18.")
    private int age;

    @Max(value = 10, message = "Height can't be >10 feet.")
    private float height;

    public User() {}

    public String getTenantID() {
        return tenantID;
    }

    public void setTenantID(String tenantID) {
        this.tenantID = tenantID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountPrefix() {
        return accountPrefix;
    }

    public void setAccountPrefix(String accountPrefix) {
        this.accountPrefix = accountPrefix;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
