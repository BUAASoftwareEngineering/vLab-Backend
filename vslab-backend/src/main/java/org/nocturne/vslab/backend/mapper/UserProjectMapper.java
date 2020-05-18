package org.nocturne.vslab.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserProjectMapper {

    public void bindProjectToUser(@Param("userId") Integer userId, @Param("projectId") Integer projectId, @Param("writeable") Boolean writeable);

    public void unbindProjectFromUser(@Param("userId") Integer userId, @Param("projectId") Integer projectId);

    public List<Integer> getHoldersOfProjects(Integer projectId);
}
