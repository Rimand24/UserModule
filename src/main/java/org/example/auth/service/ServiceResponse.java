package org.example.auth.service;

import org.example.auth.service.ErrorCode;

public interface ServiceResponse {
    void addError(ErrorCode errorCode);
}
