package org.capitalcompass.capitalcompassusers.service;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.capitalcompassusers.client.StocksServiceClient;
import org.capitalcompass.capitalcompassusers.dto.CreateWatchlistRequestDTO;
import org.capitalcompass.capitalcompassusers.dto.EditWatchlistRequestDTO;
import org.capitalcompass.capitalcompassusers.entity.Ticker;
import org.capitalcompass.capitalcompassusers.entity.Watchlist;
import org.capitalcompass.capitalcompassusers.exception.WatchListNotOwnedByUserException;
import org.capitalcompass.capitalcompassusers.exception.WatchlistAlreadyExistsException;
import org.capitalcompass.capitalcompassusers.repository.WatchListRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchListRepository watchListRepository;

    private final TickerService tickerService;

    private final StocksServiceClient stocksServiceClient;

    @Transactional
    public List<Watchlist> getWatchListsForUser(String userId) {
        return watchListRepository.findByUserId(userId);
    }

    @Transactional
    public Watchlist createWatchList(Principal principal, CreateWatchlistRequestDTO request) {
        validateWatchlistName(request);

        Watchlist watchlist = buildWatchlist(principal, request);
        Set<Ticker> tickers = getTickersForRequest(request.getTickerSymbols());
        tickers.forEach(watchlist::addTicker);
        return watchListRepository.save(watchlist);
    }

    @Transactional
    public Watchlist updateWatchlist(Principal principal, EditWatchlistRequestDTO request) {
        Watchlist watchlistToUpdate = getWatchListById(request.getId(), principal.getName());
        watchlistToUpdate.setName(request.getName());

        watchlistToUpdate.clearTickers();

        Set<Ticker> updatedTickers = getTickersForRequest(request.getTickerSymbols());
        updatedTickers.forEach(watchlistToUpdate::addTicker);
        return watchListRepository.save(watchlistToUpdate);
    }

    private Watchlist getWatchListById(Long id, String userId) {
        Watchlist watchlist = watchListRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (!Objects.equals(watchlist.getUserId(), userId)) {
            throw new WatchListNotOwnedByUserException("Watchlist was not created by the user");
        }
        return watchlist;
    }

    private void validateWatchlistName(CreateWatchlistRequestDTO request) {
        String watchlistName = request.getName();

        if (watchListRepository.existsByName(watchlistName)) {
            throw new WatchlistAlreadyExistsException("Watchlist already exists with name : " + watchlistName);
        }
    }

    private Watchlist buildWatchlist(Principal principal, CreateWatchlistRequestDTO request) {
        Date date = new Date();
        return Watchlist.builder()
                .userId(principal.getName())
                .name(request.getName())
                .tickers(new HashSet<>())
                .creationDate(date)
                .lastUpdateDate(date)
                .build();
    }

    private Set<Ticker> getTickersForRequest(Set<String> tickerSymbols) {

        Set<String> validatedSymbols = stocksServiceClient.validateBatchTickers(tickerSymbols);

        return tickerSymbols.stream()
                .filter(validatedSymbols::contains)
                .map(ticker -> tickerService.findTickerBySymbol(ticker)
                        .orElseGet(() -> Ticker.builder()
                                .symbol(ticker)
                                .watchlists(new HashSet<>())
                                .build())
                )
                .collect(Collectors.toSet());
    }
}