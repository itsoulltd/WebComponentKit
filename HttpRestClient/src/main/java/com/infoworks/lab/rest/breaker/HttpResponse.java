package com.infoworks.lab.rest.breaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class HttpResponse implements AutoCloseable {
    private CircuitBreaker.Status status;
    private Integer code;
    private String payload;
    @Override
    public void close() throws Exception {
        //TODO
    }

    public HttpResponse() {
        this(HttpURLConnection.HTTP_NOT_FOUND);
    }

    public HttpResponse(Integer code) {
        this.setCode(code);
    }

    public CircuitBreaker.Status getStatus() {
        return status;
    }

    public void setStatus(CircuitBreaker.Status status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
        this.status = (code == HttpURLConnection.HTTP_OK) ? CircuitBreaker.Status.CLOSED : CircuitBreaker.Status.OPEN;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setPayload(InputStream payload) {
        if (payload == null) return;
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(payload))){
            //
            String inputLine;
            StringBuffer response = new StringBuffer();
            //
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            setPayload(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
