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
public class UserService2Test {
    @Autowired
    private UserService2 userService;
    @Autowired
    private CacheManager cacheManager;

    private Cache userCache;
    

    @Before
    public void setUp() {
        userCache = cacheManager.getCache("user");
    	userService.deleteAll();
    }
    
    @Test
    public void testCache() {

    	
        User user = new User(1L, "zhang", "zhang@gmail.com");
        userService.save(user);
        
        System.out.println(userCache.get("id=1").get());
        System.out.println(userCache.get("name=zhang").get());
        System.out.println(userCache.get("mail=zhang@gmail.com").get());
        
        System.out.println(userService.findById(1L));
        System.out.println(userService.findByUsername("zhang"));
        System.out.println(userService.findByEmail("zhang@gmail.com"));
        
        System.out.println("--");
        
        user = new User(1L,"zhang2","zhang2@gmail.com");
        userService.update(user);
        
        System.out.println(userCache.get("id=1").get());
        System.out.println(userCache.get("name=zhang").get());
        System.out.println(userCache.get("mail=zhang@gmail.com").get());
        System.out.println(userCache.get("name=zhang2").get());
        System.out.println(userCache.get("mail=zhang2@gmail.com").get());

        System.out.println(userService.findById(1L));
        System.out.println(userService.findByUsername("zhang"));
        System.out.println(userService.findByEmail("zhang@gmail.com"));
        System.out.println(userService.findByUsername("zhang2"));
        System.out.println(userService.findByEmail("zhang2@gmail.com"));
        
        System.out.println("--");
        
        user = new User(1L,"zhang2","zhang2@gmail.com");
        userService.delete(user);
        
        System.out.println(userCache.get("name=zhang").get());
        System.out.println(userCache.get("mail=zhang@gmail.com").get());
        
        System.out.println(userService.findById(1L));
        System.out.println(userService.findByUsername("zhang"));
        System.out.println(userService.findByEmail("zhang@gmail.com"));
        System.out.println(userService.findByUsername("zhang2"));
        System.out.println(userService.findByEmail("zhang2@gmail.com"));
    }
}
