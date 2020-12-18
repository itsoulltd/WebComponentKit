package com.infoworks.lab.jjwt;

import com.it.soul.lab.sql.entity.Entity;

import java.util.Map;

public class JWTPayload extends Entity {
    private long iat;
    private long nbf;
    private long exp;
    private String iss;
    private String sub;
    private Map<String, String> data;

    public JWTPayload() {}

    public long getIat() {
        return iat;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    public long getNbf() {
        return nbf;
    }

    public void setNbf(long nbf) {
        this.nbf = nbf;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }
}
