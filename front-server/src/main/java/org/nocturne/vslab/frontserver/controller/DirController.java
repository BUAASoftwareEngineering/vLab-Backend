package org.nocturne.vslab.frontserver.controller;

import org.nocturne.vslab.api.entity.Container;
import org.nocturne.vslab.frontserver.mapper.ContainerMapper;
import org.nocturne.vslab.frontserver.util.HttpSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.nocturne.vslab.frontserver.config.StringConst.*;

@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/dir")
public class DirController {

    private ContainerMapper containerMapper;

    @Autowired
    public DirController(ContainerMapper containerMapper) {
        this.containerMapper = containerMapper;
    }

    @PostMapping("/new")
    public String createDir(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                            @RequestParam(PARAM_DIR_PATH) String dirPath) throws IOException {
        Container container = containerMapper.getContainerById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_DIR_PATH, dirPath);

        return new HttpSender(container.getIp(), container.getServerPort(), "/dir/new", params).post();
    }

    @PostMapping("/delete")
    public String deleteDir(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                            @RequestParam(PARAM_DIR_PATH) String dirPath) throws IOException {
        Container container = containerMapper.getContainerById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_DIR_PATH, dirPath);

        return new HttpSender(container.getIp(), container.getServerPort(), "/dir/delete", params).post();
    }
}
