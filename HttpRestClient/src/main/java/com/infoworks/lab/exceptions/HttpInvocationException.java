package com.infoworks.lab.exceptions;


import com.infoworks.lab.rest.models.Response;

public class HttpInvocationException extends Exception {

    private Response info;

    public HttpInvocationException(Response info) {
        this(String.format("Http Status:%s; ErrorMessage:%s",info.getStatus(),info.getError()));
        if (info != null) this.info = info;
    }

    public HttpInvocationException(String message) {
        super(message);
        info = new Response();
        info.setError(message);
        info.setStatus(500);
    }

    public Integer getStatus(){return info.getStatus();}

    @Override
    public String getMessage() {
        return info.getError();
    }

}
