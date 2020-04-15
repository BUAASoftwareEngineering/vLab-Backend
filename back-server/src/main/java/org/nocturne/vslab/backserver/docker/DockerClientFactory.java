package org.nocturne.vslab.backserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import org.springframework.stereotype.Component;

@Component
public class DockerClientFactory {

    private static DefaultDockerClientConfig.Builder configBuilder;

    static {
        configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerTlsVerify(true)
                .withDockerCertPath("./certs")
                .withDockerConfig("/home/user/.docker")
                .withApiVersion("1.30"); // optional
    }

    public static synchronized DockerClient getDockerClient(String ip) {
        DefaultDockerClientConfig config = configBuilder.withDockerHost(String.format("tcp://%s:2376", ip)).build();
        return DockerClientBuilder.getInstance(config).build();
    }
}
