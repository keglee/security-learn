package com.iversonx.security_learn.repository;

import com.iversonx.security_learn.entity.DcUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 *
 **/
@SpringBootTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    
    @Test
    public void test() {
        Iterable<DcUser> list = userRepository.findAll();
    }
}
