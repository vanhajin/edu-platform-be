package org.store.authsvc.dtos.request;

import com.eduplatform.common.utils.enums.EnumRole;
import com.eduplatform.common.utils.validation.CommonValid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @CommonValid(fieldName = "Tên đăng nhập", notNull = true, regex = "^[A-Za-z0-9_]+$", max = 50)
    private String username;

    @CommonValid(fieldName = "Địa chỉ Email", notNull = true, max = 100)
    @Email(message = "Email must have a valid format")
    private String email;

    @CommonValid(fieldName = "Mật khẩu", notNull = true, max = 50)
    private String password;

    @CommonValid(fieldName = "Tên hiển thị", notNull = true, max = 200)
    private String displayName;

    private String role = EnumRole.STUDENT.getCode();

    @CommonValid(fieldName = "Năm sinh", max = 4, regex = "^(?:\\d{1,4})?$")
    private String yearOfBirth;

    @CommonValid(fieldName = "Tên hiển thị", max = 200)
    private String homeTown;
}
