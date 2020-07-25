package org.example.auth.service.user.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.auth.service.ServiceResponse;
import org.example.auth.service.ServiceResponseCode;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserAdminResponse implements ServiceResponse, Serializable {
    private static final long serialVersionUID = 4717833379612084584L;
    ServiceResponseCode status = UserAdminServiceResponseCode.UNDEFINED_ADMIN_SERVICE_ERROR;

    public UserAdminResponse(ServiceResponseCode status) {
        this.status = status;
    }

    @Override
    public boolean isSuccess() {
        return status.equals(UserAdminServiceResponseCode.OK);
    }

    @Override
    public ServiceResponseCode getStatus() {
        return status;
    }
}
