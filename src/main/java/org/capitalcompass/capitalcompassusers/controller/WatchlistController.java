package org.capitalcompass.capitalcompassusers.controller;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.capitalcompassusers.model.CreateWatchlistRequest;
import org.capitalcompass.capitalcompassusers.model.EditWatchlistRequest;
import org.capitalcompass.capitalcompassusers.model.Watchlist;
import org.capitalcompass.capitalcompassusers.service.WatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/users/watchlists")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Watchlist> getWatchlistsForUser(Principal principal) {
        String userSub = principal.getName();
        return watchlistService.getWatchListsForUser(userSub);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Watchlist createWatchlist(Principal principal, @Valid @RequestBody CreateWatchlistRequest request) {
        return watchlistService.createWatchList(principal, request);
    }

    @PutMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Watchlist updateWatchlist(Principal principal, @Valid @RequestBody EditWatchlistRequest request) {
        return watchlistService.updateWatchlist(principal, request);
    }


}
