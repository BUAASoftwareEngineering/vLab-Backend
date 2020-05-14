package org.nocturne.vslab.backend.util;

import org.nocturne.vslab.backend.manager.DockerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ContainerLiveKeeper {

    private static final long LIVE_THRESHOLD = 60 * 60;

    private final DockerManager dockerManager;
    private final Map<Integer, Long> lastActiveTime = new ConcurrentHashMap<>();

    @Lazy
    @Autowired
    public ContainerLiveKeeper(DockerManager dockerManager) {
        this.dockerManager = dockerManager;
    }


    public void cleanContainer() {
        Long currentTime = System.currentTimeMillis();

        lastActiveTime.keySet().stream()
                .filter(x -> (currentTime - lastActiveTime.get(x) > LIVE_THRESHOLD))
                .forEach(dockerManager::stopContainer);
    }

    public void refreshActiveTime(int projectId) {
        lastActiveTime.put(projectId, System.currentTimeMillis());
        System.out.println(String.format("updated keeper[%d] : %d", projectId, lastActiveTime.get(projectId)));
    }

    public void removeKeep(int projectId) {
        lastActiveTime.remove(projectId);
    }
}
