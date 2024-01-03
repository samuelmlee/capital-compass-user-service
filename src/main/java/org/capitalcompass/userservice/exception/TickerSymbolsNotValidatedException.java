package org.capitalcompass.userservice.exception;

public class TickerSymbolsNotValidatedException extends RuntimeException {
    private static final long serialVersionUID = 3650557125496231075L;

    public TickerSymbolsNotValidatedException(String s) {
        super(s);
    }
}
