package org.nocturne.vslab.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("org.nocturne.vslab.frontserver.mapper")
@SpringBootApplication
public class VslabBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(VslabBackendApplication.class, args);
    }

}
