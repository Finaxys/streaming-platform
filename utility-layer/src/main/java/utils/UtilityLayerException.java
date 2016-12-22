package utils;

public class UtilityLayerException extends RuntimeException{

    public UtilityLayerException() {
        super();
    }

    public UtilityLayerException(String message, Throwable cause) {
        super(message, cause);
    }

    public UtilityLayerException(String message) {
        super(message);
    }

    public UtilityLayerException(Throwable cause) {
        super(cause);
    }
    
}
