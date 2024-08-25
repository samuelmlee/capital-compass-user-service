package org.capitalcompass.capitalcompassusers.service;

import org.capitalcompass.userservice.api.KeycloakTokenResponse;
import org.capitalcompass.userservice.api.KeycloakUser;
import org.capitalcompass.userservice.client.KeycloakClient;
import org.capitalcompass.userservice.dto.UserDTO;
import org.capitalcompass.userservice.exception.KeycloakClientErrorException;
import org.capitalcompass.userservice.service.AdminUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminUserServiceTest {

    @Mock
    private KeycloakClient keycloakClient;

    @InjectMocks
    private AdminUserService adminUserService;

    @Test
    public void getUsersOK() {
        KeycloakTokenResponse tokenResponse = KeycloakTokenResponse.builder()
                .accessToken("access-token")
                .build();


        KeycloakUser user1 = KeycloakUser.builder()
                .username("user1")
                .email("user1@example.com")
                .enabled(true)
                .build();

        KeycloakUser user2 = KeycloakUser.builder()
                .username("user2")
                .email("user2@example.com")
                .enabled(false)
                .build();


        List<KeycloakUser> keycloakUsers = List.of(user1, user2);

        when(keycloakClient.getAccessToken()).thenReturn(tokenResponse);
        when(keycloakClient.getUsers(tokenResponse.getAccessToken())).thenReturn(keycloakUsers);

        List<UserDTO> results = adminUserService.getUsers();

        assertEquals(results.size(), 2);

        assertEquals(results.get(0).getUsername(), "user1");
        assertEquals(results.get(0).getEmail(), "user1@example.com");

        assertEquals(results.get(1).getUsername(), "user2");
        assertEquals(results.get(1).getEmail(), "user2@example.com");
    }

    @Test
    public void getUsersEmptyListOK() {
        KeycloakTokenResponse tokenResponse = KeycloakTokenResponse.builder()
                .accessToken("access-token")
                .build();

        List<KeycloakUser> emptyList = Collections.emptyList();

        when(keycloakClient.getAccessToken()).thenReturn(tokenResponse);
        when(keycloakClient.getUsers(tokenResponse.getAccessToken())).thenReturn(emptyList);

        List<UserDTO> results = adminUserService.getUsers();

        assertEquals(results.size(), 0);
    }

    @Test
    public void getUsersTokenError() {
        when(keycloakClient.getAccessToken()).thenThrow(new KeycloakClientErrorException("Fetch Token Error"));

        Exception exception = assertThrows(KeycloakClientErrorException.class, () -> {
            adminUserService.getUsers();
        });

        assertEquals(exception.getMessage(), "Fetch Token Error");
    }

    @Test
    public void getUsersClientError() {
        KeycloakTokenResponse tokenResponse = KeycloakTokenResponse.builder()
                .accessToken("access-token")
                .build();

        when(keycloakClient.getAccessToken()).thenReturn(tokenResponse);
        when(keycloakClient.getUsers(tokenResponse.getAccessToken())).thenThrow(new KeycloakClientErrorException("Fetch Users Error"));

        Exception exception = assertThrows(KeycloakClientErrorException.class, () -> {
            adminUserService.getUsers();
        });

        assertEquals(exception.getMessage(), "Fetch Users Error");

    }
}
