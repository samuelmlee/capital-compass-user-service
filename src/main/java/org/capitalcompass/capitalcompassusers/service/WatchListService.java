package org.capitalcompass.capitalcompassusers.service;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.capitalcompassusers.model.Watchlist;
import org.capitalcompass.capitalcompassusers.model.WatchlistRequest;
import org.capitalcompass.capitalcompassusers.repository.WatchListRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.validation.Validator;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WatchListService {

    private final Validator validator;

    private final WatchListRepository watchListRepository;

    public List<Watchlist> getWatchListsForUser(String userSub) {
        return watchListRepository.findByUserSub(userSub);
    }

    public Watchlist createWatchList(Principal principal, WatchlistRequest request) {


        Date date = new Date();
        Watchlist watchlist = Watchlist.builder()
                .userId(principal.getName())
                .name(request.getName())
                .tickers(request.getTickers())
                .creationDate(date)
                .lastUpdateDate(date)
                .build();

        return this.watchListRepository.save(watchlist);

    }

    public Watchlist getWatchListById(Long id, String userSub) throws AccessDeniedException {
        Watchlist watchlist = this.watchListRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (!Objects.equals(watchlist.getUserId(), userSub)) {
            throw new AccessDeniedException("Watchlist is not created by the user");
        }
        return watchlist;
    }
}
