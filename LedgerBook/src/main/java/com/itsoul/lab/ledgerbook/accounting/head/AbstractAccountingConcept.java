package com.itsoul.lab.ledgerbook.accounting.head;


import com.itsoul.lab.generalledger.entities.Client;
import com.itsoul.lab.generalledger.exception.InfrastructureException;
import com.itsoul.lab.generalledger.services.AccountService;
import com.itsoul.lab.generalledger.services.TransferService;
import com.itsoul.lab.ledgerbook.accounting.dependency.ContextResolver;
import com.itsoul.lab.ledgerbook.connector.SourceConnector;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public abstract class AbstractAccountingConcept implements AutoCloseable{

    protected Logger LOG = Logger.getLogger(this.getClass().getSimpleName());
    private TransferService transferService;
    private AccountService accountService;
    private ContextResolver factory;

    protected void init(String className, SourceConnector connector, Client ref) throws InfrastructureException {
        try {
            factory = (ContextResolver) Class.forName(className).newInstance();
            transferService = factory.getTransferService();
            accountService = factory.getAccountService();
            factory.configureDataSource(connector);
            factory.configureRepositories(connector, ref);
        } catch (Exception e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
            throw new InfrastructureException(e.getCause());
        }
    }

    public TransferService getTransferService() {
        return transferService;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    @Override
    public void close() {
        try {
            if (factory != null) factory.close();
        } catch (Exception e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
        }
    }
}
