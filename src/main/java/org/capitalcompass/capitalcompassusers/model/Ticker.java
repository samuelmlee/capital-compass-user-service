package org.capitalcompass.capitalcompassusers.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Ticker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String primaryExchange;
    
    private String currencyName;

    @ManyToMany(mappedBy = "tickers")
    private List<Watchlist> watchlists;
}
