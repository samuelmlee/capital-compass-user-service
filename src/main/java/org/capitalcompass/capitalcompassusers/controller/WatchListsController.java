package org.capitalcompass.capitalcompassusers.controller;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.capitalcompassusers.model.Watchlist;
import org.capitalcompass.capitalcompassusers.service.WatchListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users/watchlists")
@RequiredArgsConstructor
public class WatchListsController {

    private final WatchListService watchListService;

    @GetMapping
    public ResponseEntity<List<Watchlist>> getWatchListsForUser() {
        List<Watchlist> lists = this.watchListService.getWatchListsForUser("test");
        return ResponseEntity.ok(lists);
    }


}
