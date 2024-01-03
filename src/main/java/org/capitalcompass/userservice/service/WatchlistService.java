package org.capitalcompass.userservice.service;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.userservice.client.StocksServiceClient;
import org.capitalcompass.userservice.dto.CreateWatchlistRequestDTO;
import org.capitalcompass.userservice.dto.EditWatchlistRequestDTO;
import org.capitalcompass.userservice.entity.Ticker;
import org.capitalcompass.userservice.entity.Watchlist;
import org.capitalcompass.userservice.exception.TickerSymbolsNotValidatedException;
import org.capitalcompass.userservice.exception.WatchListNotOwnedByUserException;
import org.capitalcompass.userservice.exception.WatchlistAlreadyExistsException;
import org.capitalcompass.userservice.exception.WatchlistNotFoundException;
import org.capitalcompass.userservice.repository.WatchListRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    public Watchlist createWatchList(String userSub, CreateWatchlistRequestDTO request) {
        validateWatchlistName(request);

        Watchlist watchlist = buildWatchlist(userSub, request);
        List<Ticker> tickers = getTickersForWatchlist(request.getTickerSymbols());
        tickers.forEach(watchlist::addTicker);
        return watchListRepository.save(watchlist);
    }

    @Transactional
    public Watchlist updateWatchlist(String userSub, EditWatchlistRequestDTO request) {
        Watchlist watchlistToUpdate = getWatchListById(request.getId(), userSub);

        watchlistToUpdate.setName(request.getName());

        watchlistToUpdate.clearTickers();

        List<Ticker> updatedTickers = getTickersForWatchlist(request.getTickerSymbols());
        updatedTickers.forEach(watchlistToUpdate::addTicker);
        return watchListRepository.save(watchlistToUpdate);
    }

    @Transactional
    public void deleteWatchlist(String userSub, Long watchlistId) {
        Watchlist watchlistToDelete = getWatchListById(watchlistId, userSub);
        watchListRepository.delete(watchlistToDelete);
    }

    private Watchlist getWatchListById(Long id, String userId) {
        Watchlist watchlist = watchListRepository.findById(id)
                .orElseThrow(() -> new WatchlistNotFoundException("Watchlist not found for Id :" + id));

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

    private Watchlist buildWatchlist(String userSub, CreateWatchlistRequestDTO request) {
        Date date = new Date();
        return Watchlist.builder()
                .userId(userSub)
                .name(request.getName())
                .tickers(new HashSet<>())
                .creationDate(date)
                .lastUpdateDate(date)
                .build();
    }

    private List<Ticker> getTickersForWatchlist(Set<String> tickerSymbols) {
        Set<String> registeredSymbols = stocksServiceClient.registerTickers(tickerSymbols);

        Set<String> unvalidatedSymbols = tickerSymbols.stream()
                .filter(ticker -> !registeredSymbols.contains(ticker)).collect(Collectors.toSet());

        if (!unvalidatedSymbols.isEmpty()) {
            throw new TickerSymbolsNotValidatedException("The following ticker symbols for the watchlist could not be validated :" + unvalidatedSymbols);
        }

        saveNewTickers(registeredSymbols);

        return tickerService.findTickersBySymbols(registeredSymbols);
    }

    private void saveNewTickers(Set<String> registeredSymbols) {
        List<Ticker> newTickersToSave = registeredSymbols.stream()
                .filter(ticker -> !tickerService.existsBySymbol(ticker))
                .map(symbol ->
                        Ticker.builder()
                                .symbol(symbol)
                                .watchlists(new HashSet<>())
                                .build()
                ).collect(Collectors.toList());

        tickerService.saveTickers(newTickersToSave);
    }


}