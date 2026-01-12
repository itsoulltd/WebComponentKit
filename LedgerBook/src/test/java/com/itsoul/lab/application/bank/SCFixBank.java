package com.itsoul.lab.application.bank;

import com.infoworks.tasks.queue.TaskQueue;
import com.infoworks.tasks.stack.TaskStack;
import com.infoworks.tasks.ExecutableTask;
import com.infoworks.objects.Message;
import com.infoworks.objects.Response;
import com.infoworks.connect.JDBCDriverClass;
import com.infoworks.utils.eventq.EventQueue;
import com.itsoul.lab.generalledger.entities.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

public class SCFixBank extends SCBank implements TheFixBank {

    private static Logger LOG = Logger.getLogger(SCBank.class.getSimpleName());
    private JDBCDriverClass driverClass;
    private String user;
    private String password;
    private TaskQueue queue;

    public SCFixBank(JDBCDriverClass driverClass, String manager, String password) {
        super(driverClass, manager, password);
        this.driverClass = driverClass;
        this.user = manager == null ? "manager" : manager;
        this.password = password == null ? "man@123" : password;
        this.queue = new EventQueue(Executors.newSingleThreadExecutor());
    }

    @Override
    public void transfer(String fromIban, String toIban, long transferAmount) {
        if (fromIban == null || fromIban.isEmpty()) return;
        if (toIban == null || toIban.isEmpty()) return;
        if (transferAmount <= 0L) return;
        getQueue().add(new TransferBalanceTask(getLedgerBook(), fromIban, toIban, transferAmount));
    }

    @Override
    public TaskQueue getQueue() {
        return queue;
    }

    @Override
    public void onTaskComplete(BiConsumer<Message, TaskStack.State> consumer) {
        this.queue.onTaskComplete(consumer);
    }

    private static class TransferBalanceTask extends ExecutableTask<Message, Response> {

        private LedgerBook ledgerBook;
        private String fromIban;
        private String toIban;
        private long transferAmount;

        public TransferBalanceTask(LedgerBook ledgerBook, String fromIban, String toIban, long transferAmount) {
            this.ledgerBook = ledgerBook;
            this.fromIban = fromIban;
            this.toIban = toIban;
            this.transferAmount = transferAmount;
        }

        private String getPrefix() {
            return "CH";
        }

        private Money addBalance(LedgerBook ledgerBook, String fromIban, String toIban, long transferAmount) {
            //TODO: transferAmount has to be 0.00 or any combination with at least 2 digit after precision.
            // e.g. 1002001.00 or 1200933.97 etc
            BigDecimal transferMoney = BigDecimal.valueOf(transferAmount);
            transferMoney = transferMoney.setScale(2, RoundingMode.UP);
            Money money = ledgerBook.makeTransactions(
                    "transfer", UUID.randomUUID().toString().substring(0, 20)
                    , String.format("%s@%s", getPrefix(), fromIban)
                    , transferMoney.toPlainString()
                    , String.format("%s@%s", getPrefix(), toIban)
            );
            return money;
        }

        @Override
        public Response execute(Message message) throws RuntimeException {
            Response response = new Response().setStatus(500);
            try {
                long fromBalance = ledgerBook.readBalance(getPrefix(), fromIban).getAmount().longValue();
                if(fromBalance >= transferAmount) {
                    addBalance(ledgerBook, fromIban, toIban, transferAmount);
                }
                if (fromBalance >= transferAmount)
                    response.setStatus(200).setMessage(String.format("Transfer: %s -> %s : %s", fromIban, toIban, transferAmount));
                else
                    response.setError(String.format("Insufficient Fund: %s : %s", fromIban, fromBalance));
            } catch (Exception e) {
                response.setError(e.getMessage());
            }
            return response;
        }

    }

}
