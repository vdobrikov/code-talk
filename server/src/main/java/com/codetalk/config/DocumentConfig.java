package com.codetalk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "document")
public class DocumentConfig {
    /**
     * Number of lines in new document
     */
    private int initialLinesNumber = 30;

    public int getInitialLinesNumber() {
        return initialLinesNumber;
    }

    public void setInitialLinesNumber(int initialLinesNumber) {
        this.initialLinesNumber = initialLinesNumber;
    }
}
