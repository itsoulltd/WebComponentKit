package com.infoworks.lab.rest.models;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.it.soul.lab.sql.entity.Entity;

public class Response extends Entity {

    public static <P extends Response> P CreateErrorResponse(Throwable exp, Class<P> type){
        Response newInstance = null;
        try {
            newInstance = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            newInstance = new Response();
        }
        newInstance.update(exp);
        return (P) newInstance;
    }

    public void update(Throwable exp){
        if (exp instanceof HttpInvocationException){
            status = ((HttpInvocationException) exp).getStatus();
        }else {
            status = 500;
        }
        error = exp.getMessage();
    }

    private Integer status = 200;
    private String error;

    public Response() {}

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
