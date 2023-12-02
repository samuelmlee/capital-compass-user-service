package org.capitalcompass.capitalcompassusers.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
public class WatchlistRequest {
    @NotNull
    private String name;

    private Set<String> tickers;
}
