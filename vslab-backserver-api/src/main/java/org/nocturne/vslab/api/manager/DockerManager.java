package org.nocturne.vslab.api.manager;

import org.nocturne.vslab.api.entity.Container;
import org.nocturne.vslab.api.entity.ImageType;

public interface DockerManager {

    public Container createContainer(String username, String containerName, ImageType imageType);

    /**
     * 根据Id销毁container
     * @param id 数据库中的Id，注意不是docker上下文中的containerId
     * @return 当销毁成功后返回<code>true</code>，否则返回<code>false</code>
     */
    public Boolean destroyContainer(Integer id);

    /**
     * 根据Id开启相应的container
     * @param id 数据库中的Id，注意不是docker上下文中的containerId
     * @return 当开启成功后返回<code>true</code>，否则返回<code>false</code>
     */
    public Boolean startContainer(Integer id);

    /**
     * 根据Id关闭相应的container
     * @param id 数据库中的Id，注意不是docker上下文中的containerId
     * @return 当关闭成功后返回<code>true</code>，否则返回<code>false</code>
     */
    public Boolean stopContainer(Integer id);
}
