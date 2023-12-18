package org.capitalcompass.capitalcompassusers.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.capitalcompass.capitalcompassusers.dto.ValidateTickerRequestDTO;
import org.capitalcompass.capitalcompassusers.exception.TickerSymbolNotValidatedException;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Log4j2
public class StocksServiceClient {

    @LoadBalanced
    private final RestTemplate restTemplate;

    private final String STOCKS_SERVICE_URL = "http://STOCKS/v1/stocks/reference/tickers";

    public Boolean validateTickerSymbol(String tickerSymbol) throws TickerSymbolNotValidatedException {
        ValidateTickerRequestDTO request = ValidateTickerRequestDTO.builder()
                .symbol(tickerSymbol)
                .build();
        ResponseEntity<Boolean> response = restTemplate.postForEntity(STOCKS_SERVICE_URL, request, Boolean.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            throw new TickerSymbolNotValidatedException("Ticker symbol could not be validated : " + tickerSymbol);
        }

        return response.getBody();
    }


}
