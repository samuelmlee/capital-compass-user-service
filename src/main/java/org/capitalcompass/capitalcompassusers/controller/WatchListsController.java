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
import javax.validation.Valid;
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
        String userSub = principal.getName();
        List<Watchlist> lists = watchListService.getWatchListsForUser(userSub);
        return ResponseEntity.ok(lists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Watchlist> getWatchListById(Principal principal, @PathVariable Long id) {
        Watchlist watchList;
        try {
            watchList = watchListService.getWatchListById(id, principal.getName());
            return ResponseEntity.ok(watchList);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createWatchlist(Principal principal, @Valid @RequestBody WatchlistRequest request) throws URISyntaxException {
        // TODO: Add custom Global Handler for WatchlistAlreadyExistsException
        Watchlist createdWatchList = watchListService.createWatchList(principal, request);
        URI location = new URI(
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/watchlists/")
                        .path(String.valueOf(createdWatchList.getId()))
                        .toUriString());
        return ResponseEntity.created(location).body(createdWatchList);
    }


}
