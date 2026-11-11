package org.store.authsvc.mappers;

import com.eduplatform.common.entites.UsersEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.store.authsvc.dtos.request.RegistorRequestDTO;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void mapsRegistrationDataWithoutTheRawPassword() {
        RegistorRequestDTO request = new RegistorRequestDTO(
                "VanNT_Buyer", "nguyenthivanhs98@gmail.com", "A123456", "A123456",
                "Nguyễn Thị Vân", "STUDENT", "1998", "Hải Dương"
        );

        UsersEntity user = userMapper.toEntity(request);

        assertThat(user.getUsername()).isEqualTo(request.getUsername());
        assertThat(user.getEmail()).isEqualTo(request.getEmail());
        assertThat(user.getDisplayName()).isEqualTo(request.getDisplayName());
        assertThat(user.getRole()).hasToString("STUDENT");
        assertThat(user.getYearOfBirth()).isEqualTo(request.getYearOfBirth());
        assertThat(user.getHomeTown()).isEqualTo(request.getHomeTown());
        assertThat(user.getPassword()).isNull();
    }
}
