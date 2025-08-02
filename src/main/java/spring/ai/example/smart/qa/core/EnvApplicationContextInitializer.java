package spring.ai.example.smart.qa.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EnvApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final Logger logger = LoggerFactory.getLogger(EnvApplicationContextInitializer.class);

    @Override
    public void initialize(@org.jetbrains.annotations.NotNull ConfigurableApplicationContext applicationContext) {
        try {
            Resource resource = new ClassPathResource(".env");
            if (resource.exists()) {
                Map<String, Object> envMap = new HashMap<>();
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                properties.forEach((key, value) -> {
                    // 只有当环境变量不存在时才设置
                    if (System.getenv(key.toString()) == null && System.getProperty(key.toString()) == null) {
                        System.setProperty(key.toString(), value.toString());
                        // 同时添加到 Spring 环境中
                        envMap.put(key.toString(), value);
                        logger.info("Loaded property: {} from .env file", key);
                    }
                });
                applicationContext.getEnvironment().getPropertySources().addFirst(new MapPropertySource("api-env", envMap));
            } else {
                logger.warn(".env file not found in classpath");
            }
        } catch (IOException e) {
            logger.error("Error loading .env file", e);
        }
    }
}
