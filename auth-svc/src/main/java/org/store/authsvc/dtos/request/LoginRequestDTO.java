package org.store.authsvc.dtos.request;

import com.eduplatform.common.utils.annotations.Uppercase;
import com.eduplatform.common.utils.validation.CommonValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.store.authsvc.utils.validation.ValidAccount;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    @Uppercase
    @ValidAccount(fieldName = "Tên đăng nhập hoặc Email")
    private String account;

    @CommonValid(fieldName = "Mật khẩu", notNull = true, max = 50)
    private String password;

    @CommonValid(fieldName = "Mã thiết bị", max = 36)
    private String deviceId;

    public LoginRequestDTO(String account, String password) {
        this.account = account;
        this.password = password;
    }
}
