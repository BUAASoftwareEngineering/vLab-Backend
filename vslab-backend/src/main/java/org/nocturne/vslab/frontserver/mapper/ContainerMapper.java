package org.nocturne.vslab.frontserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.nocturne.vslab.frontserver.bean.Container;

import java.util.List;

@Mapper
public interface ContainerMapper {

    public List<Container> getContainersOfUser(Integer userId);

    public void updateContainerName(@Param("projectId") Integer projectId, @Param("projectName") String projectName);

    public Container getContainerById(Integer projectId);

    public void createContainer(Container container);

    public void deleteContainer(Integer projectId);

    public void updateContainer(Container container);
}
