package org.rimand.doc.service.storage;

public class StorageServiceException extends RuntimeException {

    public StorageServiceException() {
    }

    public StorageServiceException(String message) {
        super(message);
    }

    public StorageServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageServiceException(Throwable cause) {
        super(cause);
    }
}