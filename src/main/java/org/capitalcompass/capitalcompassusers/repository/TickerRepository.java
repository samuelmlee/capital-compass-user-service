package org.capitalcompass.capitalcompassusers.repository;

import org.capitalcompass.capitalcompassusers.entity.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface TickerRepository extends JpaRepository<Ticker, Long> {


    Boolean existsBySymbol(String symbol);

    List<Ticker> findAllBySymbolIn(Set<String> symbols);
}
