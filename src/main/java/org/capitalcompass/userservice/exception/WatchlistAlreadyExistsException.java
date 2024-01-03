package org.capitalcompass.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Watchlist with a same name already exists ")
public class WatchlistAlreadyExistsException extends RuntimeException {
    public WatchlistAlreadyExistsException(String message) {
        super(message);
    }
}
