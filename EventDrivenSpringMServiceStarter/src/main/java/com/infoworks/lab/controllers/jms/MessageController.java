package com.infoworks.lab.controllers.jms;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.domain.beans.tasks.mocks.AbortTask;
import com.infoworks.lab.domain.beans.tasks.mocks.ConsolPrintTask;
import com.infoworks.lab.rest.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Queue;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class MessageController {

    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Autowired
    @Qualifier("testQueue")
    private Queue queue;

    @Autowired
    private JmsTemplate jmsTemplate;

    @GetMapping("/message/{message}")
    public ResponseEntity<String> publish(@PathVariable("message") final String message){
        //
        jmsTemplate.convertAndSend(queue, message);
        return new ResponseEntity(message, HttpStatus.OK);
    }

    @Autowired @Qualifier("taskDispatchQueue")
    private TaskQueue taskQueue;

    @GetMapping("/queue/task/{message}")
    public ResponseEntity<String> addToQueue(@PathVariable("message") final String message){
        //
        Task task;
        if (message.trim().toLowerCase().startsWith("abort")){
            Message mac = new Message().setPayload(String.format("{\"message\":\"%s\"}", message));
            AbortTask abortTask = new AbortTask();
            abortTask.setMessage(mac);
            task = abortTask;
        }else{
            Message mac = new Message().setPayload(String.format("{\"message\":\"%s\"}", message));
            ConsolPrintTask consolPrintTask = new ConsolPrintTask();
            consolPrintTask.setMessage(mac);
            task = consolPrintTask;
        }
        taskQueue.add(task);
        //Test:
        taskQueue.onTaskComplete((message1, state) -> {
            System.out.println("RUNNING ON " + Thread.currentThread().getName());
            System.out.println(state.name());
            System.out.println(message1.toString());
        });
        System.out.println("/queue/task/ " + "RETURNING");
        System.out.println("RUNNING ON " + Thread.currentThread().getName());
        //
        return new ResponseEntity(message, HttpStatus.OK);
    }

}
