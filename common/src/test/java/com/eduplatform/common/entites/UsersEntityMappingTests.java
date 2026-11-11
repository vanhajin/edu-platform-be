package com.eduplatform.common.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class UsersEntityMappingTests {

    @Test
    void mapsUsersTablePrimaryKeyAndUniqueBusinessColumns() throws NoSuchFieldException {
        assertThat(UsersEntity.class.getAnnotation(Entity.class)).isNotNull();
        assertThat(UsersEntity.class.getAnnotation(Table.class).name()).isEqualTo("users");

        Field userId = UsersEntity.class.getDeclaredField("userId");
        assertThat(userId.getAnnotation(Id.class)).isNotNull();
        assertThat(userId.getAnnotation(Column.class).length()).isEqualTo(40);

        Field username = UsersEntity.class.getDeclaredField("username");
        Field email = UsersEntity.class.getDeclaredField("email");
        assertThat(username.getAnnotation(Column.class).unique()).isTrue();
        assertThat(email.getAnnotation(Column.class).unique()).isTrue();
    }
}
