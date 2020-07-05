package org.example.auth.service.document;

public class DocumentServiceException extends RuntimeException {

    public DocumentServiceException() {
        super();
    }

    public DocumentServiceException(String message) {
        super(message);
    }

    public DocumentServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentServiceException(Throwable cause) {
        super(cause);
    }
}
