package com.eduplatform.common.repositories;

import com.eduplatform.common.entites.UsersEntity;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTests {

    @Test
    void extendsJpaRepositoryForUsersEntityWithStringIdentifier() {
        ResolvableType repositoryType = ResolvableType.forClass(UserRepository.class)
                .as(JpaRepository.class);

        assertThat(repositoryType.getGeneric(0).resolve()).isEqualTo(UsersEntity.class);
        assertThat(repositoryType.getGeneric(1).resolve()).isEqualTo(String.class);
    }
}
