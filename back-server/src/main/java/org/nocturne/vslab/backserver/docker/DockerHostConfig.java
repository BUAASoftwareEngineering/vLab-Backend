package org.nocturne.vslab.backserver.docker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DockerHostConfig {
    private static List<String> dockerHostIPList;

    static {
        dockerHostIPList = new ArrayList<>();
        dockerHostIPList.add("120.53.37.31");
    }

    public static String getIPRandomly() {
        Random random = new Random();
        return dockerHostIPList.get(random.nextInt(dockerHostIPList.size()));
    }
}
