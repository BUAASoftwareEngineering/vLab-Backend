package org.nocturne.vslab.api.manager;

import org.nocturne.vslab.api.entity.File;

public interface DirManager {

    /**
     * 获取文件树
     *
     * @param containerId 数据库中container的Id
     * @param rootPath    文件树的根路径
     */
    public File getFileTree(Integer containerId, String rootPath);

    /**
     * 创建文件夹
     * @param containerId 数据库中container的Id
     * @param dirPath 文件夹路径
     * @return 创建成功时，返回true，否则，返回false
     */
    public Boolean createDir(Integer containerId, String dirPath);

    /**
     * 删除文件夹，当文件夹非空时，不执行操作
     *
     * @param containerId 数据库中container的Id
     * @param dirPath 文件夹路径
     * @return 删除成功时，返回true，否则，返回false
     */
    public Boolean deleteDir(Integer containerId, String dirPath);

    /**
     * 删除文件夹
     * 当force为true时，无论文件夹是否为空，都会删除
     * 当force为false时，只会删除空文件夹
     *
     * @param containerId 数据库中container的Id
     * @param dirPath 文件夹路径
     * @param force 是否强制删除
     * @return 删除成功时，返回true，否则，返回false
     */
    public Boolean deleteDir(Integer containerId, String dirPath, Boolean force);

    /**
     * 移动文件夹，即使文件夹非空
     *
     * @param containerId 数据库中container的Id
     * @param oldPath     文件夹原位置
     * @param newPath     文件夹目标位置
     * @return 移动成功时，返回true，否则，返回false
     */
    public Boolean moveDir(Integer containerId, String oldPath, String newPath);
}
