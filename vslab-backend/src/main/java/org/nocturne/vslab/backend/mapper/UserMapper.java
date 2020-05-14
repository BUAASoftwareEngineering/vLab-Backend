package org.nocturne.vslab.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.nocturne.vslab.backend.bean.User;

@Mapper
public interface UserMapper {

    public User getUserByName(String username);

    public User getUserById(Integer id);

    public void createUser(User user);

    public void deleteUserByName(String username);

    public void updateUser(User user);
}
