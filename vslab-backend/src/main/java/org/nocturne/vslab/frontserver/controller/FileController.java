package org.nocturne.vslab.frontserver.controller;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.nocturne.vslab.frontserver.bean.Container;
import org.nocturne.vslab.frontserver.mapper.ContainerMapper;
import org.nocturne.vslab.frontserver.util.HttpSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

        return new HttpSender(container.getIp(), container.getServerPort(), "/file/struct", params).getForString();
    }

    @GetMapping("/content")
    public String getFileContent(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                                 @RequestParam(PARAM_FILE_PATH) String filePath) throws IOException, URISyntaxException {
        Container container = containerMapper.getContainerById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_FILE_PATH, filePath);

        return new HttpSender(container.getIp(), container.getServerPort(), "/file/content", params).getForString();
    }

    @GetMapping("/download")
    public String downLoadCode(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                               HttpServletResponse res) throws IOException, URISyntaxException {
        Container container = containerMapper.getContainerById(projectId);

        Map<String, String> params = new HashMap<>();
        HttpResponse received = new HttpSender(container.getIp(), container.getServerPort(), "/file/download", params).getForResponse();

        buildResHeads(received, res);
        writeContent(received.getEntity().getContent(), res.getOutputStream());

        return "success";
    }

    private void buildResHeads(HttpResponse received, HttpServletResponse toSend) {
        for (Header header : received.getAllHeaders()) {
            toSend.setHeader(header.getName(), header.getValue());
        }
    }

    private void writeContent(InputStream inputStream, OutputStream outputStream) throws IOException {
        IOUtils.copy(inputStream, outputStream);
        inputStream.close();
        outputStream.close();
    }

    @PostMapping("/update")
    public String updateFileContent(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                                    @RequestParam(PARAM_FILE_PATH) String filePath,
                                    @RequestParam(PARAM_FILE_CONTENT) String fileContent) throws IOException {
        Container container = containerMapper.getContainerById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_FILE_PATH, filePath);
        params.put(PARAM_FILE_CONTENT, fileContent);

        return new HttpSender(container.getIp(), container.getServerPort(), "/file/update", params).postForString();
    }

    @PostMapping("/new")
    public String createFile(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                             @RequestParam(PARAM_FILE_PATH) String filePath) throws IOException {
        Container container = containerMapper.getContainerById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_FILE_PATH, filePath);

        return new HttpSender(container.getIp(), container.getServerPort(), "/file/new", params).postForString();
    }

    @PostMapping("/delete")
    public String deleteFile(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                             @RequestParam(PARAM_FILE_PATH) String filePath) throws IOException {
        Container container = containerMapper.getContainerById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_FILE_PATH, filePath);

        return new HttpSender(container.getIp(), container.getServerPort(), "/file/delete", params).postForString();
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

        return new HttpSender(container.getIp(), container.getServerPort(), "/file/move", params).postForString();
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

        return new HttpSender(container.getIp(), container.getServerPort(), "/file/copy", params).postForString();
    }

    @PostMapping("/rename")
    public String renameFile(@RequestParam(PARAM_PROJECT_ID) Integer projectId,
                             @RequestParam(PARAM_FILE_OLD_PATH) String oldPath,
                             @RequestParam(PARAM_FILE_NEW_PATH) String newPath) throws IOException {
        Container container = containerMapper.getContainerById(projectId);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_FILE_OLD_PATH, oldPath);
        params.put(PARAM_FILE_NEW_PATH, newPath);

        return new HttpSender(container.getIp(), container.getServerPort(), "/file/rename", params).postForString();
    }
}
