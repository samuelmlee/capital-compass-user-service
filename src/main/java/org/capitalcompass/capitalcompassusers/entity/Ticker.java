package org.capitalcompass.capitalcompassusers.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Ticker {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @NotBlank
    private String symbol;

    @ManyToMany(mappedBy = "tickers")
    @JsonIgnore
    private Set<Watchlist> watchlists = new HashSet<>();

    @Override
    public int hashCode() {
        return Objects.hash(id, symbol, watchlists);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticker ticker = (Ticker) o;
        return Objects.equals(symbol, ticker.symbol);
    }
}