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
                //.withDockerTlsVerify(true)
                //.withDockerCertPath("/home/user/.docker/certs")
                .withDockerConfig("/home/user/.docker")
                //.withApiVersion("1.30") // optional
                .withRegistryUrl("https://hub.docker.com/")
                .withRegistryUsername("knowden")
                .withRegistryPassword("Drugs2bb2.love")
                .withRegistryEmail("853172766@qq.com");
    }

    public static synchronized DockerClient getDockerClient(String ip) {
        DefaultDockerClientConfig config = configBuilder.withDockerHost(String.format("tcp://%s:2375", ip)).build();
        return DockerClientBuilder.getInstance(config).build();
    }
}