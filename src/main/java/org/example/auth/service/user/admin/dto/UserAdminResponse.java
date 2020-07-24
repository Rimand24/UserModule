package org.example.auth.service.user.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.auth.service.ServiceResponse;
import org.example.auth.service.ServiceResponseCode;

@Data
@NoArgsConstructor
public class UserAdminResponse implements ServiceResponse {
    ServiceResponseCode status = UserAdminServiceResponseCode.UNDEFINED_ACCOUNT_SERVICE_ERROR;

    public UserAdminResponse(ServiceResponseCode status) {
        this.status = status;
    }

    @Override
    public boolean isSuccess() {
        return status.equals(UserAdminServiceResponseCode.OK);
    }

    @Override
    public ServiceResponseCode getStatus(){
        return status;
    }
}
