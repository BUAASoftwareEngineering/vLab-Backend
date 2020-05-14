package org.nocturne.vslab.backend.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.stereotype.Component;

@Component
public class DockerClientFactory {

    private static DefaultDockerClientConfig.Builder configBuilder;

    static {
        configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerTlsVerify(false)
                .withApiVersion("1.30");
    }

    public static synchronized DockerClient getDockerClient(String ip) {
        DefaultDockerClientConfig config = configBuilder.withDockerHost(String.format("tcp://%s:2376", ip)).build();
        return DockerClientBuilder.getInstance(config).build();
    }
}
