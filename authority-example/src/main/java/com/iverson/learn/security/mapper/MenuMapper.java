package com.iverson.learn.security.mapper;

import com.iverson.learn.security.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 **/
@Mapper
public interface MenuMapper {
    List<Menu> listAll();
}
