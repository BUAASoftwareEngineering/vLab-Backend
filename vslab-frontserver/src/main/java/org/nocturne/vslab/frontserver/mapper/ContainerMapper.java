package org.nocturne.vslab.frontserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.nocturne.vslab.api.entity.Container;

import java.util.List;

@Mapper
public interface ContainerMapper {

    public List<Container> getContainersOfUser(Integer userId);

    public Container getContainerById(Integer id);
}
