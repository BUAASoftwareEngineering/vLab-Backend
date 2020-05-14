package org.nocturne.vslab.backend.controller;

import org.nocturne.vslab.backend.bean.Container;
import org.nocturne.vslab.backend.bean.ImageType;
import org.nocturne.vslab.backend.manager.DockerManager;
import org.nocturne.vslab.backend.bean.Result;
import org.nocturne.vslab.backend.exceptiion.project.ProjectCreateLimitException;
import org.nocturne.vslab.backend.mapper.ContainerMapper;
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

    private final ContainerMapper containerMapper;

    @Autowired
    public ProjectController(ContainerMapper containerMapper, DockerManager dockerManager) {
        this.containerMapper = containerMapper;
        this.dockerManager = dockerManager;
    }

    @GetMapping("/info")
    public Result getAllProjectInfo(@CookieValue(value = PARAM_USER_ID) Integer userId) {
        List<Container> containerList = containerMapper.getContainersOfUser(userId);
        return new Result(0, "查询成功", containerList);
    }

    @PostMapping("/new")
    public Result createProject(@CookieValue(value = PARAM_USER_ID) Integer userId,
                                @RequestParam(PARAM_PROJECT_NAME) String projectName,
                                @RequestParam(PARAM_PROJECT_TYPE) String projectType) {
        ImageType imageType = ImageType.valueOf(projectType);
        checkAmountOfContainer(userId, imageType);

        Container container = dockerManager.createContainer(userId, projectName, imageType);
        return new Result(0, "创建成功", container);
    }

    private void checkAmountOfContainer(Integer userId, ImageType imageType) {
        List<Container> containerList = containerMapper.getContainersOfUser(userId);
        if (containerList.stream().map(Container::getImageType).filter(x -> x.equals(imageType)).count() >= PROJECT_LIMIT) {
            throw new ProjectCreateLimitException();
        }
    }

    @PostMapping("/info_update")
    public Result updateProjectInfo(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                                    @RequestParam(PARAM_PROJECT_NAME) String projectName) {
        containerMapper.updateContainerName(projectId, projectName);

        Container container = containerMapper.getContainerById(projectId);
        return new Result(0, "更新成功", container);
    }

    @PostMapping("/enter")
    public Result enterProject(@RequestParam(PARAM_PROJECT_ID) Integer projectId) {
        dockerManager.startContainer(projectId);

        Container container = containerMapper.getContainerById(projectId);
        return new Result(0, "启动成功", container);
    }

    @PostMapping("/exit")
    public Result exitProject(@RequestParam(PARAM_PROJECT_ID) Integer projectId) {
         dockerManager.stopContainer(projectId);

         return new Result(0, "关闭成功", null);
    }

    @PostMapping("/delete")
    public Result destroyProject(@RequestParam(PARAM_PROJECT_ID) Integer projectId) {
        dockerManager.destroyContainer(projectId);

        return new Result(0, "删除成功", null);
    }
}