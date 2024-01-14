package org.capitalcompass.userservice.service;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.userservice.client.StocksServiceClient;
import org.capitalcompass.userservice.dto.CreateWatchlistRequestDTO;
import org.capitalcompass.userservice.dto.EditWatchlistRequestDTO;
import org.capitalcompass.userservice.entity.Ticker;
import org.capitalcompass.userservice.entity.Watchlist;
import org.capitalcompass.userservice.exception.TickerSymbolsNotValidatedException;
import org.capitalcompass.userservice.exception.WatchListNotOwnedByUserException;
import org.capitalcompass.userservice.exception.WatchlistAlreadyExistsException;
import org.capitalcompass.userservice.exception.WatchlistNotFoundException;
import org.capitalcompass.userservice.repository.WatchListRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing user watch lists.
 * Provides functionalities to create, update, retrieve, and delete watch lists,
 * as well as manage ticker symbols associated with them.
 */
@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchListRepository watchListRepository;

    private final TickerService tickerService;

    private final StocksServiceClient stocksServiceClient;

    /**
     * Retrieves all watch lists for a specified user.
     *
     * @param userId The user identifier.
     * @return A list of Watchlist entities belonging to the user.
     */
    @Transactional
    public List<Watchlist> getWatchListsForUser(String userId) {
        return watchListRepository.findByUserId(userId);
    }

    /**
     * Creates a new watchlist for a user.
     *
     * @param userSub The user identifier.
     * @param request The DTO containing details for creating a new watchlist.
     * @return The newly created Watchlist entity.
     * @throws WatchlistAlreadyExistsException if a watchlist with the same name already exists.
     */
    @Transactional
    public Watchlist createWatchList(String userSub, CreateWatchlistRequestDTO request) {
        validateWatchlistName(request.getName(), userSub);

        Watchlist watchlist = buildWatchlist(userSub, request);
        List<Ticker> tickers = getTickersForWatchlist(request.getTickerSymbols());
        tickers.forEach(watchlist::addTicker);
        return watchListRepository.save(watchlist);
    }

    /**
     * Updates an existing watchlist for a user.
     *
     * @param userSub The user identifier.
     * @param request The DTO containing updated details for the watchlist.
     * @return The updated Watchlist entity.
     * @throws WatchlistNotFoundException       if the watchlist to update is not found.
     * @throws WatchListNotOwnedByUserException if the watchlist is not owned by the user.
     */
    @Transactional
    public Watchlist updateWatchlist(String userSub, EditWatchlistRequestDTO request) {
        Watchlist watchlistToUpdate = getWatchListById(request.getId(), userSub);

        watchlistToUpdate.setName(request.getName());

        watchlistToUpdate.clearTickers();

        List<Ticker> updatedTickers = getTickersForWatchlist(request.getTickerSymbols());
        updatedTickers.forEach(watchlistToUpdate::addTicker);
        return watchListRepository.save(watchlistToUpdate);
    }

    /**
     * Deletes a specific watchlist for a user.
     *
     * @param userSub     The user identifier.
     * @param watchlistId The ID of the watchlist to be deleted.
     * @throws WatchlistNotFoundException       if the watchlist to delete is not found.
     * @throws WatchListNotOwnedByUserException if the watchlist is not owned by the user.
     */
    @Transactional
    public void deleteWatchlist(String userSub, Long watchlistId) {
        Watchlist watchlistToDelete = getWatchListById(watchlistId, userSub);
        watchListRepository.delete(watchlistToDelete);
    }

    /**
     * Retrieves a watchlist by its ID, ensuring it belongs to the specified user.
     *
     * @param id     The ID of the watchlist.
     * @param userId The user identifier.
     * @return The Watchlist entity.
     * @throws WatchlistNotFoundException       if the watchlist is not found.
     * @throws WatchListNotOwnedByUserException if the watchlist does not belong to the user.
     */
    private Watchlist getWatchListById(Long id, String userId) {
        Watchlist watchlist = watchListRepository.findById(id)
                .orElseThrow(() -> new WatchlistNotFoundException("Watchlist not found for Id :" + id));

        if (!Objects.equals(watchlist.getUserId(), userId)) {
            throw new WatchListNotOwnedByUserException("Watchlist was not created by the user");
        }
        return watchlist;
    }

    /**
     * Validates if a watchlist name is already in use.
     *
     * @param watchlistName The watchlist name to validate.
     * @param userId        The userId owning the watchlist.
     * @throws WatchlistAlreadyExistsException if the watchlist name already exists.
     */
    private void validateWatchlistName(String watchlistName, String userId) {

        if (watchListRepository.existsByNameAndUserId(watchlistName, userId)) {
            throw new WatchlistAlreadyExistsException("Watchlist already exists with name : " + watchlistName);
        }
    }

    /**
     * Constructs a Watchlist entity from the provided DTO.
     *
     * @param userSub The user identifier.
     * @param request The DTO containing watchlist details.
     * @return The constructed Watchlist entity.
     */
    private Watchlist buildWatchlist(String userSub, CreateWatchlistRequestDTO request) {
        Date date = new Date();
        return Watchlist.builder()
                .userId(userSub)
                .name(request.getName())
                .tickers(new HashSet<>())
                .creationDate(date)
                .lastUpdateDate(date)
                .build();
    }

    /**
     * Retrieves and validates ticker symbols, throwing an exception if any cannot be validated.
     *
     * @param tickerSymbols The set of ticker symbols to validate.
     * @return A list of Ticker entities for the validated ticker symbols.
     * @throws TickerSymbolsNotValidatedException if any ticker symbols cannot be validated.
     */
    private List<Ticker> getTickersForWatchlist(Set<String> tickerSymbols) {
        Set<String> registeredSymbols = stocksServiceClient.registerTickers(tickerSymbols);

        Set<String> unvalidatedSymbols = tickerSymbols.stream()
                .filter(ticker -> !registeredSymbols.contains(ticker)).collect(Collectors.toSet());

        if (!unvalidatedSymbols.isEmpty()) {
            throw new TickerSymbolsNotValidatedException("The following ticker symbols for the watchlist could not be validated :" + unvalidatedSymbols);
        }

        saveNewTickers(registeredSymbols);

        return tickerService.findTickersBySymbols(registeredSymbols);
    }

    /**
     * Saves new tickers that are not already persisted in the database.
     *
     * @param registeredSymbols The set of ticker symbols to save.
     */
    private void saveNewTickers(Set<String> registeredSymbols) {
        List<Ticker> newTickersToSave = registeredSymbols.stream()
                .filter(ticker -> !tickerService.existsBySymbol(ticker))
                .map(symbol ->
                        Ticker.builder()
                                .symbol(symbol)
                                .watchlists(new HashSet<>())
                                .build()
                ).collect(Collectors.toList());

        tickerService.saveTickers(newTickersToSave);
    }


}