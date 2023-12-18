package org.capitalcompass.capitalcompassusers.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class EditWatchlistRequestDTO {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    private Set<String> tickerSymbols;
}
