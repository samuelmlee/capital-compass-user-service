package org.capitalcompass.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.userservice.dto.CreateWatchlistRequestDTO;
import org.capitalcompass.userservice.dto.EditWatchlistRequestDTO;
import org.capitalcompass.userservice.entity.Watchlist;
import org.capitalcompass.userservice.service.WatchlistService;
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
        String userSub = principal.getName();
        return watchlistService.createWatchList(userSub, request);
    }

    @PutMapping
    public Watchlist updateWatchlist(Principal principal, @Valid @RequestBody EditWatchlistRequestDTO request) {
        String userSub = principal.getName();
        return watchlistService.updateWatchlist(userSub, request);
    }

    @DeleteMapping("/{watchlistId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteWatchlist(Principal principal, @PathVariable Long watchlistId) {
        String userSub = principal.getName();
        watchlistService.deleteWatchlist(userSub, watchlistId);
    }


}
