package org.capitalcompass.capitalcompassusers.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class AwsPropertiesLogger implements ApplicationListener<ContextRefreshedEvent> {

    private final Environment env;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String datasourceUsername = env.getProperty("spring.datasource.username");

        log.info("spring.datasource.username: {}", datasourceUsername);
    }
}
