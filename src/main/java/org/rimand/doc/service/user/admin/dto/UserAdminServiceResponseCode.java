package org.rimand.doc.service.user.admin.dto;


import org.rimand.doc.service.ServiceResponseCode;

public enum UserAdminServiceResponseCode implements ServiceResponseCode {

    OK,
    BASIC_USER_ROLE_CANNOT_BE_REMOVED,
    USERNAME_NOT_FOUND,
    USER_ALREADY_BLOCKED,
    USER_NOT_BLOCKED,
    USER_ALREADY_HAVE_THIS_ROLE,
    USER_DO_NOT_HAVE_THIS_ROLE,
    UNDEFINED_ADMIN_SERVICE_ERROR;

}
