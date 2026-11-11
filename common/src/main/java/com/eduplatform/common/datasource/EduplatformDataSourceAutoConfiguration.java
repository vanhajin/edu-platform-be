package com.eduplatform.common.datasource;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(before = DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(EduplatformDataSourceProperties.class)
@ConditionalOnProperty(prefix = "eduplatform.datasource", name = "url")
public class EduplatformDataSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    DataSource eduplatformDataSource(EduplatformDataSourceProperties properties) {
        return DataSourceBuilder.create()
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .build();
    }
}
