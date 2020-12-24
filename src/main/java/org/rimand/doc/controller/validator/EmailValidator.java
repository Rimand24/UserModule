package org.rimand.doc.controller.validator;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

//    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+] + (.[_A-Za-z0-9-]+)*@ + [A-Za-z0-9-] + (.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";
//    static final String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
//    static final String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)+";
//    static final String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";
//    static final String EMAIL_PATTERN = "^" + ATOM + "+(\\." + ATOM + "+)*@" + DOMAIN + "|" + IP_DOMAIN + ")$";
    static final String EMAIL_PATTERN = "[\\w.]+@[\\w]+\\.[\\w]+";

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return (validateEmail(email));
    }

    private boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}