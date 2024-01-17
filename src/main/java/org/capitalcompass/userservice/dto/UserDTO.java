package org.capitalcompass.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String username;
    private Boolean enabled;
    private String email;
}
