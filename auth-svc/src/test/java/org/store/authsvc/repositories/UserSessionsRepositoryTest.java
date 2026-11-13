package org.store.authsvc.repositories;

import org.junit.jupiter.api.Test;
import org.store.authsvc.entites.UserSessionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import static org.assertj.core.api.Assertions.assertThat;

class UserSessionsRepositoryTest {

    @Test
    void isARepositoryForUserSessions() {
        assertThat(UserSessionsRepository.class.getAnnotation(Repository.class)).isNotNull();
        assertThat(JpaRepository.class.isAssignableFrom(UserSessionsRepository.class)).isTrue();
        assertThat(UserSessionsRepository.class.getGenericInterfaces()[0].getTypeName())
                .contains(UserSessionsEntity.class.getName());
    }
}
