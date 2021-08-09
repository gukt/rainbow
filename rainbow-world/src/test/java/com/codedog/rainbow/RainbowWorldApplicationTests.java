package com.codedog.rainbow;

import com.codedog.rainbow.domain.Role;
import com.codedog.rainbow.domain.User;
import com.codedog.rainbow.repository.RoleRepository;
import com.codedog.rainbow.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@SpringBootTest
class RainbowWorldApplicationTests {

    @Autowired
    public RoleRepository roleRepository;
    @Autowired
    public UserRepository userRepository;

    @Test
    void contextLoads() {
    }


    @Test
    @Transactional
    public void test1() {
        Role role = roleRepository.getById(1L);
        role.setCreatedAt(new Date());
        role.setId(null);
        role = roleRepository.save(role);
        System.out.println(role);

        User user = userRepository.getById(1L);
        user.setCreatedAt(new Date());
        user = userRepository.save(user);
        System.out.println(user);
    }

    @Test
    @Transactional
    public void test2() {
        Role role = roleRepository.getById(1L);
        List<Role> roles = roleRepository.findAll();
        Role role2 = roleRepository.getById(2L);
    }

    @Test
    @Transactional
    void test3() {
        // System.out.println(cacheManager.getCacheNames());
    }
}
