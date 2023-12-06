package org.capitalcompass.capitalcompassusers.controller;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.capitalcompassusers.model.Watchlist;
import org.capitalcompass.capitalcompassusers.model.WatchlistRequest;
import org.capitalcompass.capitalcompassusers.service.WatchListService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/users/watchlists")
@RequiredArgsConstructor
public class WatchListsController {

    private final WatchListService watchListService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Watchlist> getWatchListsForUser(Principal principal) {
        String userSub = principal.getName();
        return watchListService.getWatchListsForUser(userSub);
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public Watchlist getWatchListById(Principal principal, @PathVariable Long id) {
        return watchListService.getWatchListById(id, principal.getName());
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Watchlist createWatchlist(Principal principal, @Valid @RequestBody WatchlistRequest request) {
        return watchListService.createWatchList(principal, request);
    }


}
