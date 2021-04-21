package com.codetalk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "document")
public class DocumentConfig {
    /**
     * Number of lines in new document
     */
    private int initialLinesNumber = 30;

    /**
     * Max document time to live before cleanup
     */
    private Duration cleanupAge = Duration.ofHours(24);

    public int getInitialLinesNumber() {
        return initialLinesNumber;
    }

    public void setInitialLinesNumber(int initialLinesNumber) {
        this.initialLinesNumber = initialLinesNumber;
    }

    public Duration getCleanupAge() {
        return cleanupAge;
    }

    public void setCleanupAge(Duration cleanupAge) {
        this.cleanupAge = cleanupAge;
    }
}
