package org.nocturne.vslab.backserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.nocturne.vslab.api.entity.Container;

@Mapper
public interface ContainerMapper {

    public Container getContainerById(Integer projectId);

    public void createContainer(Container container);

    public void deleteContainer(Integer projectId);
}
