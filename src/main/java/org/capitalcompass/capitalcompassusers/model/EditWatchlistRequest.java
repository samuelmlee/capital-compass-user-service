package org.capitalcompass.capitalcompassusers.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class EditWatchlistRequest {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    private Set<Ticker> tickers;
}
