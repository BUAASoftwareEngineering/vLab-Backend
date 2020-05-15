package org.nocturne.vslab.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.nocturne.vslab.backend.bean.Project;

import java.util.List;

@Mapper
public interface ProjectMapper {

    public List<Project> getProjectsOfUser(Integer userId);

    public void updateProjectName(@Param("projectId") Integer projectId, @Param("projectName") String projectName);

    public Project getProjectById(Integer projectId);

    public void createProject(Project project);

    public void deleteProject(Integer projectId);

    public void updateProject(Project project);
}
