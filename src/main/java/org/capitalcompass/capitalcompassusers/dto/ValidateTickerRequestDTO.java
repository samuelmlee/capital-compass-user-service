package org.capitalcompass.capitalcompassusers.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidateTickerRequestDTO {

    private String symbol;
}
