package org.capitalcompass.userservice.service;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.userservice.entity.Ticker;
import org.capitalcompass.userservice.repository.TickerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Service class for handling operations related to Tickers.
 * Provides functionalities to check existence, save, and retrieve tickers.
 */
@Service
@RequiredArgsConstructor
public class TickerService {

    private final TickerRepository tickerRepository;

    /**
     * Checks if a ticker exists by its symbol.
     *
     * @param symbol The symbol of the ticker to check.
     * @return Boolean true if the ticker exists, false otherwise.
     */
    public Boolean existsBySymbol(String symbol) {
        return tickerRepository.existsBySymbol(symbol);
    }

    /**
     * Saves a list of ticker entities to the repository.
     *
     * @param tickers The list of Ticker entities to be saved.
     */
    public void saveTickers(List<Ticker> tickers) {
        tickerRepository.saveAllAndFlush(tickers);
    }

    /**
     * Finds and retrieves a list of tickers based on a set of symbols.
     *
     * @param symbols The set of ticker symbols to find.
     * @return A list of Ticker entities corresponding to the provided symbols.
     */
    public List<Ticker> findTickersBySymbols(Set<String> symbols) {
        return tickerRepository.findAllBySymbolIn(symbols);
    }
}
