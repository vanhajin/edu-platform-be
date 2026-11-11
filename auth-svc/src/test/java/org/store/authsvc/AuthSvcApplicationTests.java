package org.store.authsvc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.assertj.core.api.Assertions.assertThat;

class AuthSvcApplicationTests {

    @Test
    void configuresAuthServicePortWithoutStartingDatabase() {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yaml"));

        assertThat(yaml.getObject()).containsEntry("server.port", 45900);
    }

    @Test
    void scansSharedEntitiesAndRepositories() {
        assertThat(AuthSvcApplication.class.getAnnotation(EntityScan.class).basePackages())
                .contains("com.eduplatform.common.entites");
        assertThat(AuthSvcApplication.class.getAnnotation(EnableJpaRepositories.class).basePackages())
                .contains("com.eduplatform.common.repositories");
    }

}
