package org.example.auth.service.user.admin.dto;

import org.example.auth.service.ServiceResponseCode;

public enum UserAdminServiceResponseCode implements ServiceResponseCode {

    OK,
BASIC_USER_ROLE_CANNOT_BE_REMOVED,

    USERNAME_NOT_FOUND,
    USER_ALREADY_BLOCKED,
    USER_NOT_BLOCKED,
    USER_ALREADY_HAVE_THIS_ROLE,
    USER_DO_NOT_HAVE_THIS_ROLE,
//    EMAIL_ALREADY_EXISTS,
//    NEW_EMAIL_IS_THE_SAME,
//    EMAIL_TOKEN_NOT_FOUND,
//    TOKEN_NOT_VERIFIED,


    UNDEFINED_ACCOUNT_SERVICE_ERROR;


}
