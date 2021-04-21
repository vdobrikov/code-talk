package com.codetalk.tasks;

import com.codetalk.config.DocumentConfig;
import com.codetalk.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CleanupOldDocumentsScheduledTask {
    private static final Logger LOG = LoggerFactory.getLogger(CleanupOldDocumentsScheduledTask.class);

    private final DocumentService documentService;
    private final DocumentConfig documentConfig;

    public CleanupOldDocumentsScheduledTask(DocumentService documentService, DocumentConfig documentConfig) {
        this.documentService = documentService;
        this.documentConfig = documentConfig;
    }

    @Scheduled(fixedRate = 60 * 60 * 1000, // 1 hour
            initialDelay = 10 * 60 * 1000) // 10 min
    public void run() {
        Instant cleanupDate = Instant.now().minus(documentConfig.getCleanupAge());
        LOG.info("Cleanup document older than cleanupDate={}", cleanupDate);

        var docsToDelete = documentService.findByCreatedDateBefore(cleanupDate)
                .doOnNext(doc -> LOG.debug("Deleting document={}", doc));
        documentService.deleteAll(docsToDelete).subscribe();

        LOG.info("Cleanup is done");
    }
}
