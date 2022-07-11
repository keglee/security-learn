package com.iverson.learn.security.mapper;

import com.iverson.learn.security.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 *
 **/
@Mapper
public interface UserMapper {
    User loadByUsername(String username);
}
