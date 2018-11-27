package com.dl.spring.cache.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dl.spring.cache.po.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:service_config.xml","classpath:cache.xml"})
public class UserServiceTest {
    @Autowired
    private UserService userService;
    
    @Autowired
    private CacheManager cacheManager;
    

    private Cache userCache;
    
    private Cache usersCache;

    @Before
    public void setUp() {
        userCache = cacheManager.getCache("user");
        userCache.clear();
        usersCache = cacheManager.getCache("users");
        usersCache.clear();
    }
    
    @Test
    public void testCache() {
        User user = new User(1L, "zhang", "zhang@gmail.com");
        userService.save(user);
        user = new User(2L, "zhang2", "zhang2@gmail.com");
        userService.save(user);
        System.out.println(userService.getAllUsers());
        
        
        user = new User(2L, "zhang22", "zhang22@gmail.com");
        userService.update(user);
        user = new User(3L, "zhang3", "zhang3@gmail.com");
        userService.save(user);
        System.out.println(userService.getAllUsers());
        
        
        userService.delete(new User(3L, "zhang3", "zhang3@gmail.com"));
        System.out.println(userService.getAllUsers());


    }
}
