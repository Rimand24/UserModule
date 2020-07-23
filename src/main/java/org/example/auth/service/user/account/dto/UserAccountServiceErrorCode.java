package org.example.auth.service.user.account.dto;

import org.example.auth.service.ErrorCode;

public enum UserAccountServiceErrorCode implements ErrorCode {

    USERNAME_NOT_FOUND,
    USERNAME_ALREADY_EXISTS,
    PASSWORD_TOKEN_NOT_FOUND,
    PASSWORD_INCORRECT,
    EMAIL_NOT_FOUND,
    EMAIL_ALREADY_EXISTS,
    NEW_EMAIL_IS_THE_SAME,
    EMAIL_TOKEN_NOT_FOUND,
    TOKEN_NOT_VERIFIED,
    UNDEFINED_ACCOUNT_SERVICE_ERROR;

}