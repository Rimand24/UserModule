package org.rimand.doc.service.user.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.rimand.doc.service.ServiceResponse;
import org.rimand.doc.service.ServiceResponseCode;

@Data
@NoArgsConstructor
public class UserAdminResponse implements ServiceResponse {
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
