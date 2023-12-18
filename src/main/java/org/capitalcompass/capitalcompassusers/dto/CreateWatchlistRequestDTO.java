package org.capitalcompass.capitalcompassusers.dto;

import lombok.Data;
import org.capitalcompass.capitalcompassusers.entity.Ticker;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class CreateWatchlistRequestDTO {

    @NotNull
    private String name;

    private Set<Ticker> tickers;
}
