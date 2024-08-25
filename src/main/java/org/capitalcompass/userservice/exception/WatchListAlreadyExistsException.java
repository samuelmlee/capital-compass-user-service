package org.capitalcompass.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Watchlist with a same name already exists ")
public class WatchListAlreadyExistsException extends RuntimeException {
    public WatchListAlreadyExistsException(String message) {
        super(message);
    }
}
