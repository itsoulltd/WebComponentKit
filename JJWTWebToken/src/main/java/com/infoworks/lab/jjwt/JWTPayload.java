package com.infoworks.lab.jjwt;

import com.it.soul.lab.sql.entity.Entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    public JWTPayload setIat(long iat) {
        this.iat = iat;return this;
    }

    public long getNbf() {
        return nbf;
    }

    public JWTPayload setNbf(long nbf) {
        this.nbf = nbf;
        return this;
    }

    public long getExp() {
        return exp;
    }

    public JWTPayload setExp(long exp) {
        this.exp = exp;
        return this;
    }

    public Map<String, String> getData() {
        return data;
    }

    public JWTPayload setData(Map<String, String> data) {
        this.data = data;
        return this;
    }

    public String getIss() {
        return iss;
    }

    public JWTPayload setIss(String iss) {
        this.iss = iss;
        return this;
    }

    public String getSub() {
        return sub;
    }

    public JWTPayload setSub(String sub) {
        this.sub = sub;
        return this;
    }

    public JWTPayload addData(String key, String value){
        if (data == null) data = new ConcurrentHashMap<>();
        data.put(key, value);
        return this;
    }

    public JWTPayload removeData(String key){
        if (data == null) return this;
        data.remove(key);
        return this;
    }
}
