package ir.nofitech.counter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.properties")
public class PropertiesConfig {
    private String filePath = "D:\\1.properties";

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration cacheDuration = Duration.ofMinutes(1);
}