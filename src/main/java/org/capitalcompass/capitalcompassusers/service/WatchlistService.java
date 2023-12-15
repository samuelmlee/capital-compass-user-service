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
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
        String watchlistName = request.getName();

        if (watchListRepository.existsByName(watchlistName)) {
            throw new WatchlistAlreadyExistsException("Watchlist already exists with name : " + watchlistName);
        }
        Date date = new Date();
        Watchlist watchlist = Watchlist.builder()
                .userId(principal.getName())
                .name(request.getName())
                .tickers(request.getTickers())
                .creationDate(date)
                .lastUpdateDate(date)
                .build();
        return watchListRepository.save(watchlist);
    }

    @Transactional
    public Watchlist updateWatchlist(Principal principal, EditWatchlistRequest request) {
        Watchlist watchlistToUpdate = getWatchListById(request.getId(), principal.getName());
        watchlistToUpdate.setName(request.getName());

        watchlistToUpdate.getTickers().forEach(ticker ->
                ticker.getWatchlists().remove(watchlistToUpdate));
        watchlistToUpdate.getTickers().clear();

        for (Ticker tickerToAdd : request.getTickers()) {
            Ticker ticker = tickerService.findTickerBySymbol(tickerToAdd.getSymbol())
                    .orElseGet(() -> Ticker.builder()
                            .symbol(tickerToAdd.getSymbol())
                            .name(tickerToAdd.getName())
                            .build());
            ticker.getWatchlists().add(watchlistToUpdate);
            watchlistToUpdate.getTickers().add(ticker);
        }

        return watchListRepository.save(watchlistToUpdate);
    }

    private Watchlist getWatchListById(Long id, String userId) {
        Watchlist watchlist = watchListRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (!Objects.equals(watchlist.getUserId(), userId)) {
            throw new WatchListNotOwnedByUserException("Watchlist was not created by the user");
        }
        return watchlist;
    }
}
