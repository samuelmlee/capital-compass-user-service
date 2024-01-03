package org.capitalcompass.userservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.capitalcompass.userservice.dto.ValidateTickerRequestDTO;
import org.capitalcompass.userservice.exception.TickerSymbolsNotValidatedException;
import org.springframework.beans.factory.annotation.Value;
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

    private final RestTemplate restTemplate;

    @Value("${stock.service.uri}")
    private String stockServiceUri;

    public Set<String> registerTickers(Set<String> tickerSymbols) {
        String STOCKS_SERVICE_URL =  stockServiceUri + "/v1/stocks/reference/tickers";

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
