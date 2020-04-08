package org.nocturne.vslab.api.manager;

public interface FileManager {

    /**
     * 创建文件
     *
     * @param containerId 数据库中的container的Id
     * @param filePath    文件路径
     * @return 如果创建成功，返回<code>true</code>，否则，返回<code>false</code>
     */
    public Boolean createFile(Integer containerId, String filePath);

    /**
     * 删除文件
     *
     * @param containerId 数据库中的container的Id
     * @param filePath    文件路径
     * @return 如果删除成功，返回<code>true</code>，否则，返回<code>false</code>
     */
    public Boolean deleteFile(Integer containerId, String filePath);

    /**
     * 读取文件内容
     *
     * @param containerId 数据库中的container的Id
     * @param filePath    文件路径
     * @return the content of the target file
     */
    public String getFileContent(Integer containerId, String filePath);

    /**
     * 写入文件
     * 将采用覆盖写入方式
     *
     * @param containerId 数据库中的container的Id
     * @param filePath    文件路径
     * @param fileContent 文件内容
     * @return 如果写入成功，返回<code>true</code>，否则，返回<code>false</code>
     */
    public Boolean writeFileCover(Integer containerId, String filePath, String fileContent);

    /**
     * 写入文件
     * 将采用追加写入方式
     *
     * @param containerId   数据库中的container的Id
     * @param filePath      文件路径
     * @param appendContent 追加内容
     * @return 如果写入成功，返回<code>true</code>，否则，返回<code>false</code>
     */
    public Boolean writeFileAppend(Integer containerId, String filePath, String appendContent);

    /**
     * 移动文件或为文件重命名
     *
     * @param containerId 数据库中container的Id
     * @param oldPath     文件原位置
     * @param newPath     文件目标位置
     * @return 如果移动成功，返回<code>true</code>，否则，返回<code>false</code>
     */
    public Boolean moveFile(Integer containerId, String oldPath, String newPath);


}
