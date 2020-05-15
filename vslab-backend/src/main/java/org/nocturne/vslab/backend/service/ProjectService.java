package org.nocturne.vslab.backend.service;

import org.nocturne.vslab.backend.bean.Project;
import org.nocturne.vslab.backend.exceptiion.project.ProjectNotFoundException;
import org.nocturne.vslab.backend.mapper.ProjectMapper;
import org.nocturne.vslab.backend.mapper.UserProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final UserProjectMapper userProjectMapper;

    @Autowired
    public ProjectService(ProjectMapper projectMapper,
                          UserProjectMapper userProjectMapper) {
        this.projectMapper = projectMapper;
        this.userProjectMapper = userProjectMapper;
    }

    public List<Project> getProjectsOfUser(Integer userId) {
        return projectMapper.getProjectsOfUser(userId);
    }

    public Project getProjectById(Integer projectId) {
        Project project = projectMapper.getProjectById(projectId);

        if (project == null) {
            throw new ProjectNotFoundException();
        }
        return project;
    }

    public void createProject(Integer userId, Project project) {
        projectMapper.createProject(project);
        userProjectMapper.bindProjectToUser(userId, project.getProjectId());
    }

    public void updateProject(Project project) {
        projectMapper.updateProject(project);
    }

    public void bindProjectToUser(Integer userId, Project project) {
        bindProjectToUser(userId, project.getProjectId());
    }

    public void bindProjectToUser(Integer userId, Integer projectId) {
        userProjectMapper.bindProjectToUser(userId, projectId);
    }

    public void unbindProjectFromUser(Integer userId, Project project) {
        unbindProjectFromUser(userId, project.getProjectId());
    }

    public void unbindProjectFromUser(Integer userId, Integer projectId) {
        userProjectMapper.unbindProjectFromUser(userId, projectId);

        List<Integer> holders = userProjectMapper.getHoldersOfProjects(projectId);
        if (holders.isEmpty()) projectMapper.deleteProject(projectId);
    }


}
