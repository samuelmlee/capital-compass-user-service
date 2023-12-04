package org.capitalcompass.capitalcompassusers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Watchlist was not created by user")
public class WatchListNotOwnedByUserException extends RuntimeException {
    public WatchListNotOwnedByUserException(String message) {
        super(message);
    }
}
