package org.rimand.doc.service;

public interface ServiceResponse {
    boolean isSuccess();

    ServiceResponseCode getStatus();
}
