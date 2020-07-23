package org.example.auth.service.user.account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.auth.service.ErrorCode;
import org.example.auth.service.ServiceResponse;

@Data
@NoArgsConstructor
public class UserAccountResponse implements ServiceResponse {
    boolean success;
    ErrorCode error = UserAccountServiceErrorCode.UNDEFINED_ACCOUNT_SERVICE_ERROR;

    public UserAccountResponse(boolean success) {
        this.success = success;
    }

    public UserAccountResponse(boolean success, ErrorCode error) {
        this.success = success;
        this.error = error;
    }
}
