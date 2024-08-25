package org.capitalcompass.userservice.service;

import lombok.RequiredArgsConstructor;
import org.capitalcompass.userservice.api.KeycloakTokenResponse;
import org.capitalcompass.userservice.api.KeycloakUser;
import org.capitalcompass.userservice.client.KeycloakClient;
import org.capitalcompass.userservice.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final KeycloakClient keycloakClient;

    /**
     * Retrieves a list of all users from Keycloak.
     * This method first obtains an access token and then uses it to retrieve user information.
     *
     * @return A List of UserDTO containing details of all users.
     */
    public List<UserDTO> getUsers() {
        KeycloakTokenResponse token = keycloakClient.getAccessToken();
        List<KeycloakUser> users = keycloakClient.getUsers(token.getAccessToken());
        return users.stream().map(this::buildUserDTO).collect(Collectors.toList());
    }

    /**
     * Builds the UserDTO from the given KeycloakUser.
     * Converts the user information obtained from Keycloak into a UserDTO.
     *
     * @param user The KeycloakUser object containing user information.
     * @return A UserDTO containing the user's details.
     */
    private UserDTO buildUserDTO(KeycloakUser user) {
        return UserDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .enabled(user.getEnabled())
                .build();
    }
}