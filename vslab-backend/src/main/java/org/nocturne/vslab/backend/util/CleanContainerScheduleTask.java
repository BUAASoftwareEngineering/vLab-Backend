package org.nocturne.vslab.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class CleanContainerScheduleTask {

    private final ContainerLiveKeeper keeper;

    @Autowired
    public CleanContainerScheduleTask(ContainerLiveKeeper keeper) {
        this.keeper = keeper;
    }

    /**
     * 每天半夜四点清一清
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void cleanContainer() {
        keeper.cleanContainer();
    }
}
