package org.capitalcompass.capitalcompassusers.repository;

import org.capitalcompass.capitalcompassusers.model.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TickerRepository extends JpaRepository<Ticker, Long> {

    Optional<Ticker> findBySymbol(String symbol);
}
