package com.infoworks.lab.beans.tasks.definition;

import com.infoworks.lab.rest.models.Message;

import java.util.function.Function;

public interface Task<In extends Message, Out extends Message> {
    Task next();
    void linkedTo(Task task);
    Out execute(In message) throws RuntimeException;
    Out abort(In message) throws RuntimeException;
    default In getMessage() {return null;}
    default void setMessage(In message) {}

    default Function<Message, Message> getConverter() {return null;}
    default MessageConverter getMessageConverter() {return null;}
    default Message convert(Message result){
        if (getConverter() != null)
            return getConverter().apply(result);
        if (getMessageConverter() != null)
            return getMessageConverter().convert(result);
        return result;
    }

    interface MessageConverter{
        Message convert(Message message);
    }
}
