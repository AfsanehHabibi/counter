package ir.nofitech.counter.config;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public PropertiesConfiguration propertiesConfiguration(PropertiesConfig propertiesConfig) {
        try {
            PropertiesConfiguration config = new PropertiesConfiguration(propertiesConfig.getFilePath());
            config.setAutoSave(true);
            return config;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load properties from " + propertiesConfig.getFilePath(), e);
        }
    }
}