package org.capitalcompass.capitalcompassusers.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.capitalcompass.capitalcompassusers.dto.ValidateTickerRequestDTO;
import org.capitalcompass.capitalcompassusers.exception.TickerSymbolNotValidatedException;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Log4j2
public class StocksServiceClient {

    @LoadBalanced
    private final RestTemplate restTemplate;

    private final String STOCKS_SERVICE_URL = "http://STOCKS/v1/stocks/reference/tickers";

    public Set<String> validateBatchTickers(Set<String> tickerSymbols) throws TickerSymbolNotValidatedException {

        ValidateTickerRequestDTO request = ValidateTickerRequestDTO.builder()
                .symbols(tickerSymbols)
                .build();
        HttpEntity<ValidateTickerRequestDTO> entity = new HttpEntity<>(request);

        ResponseEntity<Set<String>> response = restTemplate.exchange(
                STOCKS_SERVICE_URL, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }


}
