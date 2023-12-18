package org.capitalcompass.capitalcompassusers.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ValidateTickerRequestDTO {

    private Set<String> symbols;
}
