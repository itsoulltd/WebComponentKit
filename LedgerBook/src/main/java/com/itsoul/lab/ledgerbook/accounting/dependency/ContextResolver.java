package com.itsoul.lab.ledgerbook.accounting.dependency;

import com.itsoul.lab.generalledger.entities.Client;
import com.itsoul.lab.generalledger.services.AccountService;
import com.itsoul.lab.generalledger.services.TransferService;
import com.itsoul.lab.ledgerbook.connector.SourceConnector;

import java.sql.SQLException;

public interface ContextResolver extends AutoCloseable{
    /**
     * @return an instance of the AccountService providing account management
     */
    AccountService getAccountService();

    /**
     * @return an instance of the TransferService providing account transfers
     */
    TransferService getTransferService();

    /**
     * @param connector
     */
    void configureDataSource(SourceConnector connector) throws SQLException;

    /**
     * @param connector
     */
    void configureRepositories(SourceConnector connector, Client ref) throws SQLException;
}
