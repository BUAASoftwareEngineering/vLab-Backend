package org.nocturne.vslab.frontserver.controller;

import com.alibaba.fastjson.JSON;
import org.apache.dubbo.config.annotation.Reference;
import org.nocturne.vslab.api.entity.Container;
import org.nocturne.vslab.api.entity.ImageType;
import org.nocturne.vslab.api.manager.DockerManager;
import org.nocturne.vslab.frontserver.bean.Result;
import org.nocturne.vslab.frontserver.mapper.ContainerMapper;
import org.nocturne.vslab.frontserver.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {

    @Reference
    private DockerManager dockerManager;

    private ContainerMapper containerMapper;

    @Autowired
    public ProjectController(ContainerMapper containerMapper) {
        this.containerMapper = containerMapper;
    }

    @GetMapping("/info")
    public Result getAllProjectInfo(@RequestParam("user_id") Integer userId) {
        try {
            List<Container> containerList = containerMapper.getContainersOfUser(userId);
            return new Result(true, "查询成功", containerList);
        } catch (Exception e) {
            return new Result(false, "查询失败", e.getMessage());
        }
    }

    @PostMapping("/new")
    public Result createProject(@RequestParam("user_id") Integer userId,
                                @RequestParam("project_name") String projectName,
                                @RequestParam("project_type") String projectType) {
        try {
            ImageType imageType = ImageType.valueOf(projectType);
            Container container = dockerManager.createContainer(userId, projectName, imageType);
            return new Result(true, "创建成功", container);
        } catch (Exception e) {
            return new Result(false, "创建失败", e.getMessage());
        }
    }

    @PostMapping("/enter")
    public Result enterProject(@RequestParam("project_id") Integer projectId) {
        try {
            Boolean success = dockerManager.startContainer(projectId);
            if (success) {
                return new Result(true, "启动成功", null);
            } else {
                return new Result(false, "启动失败", null);
            }
        } catch (Exception e) {
            return new Result(false, "启动失败", e.getMessage());
        }
    }

    @PostMapping("/exit")
    public Result exitProject(@RequestParam("project_id") Integer projectId) {
        try {
            Boolean success = dockerManager.stopContainer(projectId);
            if (success) {
                return new Result(true, "关闭成功", null);
            } else {
                return new Result(false, "关闭失败", null);
            }
        } catch (Exception e) {
            return new Result(false, "关闭失败", e.getMessage());
        }
    }

    @PostMapping("/delete")
    public Result destroyProject(@RequestParam("project_id") Integer projectId) {
        try {
            Boolean success = dockerManager.destroyContainer(projectId);
            if (success) {
                return new Result(true, "删除成功", null);
            } else {
                return new Result(false, "删除失败", null);
            }
        } catch (Exception e) {
            return new Result(false, "删除失败", e.getMessage());
        }
    }
}
