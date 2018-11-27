package com.dl.spring.cache.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.dl.spring.cache.po.User;

@Service
public class UserService {
    Set<User> users = new HashSet<User>();

    @CachePut(value = "user", key = "''+#user.id", condition="T(com.dl.spring.cache.service.CheckUtils).checkPut('users','all','id',#user)")
    public User save(User user) {
        users.add(user);
        return user;
    }

    @CachePut(value = "user", key = "''+#user.id", condition="T(com.dl.spring.cache.service.CheckUtils).checkPut('users','all','id',#user)")
    public User update(User user) {
        users.remove(user);
        users.add(user);
        return user;
    }
    
    @CacheEvict(value = "user", key = "''+#user.id", condition="T(com.dl.spring.cache.service.CheckUtils).checkEvict('users','all','id',#user)")
    public User delete(User user) {
        users.remove(user);
        return user;
    }

    @CacheEvict(value = "user", allEntries = true)
    public void deleteAll() {
        users.clear();
    }

    @Cacheable(value = "user", key = "''+#id")
    public User findById(final Long id) {
        System.out.println("cache miss, invoke find by id, id:" + id);
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }
    
    
    @Cacheable(value = "users", key = "'all'")
    public Set<User> getAllUsers(){
    	System.out.println("cache miss");
    	return users;
    }
}
