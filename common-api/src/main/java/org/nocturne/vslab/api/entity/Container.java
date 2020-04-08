package org.nocturne.vslab.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Container {
    private ImageType imageType;

    /**
     * 数据库中的id字段，唯一标识container
     */
    private Integer projectId;

    /**
     * docker内的containerId属性，不同的docker下可能重复
     */
    private String containerId;

    private String name;

    private String ip;

    private Integer serverPort;
    private Integer terminalPort;
}
