package org.nocturne.vslab.frontserver.controller;

import org.apache.dubbo.config.annotation.Reference;
import org.nocturne.vslab.api.entity.Container;
import org.nocturne.vslab.api.entity.ImageType;
import org.nocturne.vslab.api.manager.DockerManager;
import org.nocturne.vslab.frontserver.bean.Result;
import org.nocturne.vslab.frontserver.mapper.ContainerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.nocturne.vslab.frontserver.config.StringConst.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Reference(interfaceName = "dockerManager")
    private DockerManager dockerManager;

    private ContainerMapper containerMapper;

    @Autowired
    public ProjectController(ContainerMapper containerMapper) {
        this.containerMapper = containerMapper;
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
        Container container = dockerManager.createContainer(userId, projectName, imageType);
        return new Result(0, "创建成功", container);
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