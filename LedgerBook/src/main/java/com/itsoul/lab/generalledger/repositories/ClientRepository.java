package com.itsoul.lab.generalledger.repositories;

import com.itsoul.lab.generalledger.entities.Client;
import org.jvnet.hk2.annotations.Contract;

import java.util.Date;
import java.util.List;

@Contract
public interface ClientRepository extends Repository{
    void saveIfNotExists(Client clientRef, Date creationDate);
    boolean clientExists(Client clientRef);
    List<Client> readAll(String tenant_ref);
}
