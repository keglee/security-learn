package com.iverson.learn.security.mapper;

import com.iverson.learn.security.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 *
 **/
@Mapper
public interface UserMapper {
    @Select("SELECT * FROM t_user WHERE username = #{username}")
    User loadByUsername(String username);
    
    @Update("UPDATE t_user SET password = #{password} WHERE id = #{id}")
    Integer updatePassword(@Param("id") Long id, @Param("password") String password);
}
