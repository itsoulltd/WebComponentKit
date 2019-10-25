package com.infoworks.lab.exceptions;


import com.infoworks.lab.rest.models.Response;

public class InternalServerError extends HttpInvocationException {
    public InternalServerError(Response info) {
        super(info);
    }
}
