package org.capitalcompass.capitalcompassusers.exception;

public class WatchlistNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -5319252026422670329L;

    public WatchlistNotFoundException(String m) {
        super(m);
    }
}
