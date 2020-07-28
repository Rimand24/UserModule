package org.example.auth.service.user.account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.auth.service.ServiceResponse;
import org.example.auth.service.ServiceResponseCode;

@Data
@NoArgsConstructor
public class UserAccountResponse implements ServiceResponse {
    ServiceResponseCode status = UserAccountServiceResponseCode.UNDEFINED_ACCOUNT_SERVICE_ERROR;

    public UserAccountResponse(ServiceResponseCode status) {
        this.status = status;
    }


    @Override
    public boolean isSuccess() {
        return status.equals(UserAccountServiceResponseCode.OK);
    }

    @Override
    public ServiceResponseCode getStatus() {
        return status;
    }

}