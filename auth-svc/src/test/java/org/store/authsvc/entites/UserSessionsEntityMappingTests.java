package org.store.authsvc.entites;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class UserSessionsEntityMappingTests {

    @Test
    void mapsUserSessionsTableAndGeneratedHexPrimaryKey() throws NoSuchFieldException {
        assertThat(UserSessionsEntity.class.getAnnotation(Entity.class)).isNotNull();
        assertThat(UserSessionsEntity.class.getAnnotation(Table.class).name()).isEqualTo("user_sessions");

        Field id = UserSessionsEntity.class.getDeclaredField("userSessionId");
        assertThat(id.getAnnotation(Id.class)).isNotNull();
        assertThat(id.getAnnotation(UuidGenerator.class)).isNotNull();
    }
}
