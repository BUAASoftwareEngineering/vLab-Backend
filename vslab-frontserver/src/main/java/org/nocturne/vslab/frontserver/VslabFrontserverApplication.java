package org.nocturne.vslab.frontserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("org.nocturne.vslab.frontserver.mapper")
@SpringBootApplication
public class VslabFrontserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(VslabFrontserverApplication.class, args);
    }

}
