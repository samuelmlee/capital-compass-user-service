package org.capitalcompass.capitalcompassusers.controller;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.capitalcompassusers.model.Watchlist;
import org.capitalcompass.capitalcompassusers.service.WatchListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
