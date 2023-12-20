package org.capitalcompass.capitalcompassusers.service;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.capitalcompassusers.entity.Ticker;
import org.capitalcompass.capitalcompassusers.repository.TickerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TickerService {

    private final TickerRepository tickerRepository;

    public Boolean existsBySymbol(String symbol) {
        return tickerRepository.existsBySymbol(symbol);
    }

    public void saveTickers(List<Ticker> tickers) {
        tickerRepository.saveAllAndFlush(tickers);
    }

    public List<Ticker> findTickersBySymbols(List<String> symbols) {
        return tickerRepository.findAllBySymbolIn(symbols);
    }
}
