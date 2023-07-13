package com.esewa.csmreactivedemo.customexception;

public class InvalidContentTypeException extends  RuntimeException{
    public InvalidContentTypeException(String message) {
        super(message);
    }
}
