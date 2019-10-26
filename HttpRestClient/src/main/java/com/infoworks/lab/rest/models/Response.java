package com.infoworks.lab.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.infoworks.lab.exceptions.HttpInvocationException;
import com.it.soul.lab.sql.entity.Entity;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response extends Entity {

    public static Response CreateErrorResponse(Throwable exp){
        Response newInstance = null;
        newInstance = new Response();
        newInstance.update(exp);
        return newInstance;
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
