package org.capitalcompass.capitalcompassusers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Watchlist was not created by user")
public class WatchListNotOwnedByUserException extends RuntimeException {
    private static final long serialVersionUID = 8679077392478193231L;

    public WatchListNotOwnedByUserException(String message) {
        super(message);
    }
}
