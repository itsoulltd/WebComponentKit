package com.itsoul.lab.ledgerbook.repositories;


import com.infoworks.sql.executor.QueryExecutor;
import com.infoworks.sql.executor.SQLExecutor;
import com.infoworks.sql.query.QueryType;
import com.infoworks.sql.query.SQLInsertQuery;
import com.infoworks.sql.query.SQLQuery;
import com.infoworks.sql.query.SQLScalarQuery;
import com.infoworks.sql.query.models.Where;
import com.itsoul.lab.generalledger.entities.Client;
import com.itsoul.lab.generalledger.repositories.ClientRepository;
import org.jvnet.hk2.annotations.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * The Data Object Access support to abstract and encapsulate all access to the client table of the
 * data source.
 */
@Service
public class ClientRepositoryImpl implements ClientRepository {

    private Logger log = Logger.getLogger(this.getClass().getSimpleName());
    private QueryExecutor queryExecutor;

    public void setQueryExecutor(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    @Override
    public QueryExecutor getQueryExecutor() {
        return this.queryExecutor;
    }

    private SQLExecutor getExecutor(){
        if (queryExecutor instanceof SQLExecutor)
            return (SQLExecutor) queryExecutor;
        return null;
    }

    @Override
    public boolean clientExists(Client clientRef) {
        SQLScalarQuery query = new SQLQuery.Builder(QueryType.COUNT)
                .columns("ref")
                .on("client")
                .where(new Where("ref").isEqualTo(clientRef.getRef())
                        .and("tenant_ref").isEqualTo(clientRef.getTenantRef()))
                .build();
        //
        try {
            int count = getExecutor().getScalarValue(query);
            return count > 0;
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
        return false;
    }

    @Override
    public void saveIfNotExists(Client clientRef, Date creationDate) {
        if (!clientRef.isNull() && !clientExists(clientRef)){
            try {
                clientRef.setCreateDate(new java.sql.Timestamp(creationDate.getTime()));
                clientRef.insert(queryExecutor);
            } catch (SQLException e) {
                log.warning(e.getMessage());
            }
        }
    }

    public List<Client> readAll(String tenant_ref){
        try {
            List<Client> all = Client.read(Client.class, getExecutor(), new Where("tenant_ref").isEqualTo(tenant_ref));
            return all;
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
        return new ArrayList<>();
    }
}
