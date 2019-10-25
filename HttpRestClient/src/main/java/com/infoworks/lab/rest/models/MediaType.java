package com.infoworks.lab.rest.models;

import java.nio.charset.Charset;

public class MediaType {

    public static String Key = "Content-Type";
    public static MediaType PLAIN_TEXT = new MediaType("text/plain; charset=UTF-8");
    public static MediaType JSON = new MediaType("application/json; charset=UTF-8");

    private String typeValue;
    MediaType(String type){
        typeValue = type;
    }

    @Override
    public String toString() {
        return typeValue;
    }

    public String key() {return Key;}
    public String value() {return toString();}
    public Charset charset(){return Charset.forName("UTF-8");}

}
