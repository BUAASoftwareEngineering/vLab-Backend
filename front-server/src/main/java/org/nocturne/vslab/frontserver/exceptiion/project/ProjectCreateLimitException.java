package org.nocturne.vslab.frontserver.exceptiion.project;

/**
 * 当用户尝试创建超过限制的容器个数时，会抛出该异常
 */
public class ProjectCreateLimitException extends RuntimeException {
    public static final int CODE = -103;
}
