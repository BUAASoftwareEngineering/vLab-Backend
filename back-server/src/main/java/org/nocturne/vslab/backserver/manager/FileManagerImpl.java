package org.nocturne.vslab.backserver.manager;

import org.apache.dubbo.config.annotation.Service;
import org.nocturne.vslab.api.manager.FileManager;
import org.springframework.stereotype.Component;

@Service(interfaceName = "fileManager")
@Component
public class FileManagerImpl implements FileManager {

    @Override
    public Boolean createFile(Integer integer, String s) {
        return null;
    }

    @Override
    public Boolean deleteFile(Integer integer, String s) {
        return null;
    }

    @Override
    public String getFileContent(Integer integer, String s) {
        return null;
    }

    @Override
    public Boolean writeFileCover(Integer integer, String s, String s1) {
        return null;
    }

    @Override
    public Boolean writeFileAppend(Integer integer, String s, String s1) {
        return null;
    }

    @Override
    public Boolean moveFile(Integer integer, String s, String s1) {
        return null;
    }
}
