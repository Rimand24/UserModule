package org.rimand.doc.service.user.account.dto;


import org.rimand.doc.service.ServiceResponseCode;

import java.io.Serializable;

public enum UserAccountServiceResponseCode implements ServiceResponseCode {

    OK,
    USERNAME_NOT_FOUND,
    USERNAME_ALREADY_EXISTS,
    PASSWORD_TOKEN_NOT_FOUND,
    PASSWORD_INCORRECT,
    EMAIL_NOT_FOUND,
    EMAIL_ALREADY_EXISTS,
    NEW_EMAIL_IS_THE_SAME,
    EMAIL_TOKEN_NOT_FOUND,
    ALREADY_ACTIVATED,
    TOKEN_NOT_VERIFIED,
    UNDEFINED_ACCOUNT_SERVICE_ERROR;

}