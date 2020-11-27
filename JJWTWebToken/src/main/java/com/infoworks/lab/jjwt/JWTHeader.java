package com.infoworks.lab.jjwt;

import com.it.soul.lab.sql.entity.Entity;

public class JWTHeader extends Entity {

    private String typ;
    private String alg;
    private String kid;

    public JWTHeader() {}

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }
}
