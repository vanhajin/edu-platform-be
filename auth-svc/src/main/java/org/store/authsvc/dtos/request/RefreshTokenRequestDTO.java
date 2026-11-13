package org.store.authsvc.dtos.request;

import com.eduplatform.common.utils.validation.CommonValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequestDTO {

    @CommonValid(fieldName = "Refresh token", notNull = true, max = 500)
    private String refreshToken;
}
