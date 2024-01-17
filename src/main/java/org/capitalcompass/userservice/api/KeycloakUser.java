package org.capitalcompass.userservice.api;

import lombok.Data;

@Data
public class KeycloakUser {

    private Object createdTimestamp;
    private String username;
    private Boolean enabled;
    private String email;
}
