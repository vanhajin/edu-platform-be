package org.store.authsvc.repositories;

import org.junit.jupiter.api.Test;
import org.store.authsvc.dtos.persistence.IUserLoginDTO;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest {

    @Test
    void findsUserInfoProjectionByUsernameOrEmail() throws NoSuchMethodException {
        Method method = UserRepository.class.getMethod(
                "findUserInfoByUsernameOrEmail", String.class
        );

        assertThat(method.getReturnType()).isEqualTo(Optional.class);
        assertThat(method.getGenericReturnType().getTypeName()).contains(IUserLoginDTO.class.getName());
    }
}
