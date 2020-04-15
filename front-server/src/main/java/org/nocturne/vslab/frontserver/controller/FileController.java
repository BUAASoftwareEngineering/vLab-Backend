package org.nocturne.vslab.frontserver.controller;

import org.nocturne.vslab.api.entity.Container;
import org.nocturne.vslab.frontserver.mapper.ContainerMapper;
import org.nocturne.vslab.frontserver.util.HttpSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.nocturne.vslab.frontserver.config.StringConst.*;

@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/file")
public class FileController {

    private ContainerMapper containerMapper;

    @Autowired
    public FileController(ContainerMapper containerMapper) {
        this.containerMapper = containerMapper;
    }

    @GetMapping("/struct")
    public String getFileStruct(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                                @RequestParam(PARAM_FILE_ROOT_PATH) String rootPath) throws IOException, URISyntaxException {
        Container container = containerMapper.getContainerById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_FILE_ROOT_PATH, rootPath);

        return new HttpSender(container.getIp(), container.getServerPort(), "/file/struct", params).get();
    }

    @GetMapping("/content")
    public String getFileContent(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                                 @RequestParam(PARAM_FILE_PATH) String filePath) throws IOException, URISyntaxException {
        Container container = containerMapper.getContainerById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_FILE_PATH, filePath);

        return new HttpSender(container.getIp(), container.getServerPort(), "/file/content", params).get();
    }

    @PostMapping("/update")
    public String updateFileContent(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                                    @RequestParam(PARAM_FILE_PATH) String filePath,
                                    @RequestParam(PARAM_FILE_CONTENT) String fileContent) throws IOException {
        Container container = containerMapper.getContainerById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_FILE_PATH, filePath);
        params.put(PARAM_FILE_CONTENT, fileContent);

        return new HttpSender(container.getIp(), container.getServerPort(), "/file/update", params).post();
    }

    @PostMapping("/new")
    public String createFile(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                             @RequestParam(PARAM_FILE_PATH) String filePath) throws IOException {
        Container container = containerMapper.getContainerById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_FILE_PATH, filePath);

        return new HttpSender(container.getIp(), container.getServerPort(), "/file/new", params).post();
    }

    @PostMapping("/delete")
    public String deleteFile(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                             @RequestParam(PARAM_FILE_PATH) String filePath) throws IOException {
        Container container = containerMapper.getContainerById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_FILE_PATH, filePath);

        return new HttpSender(container.getIp(), container.getServerPort(), "/file/delete", params).post();
    }

    @PostMapping("/move")
    public String moveFile(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                           @RequestParam(PARAM_FILE_OLD_PATH) String oldPath,
                           @RequestParam(PARAM_FILE_NEW_PATH) String newPath,
                           @RequestParam(value = "force", defaultValue = "false") Boolean force) throws IOException {
        Container container = containerMapper.getContainerById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_FILE_OLD_PATH, oldPath);
        params.put(PARAM_FILE_NEW_PATH, newPath);
        params.put("force", force.toString());

        return new HttpSender(container.getIp(), container.getServerPort(), "/file/move", params).post();
    }

    @PostMapping("/copy")
    public String copyFile(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                           @RequestParam(PARAM_FILE_OLD_PATH) String oldPath,
                           @RequestParam(PARAM_FILE_NEW_PATH) String newPath,
                           @RequestParam(value = "force", defaultValue = "false") Boolean force) throws IOException {
        Container container = containerMapper.getContainerById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_FILE_OLD_PATH, oldPath);
        params.put(PARAM_FILE_NEW_PATH, newPath);
        params.put("force", force.toString());

        return new HttpSender(container.getIp(), container.getServerPort(), "/file/copy", params).post();
    }

    @PostMapping("/rename")
    public String renameFile(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                             @RequestParam(PARAM_FILE_OLD_PATH) String oldPath,
                             @RequestParam(PARAM_FILE_NEW_PATH) String newPath) throws IOException {
        Container container = containerMapper.getContainerById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_FILE_OLD_PATH, oldPath);
        params.put(PARAM_FILE_NEW_PATH, newPath);

        return new HttpSender(container.getIp(), container.getServerPort(), "/file/rename", params).post();
    }
}
