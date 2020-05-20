package org.nocturne.vslab.backend.service;

import org.nocturne.vslab.backend.bean.Project;
import org.nocturne.vslab.backend.exceptiion.project.ProjectNotFoundException;
import org.nocturne.vslab.backend.manager.DockerManager;
import org.nocturne.vslab.backend.mapper.ProjectMapper;
import org.nocturne.vslab.backend.mapper.UserProjectMapper;
import org.nocturne.vslab.backend.util.CloudFileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final UserProjectMapper userProjectMapper;
    private final DockerManager dockerManager;

    @Lazy
    @Autowired
    public ProjectService(ProjectMapper projectMapper,
                          UserProjectMapper userProjectMapper,
                          DockerManager dockerManager) {
        this.projectMapper = projectMapper;
        this.userProjectMapper = userProjectMapper;
        this.dockerManager = dockerManager;
    }

    public List<Project> getProjectsOfUser(Integer userId) {
        return projectMapper.getProjectsOfUser(userId);
    }

    // no granted for writeable field
    // this method only offer project info without writeable info
    public Project getProjectById(Integer projectId) {
        Project project = projectMapper.getProjectById(projectId);

        if (project == null) {
            throw new ProjectNotFoundException();
        }
        return project;
    }

    // this method will offer project info with writeable info
    public Project getProjectById(Integer projectId, Integer userId) {
        List<Project> projects = projectMapper.getProjectsOfUser(userId);

        if (projects == null || projects.stream().noneMatch(x -> x.getProjectId().equals(projectId))) {
            throw new ProjectNotFoundException();
        }
        return projects.stream().filter(x -> x.getProjectId().equals(projectId)).collect(Collectors.toList()).get(0);
    }

    @Transactional
    public void createProject(Integer userId, Project project) {
        projectMapper.createProject(project);
        userProjectMapper.bindProjectToUser(userId, project.getProjectId(), Boolean.TRUE);
    }

    public void updateProject(Project project) {
        projectMapper.updateProject(project);
    }

    public void bindProjectToUser(Integer userId, Project project) {
        bindProjectToUser(userId, project.getProjectId());
    }

    public void bindProjectToUser(Integer userId, Integer projectId) {
        this.bindProjectToUser(userId, projectId, Boolean.FALSE);
    }

    public void bindProjectToUser(Integer userId, Integer projectId, Boolean writeable) {
        userProjectMapper.bindProjectToUser(userId, projectId, writeable);
    }

    public void unbindProjectFromUser(Integer userId, Project project) {
        unbindProjectFromUser(userId, project.getProjectId());
    }

    @Transactional
    public void unbindProjectFromUser(Integer userId, Integer projectId) {
        List<Integer> holders = userProjectMapper.getHoldersOfProjects(projectId);

        if (holders.size() == 1) {
            dockerManager.stopContainer(projectId);
            CloudFileHelper.deleteRemoteFile(projectId);

            userProjectMapper.unbindProjectFromUser(userId, projectId);
            projectMapper.deleteProject(projectId);
        } else {
            userProjectMapper.unbindProjectFromUser(userId, projectId);
        }
    }
}
