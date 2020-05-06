package org.nocturne.vslab.frontserver;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;
import org.nocturne.vslab.api.entity.ImageType;
import org.nocturne.vslab.api.entity.User;
import org.nocturne.vslab.api.manager.DirManager;
import org.nocturne.vslab.api.manager.DockerManager;
import org.nocturne.vslab.api.manager.FileManager;
import org.nocturne.vslab.frontserver.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;

@SpringBootTest
class VslabFrontserverApplicationTests {

}
