package org.capitalcompass.userservice.exception;

public class WatchListNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -5319252026422670329L;

    public WatchListNotFoundException(String m) {
        super(m);
    }
}
