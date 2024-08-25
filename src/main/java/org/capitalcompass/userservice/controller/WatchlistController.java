package org.capitalcompass.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.userservice.dto.CreateWatchListRequestDTO;
import org.capitalcompass.userservice.dto.EditWatchListRequestDTO;
import org.capitalcompass.userservice.entity.Watchlist;
import org.capitalcompass.userservice.service.WatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * REST controller for managing user watch lists.
 * This controller provides endpoints for retrieving, creating, updating,
 * and deleting watch lists for a user.
 */
@RestController
@RequestMapping("/v1/users/watchlists")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    /**
     * Retrieves all watch lists for the authenticated user.
     *
     * @param principal The Principal object containing the logged in user's information.
     * @return A list of Watchlist entities belonging to the authenticated user.
     */
    @GetMapping
    public List<Watchlist> getWatchlistsForUser(Principal principal) {
        String userSub = principal.getName();
        return watchlistService.getWatchListsForUser(userSub);
    }


    /**
     * Creates a new watchlist for the authenticated user.
     *
     * @param principal The Principal object containing the logged in user's information.
     * @param request   The CreateWatchlistRequestDTO containing the details of the watchlist to be created.
     * @return The created Watchlist entity.
     */
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Watchlist createWatchlist(Principal principal, @Valid @RequestBody CreateWatchListRequestDTO request) {
        String userSub = principal.getName();
        return watchlistService.createWatchList(userSub, request);
    }

    /**
     * Updates an existing watchlist for the authenticated user.
     *
     * @param principal The Principal object containing the logged in user's information.
     * @param request   The EditWatchlistRequestDTO containing the updated details of the watchlist.
     * @return The updated Watchlist entity.
     */
    @PutMapping
    public Watchlist updateWatchlist(Principal principal, @Valid @RequestBody EditWatchListRequestDTO request) {
        String userSub = principal.getName();
        return watchlistService.updateWatchlist(userSub, request);
    }

    /**
     * Deletes a specific watchlist for the authenticated user.
     *
     * @param principal   The Principal object containing the logged in user's information.
     * @param watchlistId The ID of the watchlist to be deleted.
     */
    @DeleteMapping("/{watchlistId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteWatchlist(Principal principal, @PathVariable Long watchlistId) {
        String userSub = principal.getName();
        watchlistService.deleteWatchlist(userSub, watchlistId);
    }


}
