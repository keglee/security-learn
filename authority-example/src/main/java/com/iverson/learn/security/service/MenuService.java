package com.iverson.learn.security.service;

import com.iverson.learn.security.entity.Menu;
import com.iverson.learn.security.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 **/
@Service
public class MenuService {
    @Autowired
    private MenuMapper menuMapper;
    public List<Menu> listAll() {
        return menuMapper.listAll();
    }
}
