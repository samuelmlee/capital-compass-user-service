package org.capitalcompass.userservice.service;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.userservice.entity.Ticker;
import org.capitalcompass.userservice.repository.TickerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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

    public List<Ticker> findTickersBySymbols(Set<String> symbols) {
        return tickerRepository.findAllBySymbolIn(symbols);
    }
}
