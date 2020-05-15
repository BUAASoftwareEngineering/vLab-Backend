package org.nocturne.vslab.backend.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project implements Serializable {
    /**
     * 数据库中的id字段，唯一标识container
     */
    private Integer projectId;

    /**
     * docker内的containerId属性，不同的docker下可能重复
     */
    private String containerId;

    private ImageType imageType;

    private String name;

    private String ip;

    private Integer serverPort;
    private Integer terminalPort;
    private Integer languagePort;
}