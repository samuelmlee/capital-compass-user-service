package org.capitalcompass.userservice.exception;

public class KeycloakClientErrorException extends RuntimeException {
    public KeycloakClientErrorException(String s) {
        super(s);
    }
}
