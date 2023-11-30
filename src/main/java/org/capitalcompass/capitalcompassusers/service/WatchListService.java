package org.capitalcompass.capitalcompassusers.service;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.capitalcompassusers.model.Watchlist;
import org.capitalcompass.capitalcompassusers.repository.WatchListRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchListService {

    private final WatchListRepository watchListRepository;

    public List<Watchlist> getWatchListsForUser(String userSub) {
        return watchListRepository.findByUserSub(userSub);
    }
}
