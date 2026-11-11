package com.eduplatform.common.datasource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EduplatformDataSourcePropertiesTests {

    @Test
    void bindsSharedDatasourceCredentialsWithoutChangingSpecialCharacters() {
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("test", Map.of(
                "eduplatform.datasource.url", "jdbc:mysql://db:3306/edu",
                "eduplatform.datasource.username", "edu_user",
                "eduplatform.datasource.password", "p@ss!$word"
        )));

        EduplatformDataSourceProperties properties = Binder.get(environment)
                .bind("eduplatform.datasource", Bindable.of(EduplatformDataSourceProperties.class))
                .orElseThrow(() -> new AssertionError("Datasource properties were not bound"));

        assertThat(properties.getUrl()).isEqualTo("jdbc:mysql://db:3306/edu");
        assertThat(properties.getUsername()).isEqualTo("edu_user");
        assertThat(properties.getPassword()).isEqualTo("p@ss!$word");
    }
}
