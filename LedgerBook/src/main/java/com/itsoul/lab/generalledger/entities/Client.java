package com.itsoul.lab.generalledger.entities;

import com.infoworks.entity.Column;
import com.infoworks.objects.Ignore;
import com.infoworks.entity.PrimaryKey;
import com.infoworks.entity.TableName;
import java.util.Date;
import java.util.Objects;

/**
 * @author towhid
 * @since 19-Aug-19
 */
@TableName(value = "client", acceptAll = false)
public class Client extends LedgerEntity{

    @PrimaryKey(name = "ref")
    private String ref;
    @Column(name = "tenant_ref")
    private String tenantRef;
    @Column(name = "creation_date")
    private Date createDate;

    private static Client NULL_CLIENT = new Client("","");

    public Client() {this("", "");}

    public Client(String ref, String tenantRef) {
        this.ref = ref;
        this.tenantRef = tenantRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(ref, client.ref) &&
                Objects.equals(tenantRef, client.tenantRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ref, tenantRef);
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getTenantRef() {
        return tenantRef;
    }

    public void setTenantRef(String tenantRef) {
        this.tenantRef = tenantRef;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isNull(){
        return this.equals(NULL_CLIENT);
    }

    @Ignore
    private String secret;
    public String getSecret() {
        return secret;
    }
    public void setSecret(String secret) {
        this.secret = secret;
    }
}
