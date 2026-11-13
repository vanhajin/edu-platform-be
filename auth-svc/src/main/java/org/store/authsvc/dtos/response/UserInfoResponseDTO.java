package org.store.authsvc.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDTO implements IUserInfoDTO {

    private String userId;
    private String username;
    private String displayName;
    private String email;
    private String role;
}
