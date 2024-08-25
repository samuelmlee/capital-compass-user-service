package org.capitalcompass.userservice.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakUser {

    private Object createdTimestamp;
    private String username;
    private Boolean enabled;
    private String email;
}
