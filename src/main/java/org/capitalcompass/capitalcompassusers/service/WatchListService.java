package org.capitalcompass.capitalcompassusers.service;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.capitalcompassusers.exception.WatchlistAlreadyExistsException;
import org.capitalcompass.capitalcompassusers.model.Watchlist;
import org.capitalcompass.capitalcompassusers.model.WatchlistRequest;
import org.capitalcompass.capitalcompassusers.repository.WatchListRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WatchListService {

    private final WatchListRepository watchListRepository;

    public List<Watchlist> getWatchListsForUser(String userId) {
        return watchListRepository.findByUserId(userId);
    }

    public Watchlist createWatchList(Principal principal, WatchlistRequest request) {
        String watchlistName = request.getName();

        if (!watchListRepository.findByName(watchlistName).isEmpty()) {
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

    public Watchlist getWatchListById(Long id, String userId) throws AccessDeniedException {
        Watchlist watchlist = watchListRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (!Objects.equals(watchlist.getUserId(), userId)) {
            throw new AccessDeniedException("Watchlist was not created by the user");
        }
        return watchlist;
    }
}
