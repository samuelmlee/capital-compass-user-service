package org.capitalcompass.capitalcompassusers.controller;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.capitalcompassusers.model.Watchlist;
import org.capitalcompass.capitalcompassusers.model.WatchlistRequest;
import org.capitalcompass.capitalcompassusers.service.WatchListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/users/watchlists")
@RequiredArgsConstructor
public class WatchListsController {

    private final WatchListService watchListService;

    @GetMapping
    public ResponseEntity<List<Watchlist>> getWatchListsForUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String userSub = principal.getName();
        List<Watchlist> lists = this.watchListService.getWatchListsForUser(userSub);
        return ResponseEntity.ok(lists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Watchlist> getWatchListById(Principal principal, @PathVariable Long id) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Watchlist watchList;
        try {
            watchList = this.watchListService.getWatchListById(id, principal.getName());
            return ResponseEntity.ok(watchList);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping
    public ResponseEntity<Watchlist> createWatchlist(Principal principal, @RequestBody WatchlistRequest request) throws URISyntaxException {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Watchlist createdWatchList = this.watchListService.createWatchList(principal, request);
        URI location = new URI(
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/watchlists/")
                        .path(String.valueOf(createdWatchList.getId()))
                        .toUriString());
        return ResponseEntity.created(location).build();


    }


}
