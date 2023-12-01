package com.infoworks.lab.beans.tasks.impl;

import com.infoworks.lab.beans.tasks.definition.TaskStack;
import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import org.junit.Test;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class ChainOfRequestTest {

    @Test
    public void requestFlowTest() {
        CountDownLatch latch = new CountDownLatch(1);
        //
        TaskStack stack = TaskStack.createSync(true);

        stack.push(new ResponseAggregatorTask("https://example.com:8081/api/wtf?val=1"
                , new Message().setPayload("Hi there 1!")));

        stack.push(new ResponseAggregatorTask("https://example.com:8081/api/wtf?val=2"
                , new Message().setPayload("Hi there 2!")));

        stack.push(new ResponseAggregatorTask("https://example.com:8081/api/wtf?val=3"
                , new Message().setPayload("Hi there 3!")));

        stack.push(new ResponseAggregatorTask("https://example.com:8081/api/wtf?val=4"
                , new Message().setPayload("Hi there 4!")));
        //
        stack.commit(true, (message, status) -> {
            if (message == null) {
                System.out.println("No Message Return!");
            } else {
                System.out.println("\n");
                System.out.println("State: " + status);
                //System.out.println(message.toString());
                if (message instanceof BagOfResponse) {
                    ((BagOfResponse) message).getBagOfResponse()
                            .forEach((key, val) -> {
                                System.out.println(key + " " + val.getMessage());
                            });
                }
            }
            latch.countDown();
        });
        //
        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

    ///////////////////////////////////////////////////////////////////

    private static class ResponseAggregatorTask extends ExecutableTask<Message, Response> {

        private String url;
        private Message toSend;

        public ResponseAggregatorTask() {}

        public ResponseAggregatorTask(String url, Message toSend) {
            this.url = url;
            this.toSend = toSend;
        }

        @Override
        public Response execute(Message message) throws RuntimeException {
            //Make a Rest-Api Call:
            Response response = DummyRestService.get(url, toSend);
            //
            if (message == null) {
                message = new BagOfResponse();
            }
            if (message instanceof BagOfResponse) {
                //Add response to bag:
                ((BagOfResponse) message).getBagOfResponse().put(url, response);
            }
            return (Response) message;
        }
    }

    private static class BagOfResponse extends Response {

        private Map<String, Response> bagOfResponse;

        public BagOfResponse() {}

        public Map<String, Response> getBagOfResponse() {
            if (bagOfResponse == null) {
                bagOfResponse = new ConcurrentHashMap<>();
            }
            return bagOfResponse;
        }
    }

    ///////////////////////////////////////////////////////////////////

    private static class DummyRestService {

        private static Random random = new Random(1232);

        public static Response get(String url, Message body) {
            return new Response().setStatus(200).setMessage("#Number:" + random.nextInt());
        }
    }
}
