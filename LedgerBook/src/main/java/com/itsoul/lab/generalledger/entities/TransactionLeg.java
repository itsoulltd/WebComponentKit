package com.itsoul.lab.generalledger.entities;

import com.it.soul.lab.sql.entity.Ignore;
import com.itsoul.lab.generalledger.services.Cryptor;
import com.itsoul.lab.generalledger.validation.TransferValidationException;

import java.util.Base64;
import java.util.Date;
import java.util.Objects;

/**
 * Value object representing a single monetary transaction towards an account.
 *
 * @author towhid
 * @see Transaction
 * @since 19-Aug-19
 */
public final class TransactionLeg extends LedgerEntity {

    @Ignore
    private static final long serialVersionUID = 1L;

    private final String accountRef;

    private final Money amount;

    public TransactionLeg(String accountRef, Money amount) {
        if (accountRef == null) {
            throw new TransferValidationException("accountRef is null");
        }
        if (amount == null) {
            throw new TransferValidationException("amount is null");
        }
        this.accountRef = accountRef;
        this.amount = amount;
    }

    public Money getAmount() {
        return amount;
    }

    public String getAccountRef() {
        return accountRef;
    }

    @Override
    public int hashCode() {
        int result = getAmount().hashCode();
        result = 31 * result + Objects.hashCode(getTimeStamp());
        return result;
    }

    private String signature;
    public String getSignature() {
        if (signature == null){
            //Capture timestamp if 0l:
            if(getTimeStamp() == 0l) setTimeStamp(new Date().getTime());
            int result = hashCode();
            //Generate-Digital Signature:
            signature = new String(Base64.getEncoder().encode(String.valueOf(result).getBytes()));
        }
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Ignore
    private long timeStamp;
    private long getTimeStamp() {
        return timeStamp;
    }
    private void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    private String eventTimestamp;
    public String getEventTimestamp() {
        return eventTimestamp;
    }
    public void setEventTimestamp(String eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String encryptedTimestamp(String secret, Cryptor cryptor) {
        //Capture timestamp if 0l:
        if(getTimeStamp() == 0l) setTimeStamp(new Date().getTime());
        //Do-Process:
        if (secret != null
                && !secret.isEmpty()
                && cryptor != null){
            //AES encrypted timestamp:
            String source = String.valueOf(getTimeStamp());
            String result = cryptor.encrypt(secret, source);
            setEventTimestamp(result);
            return getEventTimestamp();
        }else{
            //Base64 encoded timestamp:
            String source = String.valueOf(getTimeStamp());
            byte[] encoded = Base64.getEncoder().encode(source.getBytes());
            String result = new String(encoded);
            setEventTimestamp(result);
            return getEventTimestamp();
        }
    }

    public boolean isSignatureValid(String secret, Cryptor cryptor){
        String eventTimestamp = getEventTimestamp();
        if (eventTimestamp == null || eventTimestamp.isEmpty())
            return false;
        //Do-Process:
        String val;
        if (secret != null
                && !secret.isEmpty()
                && cryptor != null){
            //encrypted signature:
            val = cryptor.decrypt(secret, eventTimestamp);
        }else {
            //base64 encoded signature:
            byte[] decoded = Base64.getDecoder().decode(eventTimestamp.getBytes());
            val = new String(decoded);
        }
        //Check for integrity:
        try {
            long timestamp = Long.valueOf(val);
            setTimeStamp(timestamp);
            int hashCode = hashCode();
            setTimeStamp(0l);
            String signature = new String(Base64.getEncoder().encode(String.valueOf(hashCode).getBytes()));
            return Objects.equals(getSignature(), signature);
        }catch (Exception e){}
        return false;
    }

}

