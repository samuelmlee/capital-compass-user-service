package org.capitalcompass.userservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Symbol must not be blank for Ticker")
    @NotBlank
    private String symbol;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @ManyToMany(mappedBy = "tickers")
    @JsonIgnore
    private Set<Watchlist> watchlists = new HashSet<>();

    public void addWatchlist(Watchlist watchlist) {
        watchlists.add(watchlist);
    }

    public void removeWatchlist(Watchlist watchlist) {
        watchlists.remove(watchlist);
    }

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
