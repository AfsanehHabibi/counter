package ir.nofitech.counter.service;

import ir.nofitech.counter.config.PropertiesConfig;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class PropertyServiceTest {

    private PropertyService propertyService;
    private PropertiesConfiguration fileConfig;
    private PropertiesConfig serviceConfig;


    @BeforeEach
    void setUp(@TempDir Path tempDir) throws Exception {
        File propertiesFile = tempDir.resolve("test.properties").toFile();
        fileConfig = new PropertiesConfiguration(propertiesFile);
        fileConfig.setProperty("existingKey", "testValue");
        fileConfig.save();
        serviceConfig = new PropertiesConfig();
        serviceConfig.setFilePath(propertiesFile.getAbsolutePath());
        serviceConfig.setCacheDuration(Duration.ofSeconds(30));
        propertyService = new PropertyService(serviceConfig, fileConfig);
    }

    @Test
    void whenPropertyExistGetValueReturnsIt() {
        assertEquals("testValue", propertyService.getValue("existingKey"));
    }

    @Test
    void whenPropertyDoesNotExistGetValueReturnsNull() {
        assertNull(propertyService.getValue("nonExistingKey"));
    }

    @Test
    void whenAddingNewPropertyItsValueWillBeAdded() throws Exception {
        propertyService.setValue("newKey", "newValue");
        assertEquals("newValue", propertyService.getValue("newKey"));

        assertEquals("newValue", propertyService.getValue("newKey"));
    }

    @Test
    void whenAccessTimeIsShorterThanCashReloadReturnsCashValue() throws Exception {
        propertyService.getValue("existingKey");

        fileConfig.setProperty("existingKey", "updatedValue");
        fileConfig.save();

        assertEquals("testValue", propertyService.getValue("existingKey"));
    }

    @Test
    void whenAccessTimeIsLongerThanCashReloadReturnsUpdatedValue() throws Exception {
        serviceConfig.setCacheDuration(Duration.ofSeconds(0));
        propertyService = new PropertyService(serviceConfig, fileConfig);
        propertyService.getValue("existingKey");

        fileConfig.setProperty("existingKey", "updatedValue");
        fileConfig.save();

        assertEquals("updatedValue", propertyService.getValue("existingKey"));
    }

    @Test
    void whenMultipleThreadAccessTogetherNoErrorHappens() throws InterruptedException {
        final int threadCount = 10;
        final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final ConcurrentHashMap<String, String> results = new ConcurrentHashMap<>();

        for (int i = 0; i < threadCount; i++) {
            final String key = "key" + i;
            executor.execute(() -> {
                propertyService.setValue(key, "value" + Thread.currentThread().getId());
                results.put(key, propertyService.getValue(key));
                latch.countDown();
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(threadCount, results.size());
        results.forEach((k, v) -> assertTrue(v.startsWith("value")));
    }
}