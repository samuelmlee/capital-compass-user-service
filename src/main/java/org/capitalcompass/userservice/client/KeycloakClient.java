package org.capitalcompass.userservice.client;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.userservice.api.KeycloakTokenResponse;
import org.capitalcompass.userservice.api.KeycloakUser;
import org.capitalcompass.userservice.exception.KeycloakClientErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KeycloakClient {

    private final RestTemplate restTemplate;

    @Value("${keycloak-admin-client.id}")
    private String keycloakClientId;
    @Value("${keycloak.realm}")
    private String keycloakRealm;
    @Value("${keycloak.base-url}")
    private String keycloakBaseUrl;
    @Value("${keycloak.admin-client.secret}")
    private String keycloakClientSecret;

    public KeycloakTokenResponse getAccessToken() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", keycloakClientId);
        map.add("client_secret", keycloakClientSecret);
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);


        try {
            ResponseEntity<KeycloakTokenResponse> response =
                    restTemplate.exchange(keycloakBaseUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/token",
                            HttpMethod.POST,
                            entity,
                            KeycloakTokenResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new KeycloakClientErrorException("Error occurred getting access token. HTTP status: " + response.getStatusCode());
            }
            return response.getBody();
        } catch (RestClientException e) {
            throw new KeycloakClientErrorException("Error communicating with the Keycloak server getting the access token : " + e.getMessage());
        }
    }

    public List<KeycloakUser> getUsers(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        try {
            ResponseEntity<KeycloakUser[]> response = restTemplate.exchange(
                    keycloakBaseUrl + "/admin/realms/" + keycloakRealm + "/users",
                    HttpMethod.GET,
                    entity,
                    KeycloakUser[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new KeycloakClientErrorException("KeycloakClientErrorException occurred getting all users. HTTP Status : " + response.getStatusCode());
            }
            return List.of(response.getBody());
        } catch (RestClientException e) {
            throw new KeycloakClientErrorException("A network error occurred getting all Users from Keycloak : " + e.getMessage());
        }


    }


}
