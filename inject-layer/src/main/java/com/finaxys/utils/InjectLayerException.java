package com.finaxys.utils;

/**
 * Created by finaxys on 1/6/16.
 */
public class InjectLayerException extends RuntimeException{
    public InjectLayerException() {
        super();
    }

    public InjectLayerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InjectLayerException(String message) {
        super(message);
    }

    public InjectLayerException(Throwable cause) {
        super(cause);
    }
    
}
