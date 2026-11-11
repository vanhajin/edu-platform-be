package org.store.authsvc;

import com.eduplatform.common.utils.ExceptionMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
class BackgroundErrorConfiguration implements AsyncConfigurer {
    private static final Logger log = LoggerFactory.getLogger(BackgroundErrorConfiguration.class);

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (exception, method, parameters) -> log.error(ExceptionMessageUtil.getDetail("auth-svc", exception));
    }
}
