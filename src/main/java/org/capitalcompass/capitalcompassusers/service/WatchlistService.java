package org.capitalcompass.capitalcompassusers.service;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.capitalcompassusers.exception.WatchListNotOwnedByUserException;
import org.capitalcompass.capitalcompassusers.exception.WatchlistAlreadyExistsException;
import org.capitalcompass.capitalcompassusers.model.CreateWatchlistRequest;
import org.capitalcompass.capitalcompassusers.model.EditWatchlistRequest;
import org.capitalcompass.capitalcompassusers.model.Ticker;
import org.capitalcompass.capitalcompassusers.model.Watchlist;
import org.capitalcompass.capitalcompassusers.repository.WatchListRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchListRepository watchListRepository;

    private final TickerService tickerService;

    public List<Watchlist> getWatchListsForUser(String userId) {
        return watchListRepository.findByUserId(userId);
    }

    @Transactional
    public Watchlist createWatchList(Principal principal, CreateWatchlistRequest request) {
        validateWatchlistName(request);

        Watchlist watchlist = buildWatchlist(principal, request);
        watchlist.getTickers().forEach(ticker -> ticker.getWatchlists().add(watchlist));
        return watchListRepository.save(watchlist);
    }

    @Transactional
    public Watchlist updateWatchlist(Principal principal, EditWatchlistRequest request) {
        Watchlist watchlistToUpdate = getWatchListById(request.getId(), principal.getName());
        watchlistToUpdate.setName(request.getName());

        clearWatchlistFromTickers(watchlistToUpdate);

        Set<Ticker> updatedTickers = getTickersForRequest(request.getTickers());
        watchlistToUpdate.setTickers(updatedTickers);
        watchlistToUpdate.getTickers().forEach(ticker -> ticker.getWatchlists().add(watchlistToUpdate));
        return watchListRepository.save(watchlistToUpdate);
    }

    private Watchlist getWatchListById(Long id, String userId) {
        Watchlist watchlist = watchListRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (!Objects.equals(watchlist.getUserId(), userId)) {
            throw new WatchListNotOwnedByUserException("Watchlist was not created by the user");
        }
        return watchlist;
    }

    private void clearWatchlistFromTickers(Watchlist watchlistToUpdate) {
        watchlistToUpdate.getTickers().forEach(ticker ->
                ticker.getWatchlists().remove(watchlistToUpdate));
        watchlistToUpdate.getTickers().clear();
    }


    private void validateWatchlistName(CreateWatchlistRequest request) {
        String watchlistName = request.getName();

        if (watchListRepository.existsByName(watchlistName)) {
            throw new WatchlistAlreadyExistsException("Watchlist already exists with name : " + watchlistName);
        }
    }

    private Watchlist buildWatchlist(Principal principal, CreateWatchlistRequest request) {
        Set<Ticker> tickers = getTickersForRequest(request.getTickers());
        Date date = new Date();
        return Watchlist.builder()
                .userId(principal.getName())
                .name(request.getName())
                .tickers(tickers)
                .creationDate(date)
                .lastUpdateDate(date)
                .build();
    }

    private Set<Ticker> getTickersForRequest(Set<Ticker> requestTickers) {
        Set<Ticker> tickers = new HashSet<>();
        for (Ticker watchlistTicker : requestTickers) {
            Ticker ticker = tickerService.findTickerBySymbol(watchlistTicker.getSymbol())
                    .orElseGet(() ->
                            Ticker.builder()
                                    .symbol(watchlistTicker.getSymbol())
                                    .name(watchlistTicker.getName())
                                    .watchlists(new HashSet<>())
                                    .build()
                    );
            tickers.add(ticker);
        }
        return tickers;
    }
}
