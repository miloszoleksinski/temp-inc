package io.kontak.anomaly.storage.exception;

public class AnomalyServiceException extends RuntimeException {
    public AnomalyServiceException(String message){
        super(message);
    }

    public AnomalyServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
