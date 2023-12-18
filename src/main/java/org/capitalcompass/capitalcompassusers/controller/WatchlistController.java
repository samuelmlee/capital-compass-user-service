package org.capitalcompass.capitalcompassusers.controller;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.capitalcompassusers.dto.CreateWatchlistRequestDTO;
import org.capitalcompass.capitalcompassusers.dto.EditWatchlistRequestDTO;
import org.capitalcompass.capitalcompassusers.entity.Watchlist;
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
    public List<Watchlist> getWatchlistsForUser(Principal principal) {
        String userSub = principal.getName();
        return watchlistService.getWatchListsForUser(userSub);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Watchlist createWatchlist(Principal principal, @Valid @RequestBody CreateWatchlistRequestDTO request) {
        return watchlistService.createWatchList(principal, request);
    }

    @PutMapping
    public Watchlist updateWatchlist(Principal principal, @Valid @RequestBody EditWatchlistRequestDTO request) {
        return watchlistService.updateWatchlist(principal, request);
    }


}
