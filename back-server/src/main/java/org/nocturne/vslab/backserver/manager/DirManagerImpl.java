package org.nocturne.vslab.backserver.manager;

import org.apache.dubbo.config.annotation.Service;
import org.nocturne.vslab.api.entity.File;
import org.nocturne.vslab.api.manager.DirManager;
import org.springframework.stereotype.Component;

@Service(interfaceName = "dirManager")
@Component
public class DirManagerImpl implements DirManager {

    @Override
    public File getFileTree(Integer integer, String s) {
        return null;
    }

    @Override
    public Boolean createDir(Integer integer, String s) {
        return false;
    }

    @Override
    public Boolean deleteDir(Integer integer, String s) {
        return null;
    }

    @Override
    public Boolean deleteDir(Integer integer, String s, Boolean aBoolean) {
        return null;
    }

    @Override
    public Boolean moveDir(Integer integer, String s, String s1) {
        return null;
    }
}
