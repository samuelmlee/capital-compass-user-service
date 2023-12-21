package org.capitalcompass.capitalcompassusers.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.capitalcompass.capitalcompassusers.dto.ValidateTickerRequestDTO;
import org.capitalcompass.capitalcompassusers.exception.TickerSymbolsNotValidatedException;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Log4j2
public class StocksServiceClient {

    @LoadBalanced
    private final RestTemplate restTemplate;

    private final String STOCKS_SERVICE_URL = "http://STOCKS/v1/stocks/reference/tickers";

    public Set<String> registerBatchTickers(Set<String> tickerSymbols) {

        ValidateTickerRequestDTO request = ValidateTickerRequestDTO.builder()
                .symbols(tickerSymbols)
                .build();
        HttpEntity<ValidateTickerRequestDTO> entity = new HttpEntity<>(request);

        try {
            ResponseEntity<Set<String>> response = restTemplate.exchange(
                    STOCKS_SERVICE_URL + "/register", HttpMethod.POST, entity,
                    new ParameterizedTypeReference<Set<String>>() {
                    });

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new TickerSymbolsNotValidatedException("Error registering and validating ticker symbols. HTTP status: "
                        + response.getStatusCode());
            }
            return response.getBody();

        } catch (RestClientException e) {
            throw new TickerSymbolsNotValidatedException("Error communicating with the stocks service: "
                    + e.getMessage());
        }
    }


}
