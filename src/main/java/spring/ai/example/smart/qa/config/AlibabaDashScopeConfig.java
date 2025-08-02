package spring.ai.example.smart.qa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlibabaDashScopeConfig {
//    @Value("${dashscope.api-key}")
    private String apiKey;

//    @Bean
//    public DashScopeClient dashScopeClient() {
//        return new DashScopeClient(apiKey);
//    }
}
