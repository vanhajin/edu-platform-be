package org.store.authsvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"org.store.authsvc", "com.eduplatform.common.entites"})
@EnableJpaRepositories(basePackages = {"org.store.authsvc", "com.eduplatform.common.repositories"})
public class AuthSvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthSvcApplication.class, args);
    }

}
