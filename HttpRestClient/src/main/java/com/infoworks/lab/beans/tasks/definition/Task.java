package com.infoworks.lab.beans.tasks.definition;

import com.infoworks.lab.rest.models.Message;

import java.util.function.Function;

public interface Task {
    Task next();
    void linkedTo(Task task);
    Message execute(Message message) throws RuntimeException;
    Message abort(Message message) throws RuntimeException;
    default Message getMessage() {return null;}

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
