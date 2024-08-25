package org.capitalcompass.userservice.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @NotBlank
    private String userId;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private Date creationDate;

    @NotNull
    private Date lastUpdateDate;
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            joinColumns = @JoinColumn(name = "watchlist_id"),
            inverseJoinColumns = @JoinColumn(name = "ticker_id")
    )
    private Set<Ticker> tickers = new HashSet<>();


    public void addTicker(Ticker ticker) {
        tickers.add(ticker);
        ticker.addWatchlist(this);
    }

    public void removeTicker(Ticker ticker) {
        tickers.remove(ticker);
        ticker.removeWatchlist(this);
    }

    public void clearTickers() {
        tickers.forEach(ticker -> ticker.removeWatchlist(this));
        tickers.clear();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Watchlist watchlist = (Watchlist) o;
        return Objects.equals(userId, watchlist.userId) && Objects.equals(name, watchlist.name) && Objects.equals(creationDate, watchlist.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, creationDate);
    }
}
