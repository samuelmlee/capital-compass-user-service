package org.capitalcompass.capitalcompassusers.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class UsersGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {WatchlistNotFoundException.class})
    public ResponseEntity<?> handleWatchlistNotFound(WatchlistNotFoundException watchlistNotFoundException, WebRequest request) {
        return super.handleExceptionInternal(watchlistNotFoundException,
                watchlistNotFoundException.getMessage(), new HttpHeaders(),
                HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {WatchlistAlreadyExistsException.class})
    public ResponseEntity<?> handleWatchlistAlreadyExists(WatchlistAlreadyExistsException watchlistAlreadyExistsException, WebRequest request) {
        return super.handleExceptionInternal(watchlistAlreadyExistsException,
                watchlistAlreadyExistsException.getMessage(), new HttpHeaders(),
                HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = {WatchListNotOwnedByUserException.class})
    public ResponseEntity<?> handleWatchlistNotOwnedByUser(WatchListNotOwnedByUserException watchListNotOwnedByUserException, WebRequest request) {
        return super.handleExceptionInternal(watchListNotOwnedByUserException,
                watchListNotOwnedByUserException.getMessage(), new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {TickerSymbolsNotValidatedException.class})
    public ResponseEntity<?> handleTickerSymbolNotValidated(TickerSymbolsNotValidatedException tickerSymbolsNotValidatedException, WebRequest request) {
        return super.handleExceptionInternal(tickerSymbolsNotValidatedException,
                tickerSymbolsNotValidatedException.getMessage(), new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request);
    }


}