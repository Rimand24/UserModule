package org.rimand.doc.service.user.account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.rimand.doc.service.ServiceResponse;
import org.rimand.doc.service.ServiceResponseCode;

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