package org.example.auth.service;

public interface ServiceResponse {
    boolean isSuccess();

    ServiceResponseCode getStatus();
}
