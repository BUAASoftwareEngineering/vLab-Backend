package org.nocturne.vslab.backend.controller;

import org.nocturne.vslab.backend.bean.Project;
import org.nocturne.vslab.backend.bean.Result;
import org.nocturne.vslab.backend.bean.User;
import org.nocturne.vslab.backend.exceptiion.project.ProjectNotFoundException;
import org.nocturne.vslab.backend.service.ProjectService;
import org.nocturne.vslab.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nocturne.vslab.backend.config.StringConst.*;

@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/share")
public class ShareController {

    private final StringRedisTemplate redisTemplate;
    private final ProjectService projectService;
    private final UserService userService;

    @Autowired
    public ShareController(StringRedisTemplate redisTemplate,
                           ProjectService projectService,
                           UserService userService) {
        this.redisTemplate = redisTemplate;
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping("/info")
    public Result getSharedProjectsInfo(@CookieValue(COOKIE_USER_NAME) String username) {
        Set<String> keys = redisTemplate.keys("share:" + username + ":*");
        if (keys == null) keys = new HashSet<>();

        List<Project> projectList = keys
                .stream()
                .map(ShareController::getProjectIdFromKey)
                .map(projectService::getProjectById)
                .collect(Collectors.toList());

        return new Result(0, "查询成功", projectList);
    }

    private static Integer getProjectIdFromKey(String key) {
        return Integer.parseInt(key.split(":")[2]);
    }

    @PostMapping("/invite")
    public Result inviteOthersToProject(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                                        @RequestParam(PARAM_USER_NAME) String invitee,
                                        @RequestParam(PARAM_PROJECT_WRITEABLE) Boolean writeable,
                                        @CookieValue(COOKIE_USER_NAME) String inviter) {
        checkShareAuthority(projectId, inviter);

        redisTemplate.opsForValue().set(getShareKey(invitee, projectId), writeable.toString());

        return new Result(0, "邀请完成", null);
    }

    private void checkShareAuthority(Integer projectId, String inviter) {
        User user = userService.getUserByName(inviter);
        Project project = projectService.getProjectById(projectId, user.getId());

        if (!project.getWriteable()) {
            throw new ProjectNotFoundException();
        }
    }

    @PostMapping("/accept")
    public Result acceptInvitation(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                                   @CookieValue(COOKIE_USER_NAME) String username,
                                   @CookieValue(COOKIE_USER_ID) Integer userId) {
        String writeableStr = redisTemplate.opsForValue().get(getShareKey(username, projectId));
        redisTemplate.delete(getShareKey(username, projectId));
        if (writeableStr == null) throw new ProjectNotFoundException();

        Boolean writeable = Boolean.valueOf(writeableStr);
        projectService.bindProjectToUser(userId, projectId, writeable);

        return new Result(0, "完成操作", null);
    }

    @PostMapping("/refuse")
    public Result refuseInvitation(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                                   @CookieValue(COOKIE_USER_NAME) String username) {
        redisTemplate.delete(getShareKey(username, projectId));

        return new Result(0, "完成操作", null);
    }

    private String getShareKey(String username, Integer projectId) {
        return "share:" + username + ":" + projectId.toString();
    }
}
