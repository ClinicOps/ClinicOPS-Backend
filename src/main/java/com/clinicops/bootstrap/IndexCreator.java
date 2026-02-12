package com.clinicops.bootstrap;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class IndexCreator {

    @EventListener(ApplicationReadyEvent.class)
    public void createIndexes() {
        // Mongo indexes later
    }
}
