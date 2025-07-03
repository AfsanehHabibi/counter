package ir.nofitech.counter.service;

import ir.nofitech.counter.config.PropertiesConfig;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class PropertyService {
    private final PropertiesConfig config;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private volatile long lastReadTime = 0;
    private PropertiesConfiguration properties;

    public PropertyService(PropertiesConfig config, PropertiesConfiguration properties) {
        this.config = config;
        this.properties = properties;
        this.properties.setAutoSave(true);
        loadProperties();
    }

    public String getValue(String key) {
        if (System.currentTimeMillis() - lastReadTime > config.getCacheDuration().toMillis()) {
            loadProperties();
        }

        lock.readLock().lock();
        try {
            return properties.getString(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setValue(String key, String value) {
        lock.writeLock().lock();
        try {
            properties.setProperty(key, value);
            properties.save(new File(config.getFilePath()));
            lastReadTime = System.currentTimeMillis();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save properties to " + config.getFilePath(), e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void loadProperties() {
        lock.writeLock().lock();
        try {
            properties = new PropertiesConfiguration(config.getFilePath());
            properties.setAutoSave(true);
            lastReadTime = System.currentTimeMillis();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load properties from " + config.getFilePath(), e);
        } finally {
            lock.writeLock().unlock();
        }
    }
}