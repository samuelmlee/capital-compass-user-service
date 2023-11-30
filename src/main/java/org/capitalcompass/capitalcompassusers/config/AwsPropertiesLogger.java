package org.capitalcompass.capitalcompassusers.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AwsPropertiesLogger implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AwsPropertiesLogger.class);

    private final Environment env;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String datasourceUsername = env.getProperty("spring.datasource.username");

        logger.info("spring.datasource.username: {}", datasourceUsername);
    }
}
