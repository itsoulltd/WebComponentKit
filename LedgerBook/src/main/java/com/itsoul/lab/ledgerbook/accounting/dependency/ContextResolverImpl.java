package com.itsoul.lab.ledgerbook.accounting.dependency;

import com.it.soul.lab.connect.io.ScriptRunner;
import com.it.soul.lab.sql.QueryExecutor;
import com.itsoul.lab.generalledger.entities.Client;
import com.itsoul.lab.generalledger.repositories.AccountRepository;
import com.itsoul.lab.generalledger.repositories.ClientRepository;
import com.itsoul.lab.generalledger.repositories.TransactionRepository;
import com.itsoul.lab.generalledger.services.AccountService;
import com.itsoul.lab.generalledger.services.TransferService;
import com.itsoul.lab.ledgerbook.connector.SourceConnector;
import com.itsoul.lab.ledgerbook.repositories.AccountRepositoryImpl;
import com.itsoul.lab.ledgerbook.repositories.ClientRepositoryImpl;
import com.itsoul.lab.ledgerbook.repositories.TransactionRepositoryImpl;
import com.itsoul.lab.ledgerbook.services.AccountServiceImpl;
import com.itsoul.lab.ledgerbook.services.TransferServiceImpl;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class ContextResolverImpl implements ContextResolver {

    private Logger log = Logger.getLogger(this.getClass().getSimpleName());
    private ServiceLocator locator;
    private Set<AutoCloseable> bagOfClosable;

    public ContextResolverImpl() {
        locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
    }

    public ServiceLocator getLocator() {
        return locator;
    }

    public Set<AutoCloseable> getBagOfClosable() {
        if (bagOfClosable == null){
            bagOfClosable = new HashSet<>();
        }
        return bagOfClosable;
    }

    @Override
    public AccountService getAccountService() {
        return getLocator().getService(AccountServiceImpl.class);
    }

    @Override
    public TransferService getTransferService() {
        return getLocator().getService(TransferServiceImpl.class);
    }

    @Override
    public void configureRepositories(SourceConnector connector, Client ref) throws SQLException{
        //Create DataSource DAO here:
        QueryExecutor executor = connector.getExecutor();
        getBagOfClosable().add(executor);

        AccountRepository repository = getLocator().getService(AccountRepositoryImpl.class);
        repository.setClientRef(ref);
        repository.setQueryExecutor(executor);
        //
        TransactionRepository transacRepository = getLocator().getService(TransactionRepositoryImpl.class);
        transacRepository.setClientRef(ref);
        transacRepository.setQueryExecutor(executor);
        //
        ClientRepository clientRepository = getLocator().getService(ClientRepositoryImpl.class);
        clientRepository.setQueryExecutor(executor);
        clientRepository.saveIfNotExists(ref, new Date());
    }

    @Override
    public void configureDataSource(SourceConnector connector) throws SQLException{
        if (connector.generateSchema()) {
            try(Connection connection = connector.getConnection()) {
                ScriptRunner runner = new ScriptRunner();
                File file = new File("db/"+connector.schema());
                String[] cmds = runner.commands(runner.createStream(file));
                runner.execute(cmds, connection);
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (getBagOfClosable().size() > 0){
            for (AutoCloseable a : getBagOfClosable()) {
                a.close();
            }
        }
    }
}
