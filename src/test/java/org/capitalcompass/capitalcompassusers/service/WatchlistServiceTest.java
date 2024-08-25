package org.capitalcompass.capitalcompassusers.service;

import org.capitalcompass.userservice.client.StocksServiceClient;
import org.capitalcompass.userservice.dto.CreateWatchListRequestDTO;
import org.capitalcompass.userservice.dto.EditWatchListRequestDTO;
import org.capitalcompass.userservice.entity.Ticker;
import org.capitalcompass.userservice.entity.Watchlist;
import org.capitalcompass.userservice.exception.TickerSymbolsNotValidatedException;
import org.capitalcompass.userservice.exception.WatchListAlreadyExistsException;
import org.capitalcompass.userservice.exception.WatchListNotFoundException;
import org.capitalcompass.userservice.exception.WatchListNotOwnedByUserException;
import org.capitalcompass.userservice.repository.WatchListRepository;
import org.capitalcompass.userservice.service.TickerService;
import org.capitalcompass.userservice.service.WatchlistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WatchlistServiceTest {

    @Mock
    private WatchListRepository watchListRepository;

    @Mock
    private StocksServiceClient stocksServiceClient;

    @Mock
    private TickerService tickerService;

    @InjectMocks
    private WatchlistService watchlistService;


    @Test
    public void getWatchListsForUserOK() {
        String userId = "user1";
        Date creationDate = new Date();

        Watchlist savedWatchlist = Watchlist.builder()
                .userId(userId)
                .name("Watchlist 1")
                .tickers(Set.of(
                        new Ticker(1L, "AAPL", new HashSet<>()),
                        new Ticker(2L, "MSFT", new HashSet<>())))
                .creationDate(creationDate)
                .lastUpdateDate(creationDate)
                .build();

        List<Watchlist> savedWatchlists = List.of(savedWatchlist);
        when(watchListRepository.findByUserId(userId)).thenReturn(savedWatchlists);

        List<Watchlist> result = watchlistService.getWatchListsForUser(userId);

        assertEquals(savedWatchlists, result);
        verify(watchListRepository).findByUserId(userId);
    }

    @Test
    public void getWatchListsForUserEmpty() {
        String userId = "user1";

        when(watchListRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        List<Watchlist> result = watchlistService.getWatchListsForUser(userId);

        assertEquals(result, Collections.emptyList());
        verify(watchListRepository).findByUserId(userId);
    }

    @Test
    public void getWatchListsForUserRepoError() {
        String userId = "user1";
        String exceptionMessage = "Timed out";

        when(watchListRepository.findByUserId(userId)).thenThrow(new QueryTimeoutException(exceptionMessage));

        Exception exception = assertThrows(DataAccessException.class, () -> {
            watchlistService.getWatchListsForUser(userId);
        });

        assertEquals(exception.getMessage(), exceptionMessage);

        verify(watchListRepository).findByUserId(userId);
    }

    @Test
    public void createWatchListOK() {
        String userId = "user1";
        Date creationDate = new Date();
        Set<String> symbolsSet = Set.of("AAPL", "MSFT");

        CreateWatchListRequestDTO request = new CreateWatchListRequestDTO();
        request.setName("Tech Stocks");
        request.setTickerSymbols(symbolsSet);

        Watchlist savedWatchlist = Watchlist.builder()
                .userId(userId)
                .name(request.getName())
                .tickers(Set.of(
                        new Ticker(1L, "AAPL", new HashSet<>()),
                        new Ticker(2L, "MSFT", new HashSet<>())))
                .creationDate(creationDate)
                .lastUpdateDate(creationDate)
                .build();
        when(watchListRepository.existsByNameAndUserId(request.getName(), userId)).thenReturn(false);
        when(stocksServiceClient.registerTickers(request.getTickerSymbols())).thenReturn(request.getTickerSymbols());
        when(watchListRepository.save(any(Watchlist.class))).thenReturn(savedWatchlist);

        Watchlist result = watchlistService.createWatchList(userId, request);

        assertEquals(savedWatchlist, result);
        verify(watchListRepository).existsByNameAndUserId(anyString(), anyString());
        verify(stocksServiceClient).registerTickers(anySet());
        verify(watchListRepository).save(any(Watchlist.class));
    }

    @Test
    public void createWatchListNoSymbolsOK() {
        String userId = "user1";
        Date creationDate = new Date();

        CreateWatchListRequestDTO request = new CreateWatchListRequestDTO();
        request.setName("Tech Stocks");
        request.setTickerSymbols(Collections.emptySet());

        Watchlist savedWatchlist = Watchlist.builder()
                .userId(userId)
                .name(request.getName())
                .tickers(Collections.emptySet())
                .creationDate(creationDate)
                .lastUpdateDate(creationDate)
                .build();
        when(watchListRepository.existsByNameAndUserId(request.getName(), userId)).thenReturn(false);

        when(watchListRepository.save(any(Watchlist.class))).thenReturn(savedWatchlist);

        Watchlist result = watchlistService.createWatchList(userId, request);

        assertEquals(savedWatchlist, result);
        verify(watchListRepository).existsByNameAndUserId(anyString(), anyString());
        verify(stocksServiceClient, never()).registerTickers(anySet());
        verify(watchListRepository).save(any(Watchlist.class));
    }

    @Test
    void createWatchListExistingNameError() {
        String userId = "user1";
        Set<String> symbolsSet = Set.of("AAPL", "MSFT");

        CreateWatchListRequestDTO request = new CreateWatchListRequestDTO();
        request.setName("Tech Stocks");
        request.setTickerSymbols(symbolsSet);

        when(watchListRepository.existsByNameAndUserId(request.getName(), userId)).thenReturn(true);

        assertThrows(WatchListAlreadyExistsException.class, () -> {
            watchlistService.createWatchList(userId, request);
        });

        verify(watchListRepository, never()).save(any(Watchlist.class));
    }

    @Test
    void createWatchListTickerNotValidatedError() {
        String userId = "user1";
        Set<String> symbolsSet = Set.of("AAPL", "MSFT");

        CreateWatchListRequestDTO request = new CreateWatchListRequestDTO();
        request.setName("Tech Stocks");
        request.setTickerSymbols(symbolsSet);

        when(watchListRepository.existsByNameAndUserId(anyString(), anyString())).thenReturn(false);
        when(stocksServiceClient.registerTickers(anySet())).thenReturn(Set.of("AAPL"));

        assertThrows(TickerSymbolsNotValidatedException.class, () -> {
            watchlistService.createWatchList(userId, request);
        });

        verify(watchListRepository, never()).save(any(Watchlist.class));
    }

    @Test
    void createWatchListRepoError() {
        String userId = "user1";
        Set<String> symbolsSet = Set.of("AAPL", "MSFT");

        CreateWatchListRequestDTO request = new CreateWatchListRequestDTO();
        request.setName("Tech Stocks");
        request.setTickerSymbols(symbolsSet);

        List<Ticker> mockTickers = List.of(
                new Ticker(1L, "AAPL", new HashSet<>()),
                new Ticker(2L, "MSFT", new HashSet<>()));

        when(watchListRepository.existsByNameAndUserId(anyString(), anyString())).thenReturn(false);
        when(stocksServiceClient.registerTickers(anySet())).thenReturn(new HashSet<>(request.getTickerSymbols()));
        when(tickerService.findTickersBySymbols(anySet())).thenReturn(mockTickers);
        when(watchListRepository.save(any(Watchlist.class))).thenThrow(new DataIntegrityViolationException("Database error"));

        assertThrows(DataAccessException.class, () -> {
            watchlistService.createWatchList(userId, request);
        });

        verify(watchListRepository).save(any(Watchlist.class));
    }

    @Test
    void updateWatchlistOK() {
        String userId = "user1";
        Set<String> symbolsSet = Set.of("AAPL", "MSFT");
        Date creationDate = new Date();

        List<Ticker> updatedTickers = List.of(
                new Ticker(1L, "AAPL", new HashSet<>()),
                new Ticker(2L, "MSFT", new HashSet<>()));

        EditWatchListRequestDTO request = new EditWatchListRequestDTO();
        request.setName("Tech Stocks 2");
        request.setTickerSymbols(symbolsSet);

        Watchlist existingWatchlist = Watchlist.builder()
                .userId(userId)
                .name("Tech Stocks")
                .tickers(Stream.of(
                                new Ticker(1L, "AAPL", new HashSet<>()))
                        .collect(Collectors.toCollection(HashSet::new)))
                .creationDate(creationDate)
                .lastUpdateDate(creationDate)
                .build();

        Watchlist updatedWatchlist = Watchlist.builder()
                .userId(userId)
                .name("Tech Stocks 2")
                .tickers(new HashSet<>(updatedTickers))
                .creationDate(creationDate)
                .lastUpdateDate(creationDate)
                .build();

        when(watchListRepository.findById(request.getId())).thenReturn(Optional.of(existingWatchlist));
        when(watchListRepository.save(any(Watchlist.class))).thenReturn(updatedWatchlist);
        when(stocksServiceClient.registerTickers(request.getTickerSymbols())).thenReturn(request.getTickerSymbols());
        when(tickerService.findTickersBySymbols(request.getTickerSymbols())).thenReturn(updatedTickers);

        Watchlist result = watchlistService.updateWatchlist(userId, request);

        assertEquals(updatedWatchlist, result);
    }

    @Test
    void updateWatchlistNoSymbolsOK() {
        String userId = "user1";
        Date creationDate = new Date();

        EditWatchListRequestDTO request = new EditWatchListRequestDTO();
        request.setName("Tech Stocks 2");
        request.setTickerSymbols(Collections.emptySet());

        Watchlist existingWatchlist = Watchlist.builder()
                .userId(userId)
                .name("Tech Stocks")
                .tickers(Stream.of(
                                new Ticker(1L, "AAPL", new HashSet<>()))
                        .collect(Collectors.toCollection(HashSet::new)))
                .creationDate(creationDate)
                .lastUpdateDate(creationDate)
                .build();

        Watchlist updatedWatchlist = Watchlist.builder()
                .userId(userId)
                .name("Tech Stocks 2")
                .tickers(Collections.emptySet())
                .creationDate(creationDate)
                .lastUpdateDate(creationDate)
                .build();

        when(watchListRepository.findById(request.getId())).thenReturn(Optional.of(existingWatchlist));
        when(watchListRepository.save(any(Watchlist.class))).thenReturn(updatedWatchlist);

        Watchlist result = watchlistService.updateWatchlist(userId, request);

        assertEquals(updatedWatchlist, result);
        
        verify(stocksServiceClient, never()).registerTickers(anySet());
        verify(watchListRepository).save(any(Watchlist.class));
    }

    @Test
    void updateWatchlistWatchlistNotFoundError() {
        String userId = "user1";
        Set<String> symbolsSet = Set.of("AAPL", "MSFT");

        EditWatchListRequestDTO request = new EditWatchListRequestDTO();
        request.setName("Tech Stocks 2");
        request.setTickerSymbols(symbolsSet);

        when(watchListRepository.findById(request.getId())).thenReturn(Optional.empty());

        assertThrows(WatchListNotFoundException.class, () -> watchlistService.updateWatchlist(userId, request));
    }

    @Test
    void updateWatchlistWatchlistNotOwnedError() {
        String userId = "user1";
        Set<String> symbolsSet = Set.of("AAPL", "MSFT");
        Date creationDate = new Date();

        EditWatchListRequestDTO request = new EditWatchListRequestDTO();
        request.setName("Tech Stocks 2");
        request.setTickerSymbols(symbolsSet);

        Watchlist existingWatchlist = Watchlist.builder()
                .userId("user2")
                .name("Tech Stocks")
                .tickers(Set.of(
                        new Ticker(1L, "AAPL", new HashSet<>())))
                .creationDate(creationDate)
                .lastUpdateDate(creationDate)
                .build();

        when(watchListRepository.findById(request.getId())).thenReturn(Optional.of(existingWatchlist));

        assertThrows(WatchListNotOwnedByUserException.class, () -> watchlistService.updateWatchlist(userId, request));
    }

    @Test
    void updateWatchlistInvalidSymbolError() {
        String userId = "user1";
        Set<String> symbolsSet = Set.of("AAPL", "MSFT");
        Date creationDate = new Date();

        EditWatchListRequestDTO request = new EditWatchListRequestDTO();
        request.setName("Tech Stocks 2");
        request.setTickerSymbols(symbolsSet);

        Watchlist existingWatchlist = Watchlist.builder()
                .userId(userId)
                .name("Tech Stocks")
                .tickers(Stream.of(
                                new Ticker(1L, "AAPL", new HashSet<>()))
                        .collect(Collectors.toCollection(HashSet::new)))
                .creationDate(creationDate)
                .lastUpdateDate(creationDate)
                .build();

        when(watchListRepository.findById(request.getId())).thenReturn(Optional.of(existingWatchlist));
        when(stocksServiceClient.registerTickers(request.getTickerSymbols())).thenReturn(Set.of("AAPL"));

        assertThrows(TickerSymbolsNotValidatedException.class, () -> watchlistService.updateWatchlist(userId, request));
    }

    @Test
    void updateWatchlistRepoError() {
        String userId = "user1";
        Set<String> symbolsSet = Set.of("AAPL", "MSFT");
        Date creationDate = new Date();

        EditWatchListRequestDTO request = new EditWatchListRequestDTO();
        request.setName("Tech Stocks 2");
        request.setTickerSymbols(symbolsSet);

        Watchlist existingWatchlist = Watchlist.builder()
                .userId(userId)
                .name("Tech Stocks")
                .tickers(Stream.of(
                                new Ticker(1L, "AAPL", new HashSet<>()))
                        .collect(Collectors.toCollection(HashSet::new)))
                .creationDate(creationDate)
                .lastUpdateDate(creationDate)
                .build();

        when(watchListRepository.findById(request.getId())).thenReturn(Optional.of(existingWatchlist));
        when(stocksServiceClient.registerTickers(request.getTickerSymbols())).thenReturn(request.getTickerSymbols());
        when(watchListRepository.save(any(Watchlist.class))).thenThrow(new DataAccessException("Database error") {
        });

        assertThrows(DataAccessException.class, () -> watchlistService.updateWatchlist(userId, request));
    }

    @Test
    public void deleteWatchlistOK() {
        String userId = "user1";
        Long watchlistId = 1L;
        Date creationDate = new Date();

        Watchlist existingWatchlist = Watchlist.builder()
                .id(watchlistId)
                .userId(userId)
                .name("Tech Stocks")
                .tickers(Stream.of(
                                new Ticker(1L, "AAPL", new HashSet<>()))
                        .collect(Collectors.toCollection(HashSet::new)))
                .creationDate(creationDate)
                .lastUpdateDate(creationDate)
                .build();

        when(watchListRepository.findById(watchlistId)).thenReturn(Optional.of(existingWatchlist));

        watchlistService.deleteWatchlist(userId, watchlistId);

        verify(watchListRepository).delete(existingWatchlist);
    }

    @Test
    void deleteWatchlistNotFoundError() {
        String userId = "user1";
        Long watchlistId = 1L;

        when(watchListRepository.findById(watchlistId)).thenReturn(Optional.empty());

        assertThrows(WatchListNotFoundException.class, () -> watchlistService.deleteWatchlist(userId, watchlistId));
    }

    @Test
    void deleteWatchlistNotOwnedError() {
        String userId = "user1";
        Long watchlistId = 1L;
        Date creationDate = new Date();

        Watchlist watchlistToDelete = Watchlist.builder()
                .id(watchlistId)
                .userId("user2")
                .name("Tech Stocks")
                .tickers(Stream.of(
                                new Ticker(1L, "AAPL", new HashSet<>()))
                        .collect(Collectors.toCollection(HashSet::new)))
                .creationDate(creationDate)
                .lastUpdateDate(creationDate)
                .build();

        when(watchListRepository.findById(watchlistId)).thenReturn(Optional.of(watchlistToDelete));

        assertThrows(WatchListNotOwnedByUserException.class, () -> watchlistService.deleteWatchlist(userId, watchlistId));
    }

    @Test
    void deleteWatchlistRepoError() {
        String userId = "user1";
        Long watchlistId = 1L;
        Date creationDate = new Date();

        Watchlist watchlistToDelete = Watchlist.builder()
                .id(watchlistId)
                .userId(userId)
                .name("Tech Stocks")
                .tickers(Stream.of(
                                new Ticker(1L, "AAPL", new HashSet<>()))
                        .collect(Collectors.toCollection(HashSet::new)))
                .creationDate(creationDate)
                .lastUpdateDate(creationDate)
                .build();

        when(watchListRepository.findById(watchlistId)).thenReturn(Optional.of(watchlistToDelete));
        doThrow(new DataAccessException("Database error") {
        }).when(watchListRepository).delete(any(Watchlist.class));

        assertThrows(DataAccessException.class, () -> watchlistService.deleteWatchlist(userId, watchlistId));
    }


}
