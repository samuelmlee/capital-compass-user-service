package org.capitalcompass.userservice.client;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.userservice.dto.ValidateTickerRequestDTO;
import org.capitalcompass.userservice.exception.TickerSymbolsNotValidatedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

/**
 * Client class to call Stocks Service APIs.
 * This class provides methods to communicate with the Stocks Service.
 */
@Component
@RequiredArgsConstructor
public class StocksServiceClient {

    private final RestTemplate restTemplate;

    @Value("${stock.service.uri}")
    private String stockServiceUri;

    /**
     * Registers a set of ticker symbols with the Stocks Service.
     *
     * @param tickerSymbols The set of ticker symbols to register.
     * @return A set of strings representing the registered ticker symbols.
     * @throws TickerSymbolsNotValidatedException if there is an error in registering
     *                                            the ticker symbols or in communication with the stocks service.
     */
    public Set<String> registerTickers(Set<String> tickerSymbols) {
        try {
            ValidateTickerRequestDTO request = ValidateTickerRequestDTO.builder()
                    .symbols(tickerSymbols)
                    .build();
            HttpEntity<ValidateTickerRequestDTO> entity = new HttpEntity<>(request);

            ResponseEntity<String[]> response = restTemplate.postForEntity(
                    stockServiceUri + "/v1/stocks/reference/tickers/register", entity, String[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new TickerSymbolsNotValidatedException("Error registering and validating ticker symbols. HTTP status: "
                        + response.getStatusCode());
            }
            return Set.of(response.getBody());
        } catch (RestClientResponseException e) {
            throw new TickerSymbolsNotValidatedException("Error communicating with the stocks service: "
                    + e.getMessage());
        }
    }
}
