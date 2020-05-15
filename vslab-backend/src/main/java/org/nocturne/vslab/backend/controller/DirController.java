package org.nocturne.vslab.backend.controller;

import org.nocturne.vslab.backend.bean.Project;
import org.nocturne.vslab.backend.mapper.ProjectMapper;
import org.nocturne.vslab.backend.service.ProjectService;
import org.nocturne.vslab.backend.util.HttpSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.nocturne.vslab.backend.config.StringConst.*;

@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/dir")
public class DirController {

    private final ProjectService projectService;

    @Autowired
    public DirController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/new")
    public String createDir(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                            @RequestParam(PARAM_DIR_PATH) String dirPath) throws IOException {
        Project project = projectService.getProjectById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_DIR_PATH, dirPath);

        return new HttpSender(project.getIp(), project.getServerPort(), "/dir/new", params).postForString();
    }

    @PostMapping("/delete")
    public String deleteDir(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                            @RequestParam(PARAM_DIR_PATH) String dirPath) throws IOException {
        Project project = projectService.getProjectById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_DIR_PATH, dirPath);

        return new HttpSender(project.getIp(), project.getServerPort(), "/dir/delete", params).postForString();
    }

    @PostMapping("/move")
    public String moveDir(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                          @RequestParam(PARAM_DIR_OLD_PATH) String oldPath,
                          @RequestParam(PARAM_DIR_NEW_PATH) String newPath,
                          @RequestParam(value = "force", defaultValue = "false") Boolean force) throws IOException {
        Project project = projectService.getProjectById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_DIR_OLD_PATH, oldPath);
        params.put(PARAM_DIR_NEW_PATH, newPath);
        params.put("force", force.toString());

        return new HttpSender(project.getIp(), project.getServerPort(), "/dir/move", params).postForString();
    }

    @PostMapping("/copy")
    public String copyDir(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                          @RequestParam(PARAM_DIR_OLD_PATH) String oldPath,
                          @RequestParam(PARAM_DIR_NEW_PATH) String newPath,
                          @RequestParam(value = "force", defaultValue = "false") Boolean force) throws IOException {
        Project project = projectService.getProjectById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_DIR_OLD_PATH, oldPath);
        params.put(PARAM_DIR_NEW_PATH, newPath);
        params.put("force", force.toString());

        return new HttpSender(project.getIp(), project.getServerPort(), "/dir/copy", params).postForString();
    }

    @PostMapping("/rename")
    public String renameDir(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                          @RequestParam(PARAM_DIR_OLD_PATH) String oldPath,
                          @RequestParam(PARAM_DIR_NEW_PATH) String newPath) throws IOException {
        Project project = projectService.getProjectById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_DIR_OLD_PATH, oldPath);
        params.put(PARAM_DIR_NEW_PATH, newPath);

        return new HttpSender(project.getIp(), project.getServerPort(), "/dir/rename", params).postForString();
    }
}
