package org.nocturne.vslab.backserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("org.nocturne.vslab.backserver.mapper")
@SpringBootApplication
public class BackServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackServerApplication.class, args);
    }

}
