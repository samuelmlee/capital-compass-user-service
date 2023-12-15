package org.capitalcompass.capitalcompassusers.service;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.capitalcompassusers.model.Ticker;
import org.capitalcompass.capitalcompassusers.repository.TickerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TickerService {

    private final TickerRepository tickerRepository;

    public Optional<Ticker> findTickerBySymbol(String symbol) {
        return tickerRepository.findBySymbol(symbol);
    }
}
