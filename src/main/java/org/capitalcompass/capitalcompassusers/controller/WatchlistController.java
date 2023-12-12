package org.capitalcompass.capitalcompassusers.controller;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.capitalcompassusers.model.Watchlist;
import org.capitalcompass.capitalcompassusers.model.WatchlistRequest;
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

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public Watchlist getWatchListById(Principal principal, @PathVariable Long id) {
        return watchlistService.getWatchListById(id, principal.getName());
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Watchlist createWatchlist(Principal principal, @Valid @RequestBody WatchlistRequest request) {
        return watchlistService.createWatchList(principal, request);
    }


}
