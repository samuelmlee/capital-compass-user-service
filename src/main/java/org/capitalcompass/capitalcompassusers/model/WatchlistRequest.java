package org.capitalcompass.capitalcompassusers.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class WatchlistRequest {
    @NotNull
    private String name;

    private Set<String> tickers;
}
