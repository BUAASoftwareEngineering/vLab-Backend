package org.nocturne.vslab.backend.mapper;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.nocturne.vslab.backend.bean.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProjectMapperTest {

    @Autowired
    public ProjectMapper projectMapper;

    @Test
    public void getContainersOfUser() {
        List<Project> list = projectMapper.getProjectsOfUser(57);
        assertFalse(list.isEmpty());
    }

    @Test
    public void getContainerById() {
        Project project = projectMapper.getProjectById(314);
        assertNotNull(project);
    }

    @Test
    public void updateContainerName() {
        Project project = projectMapper.getProjectById(314);
        assertNotEquals("new", project.getName());
        projectMapper.updateProjectName(314, "new");

        project = projectMapper.getProjectById(314);
        assertEquals("new", project.getName());
    }
}