package com.esewa.csmreactivedemo.customexception;

public class BucketNameDoesNotExistsException extends RuntimeException{
    public BucketNameDoesNotExistsException(String message) {
        super(message);
    }
}
