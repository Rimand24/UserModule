package org.example.auth.service.user.account.dto;

import lombok.Data;
import org.example.auth.service.ErrorCode;
import org.example.auth.service.ServiceResponse;

@Data
public class UserAccountResponse implements ServiceResponse {
    boolean success;
    ErrorCode error;

    @Override
    public void addError(ErrorCode errorCode) {
        this.error=errorCode;
    }
}
