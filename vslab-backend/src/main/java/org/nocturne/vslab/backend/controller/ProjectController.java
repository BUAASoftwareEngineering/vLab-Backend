package org.nocturne.vslab.backend.controller;

import org.nocturne.vslab.backend.bean.Project;
import org.nocturne.vslab.backend.bean.ImageType;
import org.nocturne.vslab.backend.manager.DockerManager;
import org.nocturne.vslab.backend.bean.Result;
import org.nocturne.vslab.backend.exceptiion.project.ProjectCreateLimitException;
import org.nocturne.vslab.backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.nocturne.vslab.backend.config.StringConst.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/project")
public class ProjectController {

    private static final Integer PROJECT_LIMIT = 3;

    private final DockerManager dockerManager;
    private final ProjectService projectService;


    @Autowired
    public ProjectController(DockerManager dockerManager,
                             ProjectService projectService) {
        this.dockerManager = dockerManager;
        this.projectService = projectService;
    }

    @GetMapping("/info")
    public Result getAllProjectInfo(@CookieValue(value = PARAM_USER_ID) Integer userId) {
        List<Project> projectList = projectService.getProjectsOfUser(userId);
        return new Result(0, "查询成功", projectList);
    }

    @PostMapping("/new")
    public Result createProject(@CookieValue(value = PARAM_USER_ID) Integer userId,
                                @RequestParam(PARAM_PROJECT_NAME) String projectName,
                                @RequestParam(PARAM_PROJECT_TYPE) String projectType) {
        ImageType imageType = ImageType.valueOf(projectType);
        checkAmountOfContainer(userId, imageType);

        Project project = dockerManager.createContainer(userId, projectName, imageType);
        return new Result(0, "创建成功", project);
    }

    private void checkAmountOfContainer(Integer userId, ImageType imageType) {
        List<Project> projectList = projectService.getProjectsOfUser(userId);
        if (projectList.stream().map(Project::getImageType).filter(x -> x.equals(imageType)).count() >= PROJECT_LIMIT) {
            throw new ProjectCreateLimitException();
        }
    }

    @PostMapping("/info_update")
    public Result updateProjectInfo(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                                    @RequestParam(PARAM_PROJECT_NAME) String projectName,
                                    @CookieValue(PARAM_USER_ID) Integer userId) {
        Project project = projectService.getProjectById(projectId, userId);

        project.setName(projectName);
        projectService.updateProject(project);

        return new Result(0, "更新成功", project);
    }

    @PostMapping("/enter")
    public Result enterProject(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                               @CookieValue(COOKIE_USER_ID) Integer userId) {
        dockerManager.startContainer(projectId);

        Project project = projectService.getProjectById(projectId, userId);
        return new Result(0, "启动成功", project);
    }

    @PostMapping("/exit")
    public Result exitProject(@RequestParam(PARAM_PROJECT_ID) Integer projectId) {
         dockerManager.stopContainer(projectId);

         return new Result(0, "关闭成功", null);
    }

    @PostMapping("/delete")
    public Result destroyProject(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                                 @CookieValue(value = PARAM_USER_ID) Integer userId) {
        dockerManager.destroyContainer(userId, projectId);

        return new Result(0, "删除成功", null);
    }
}