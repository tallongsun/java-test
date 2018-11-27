package com.dl.spring.cache.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.dl.spring.cache.po.User;

@Service
public class UserService2 {

    Set<User> users = new HashSet<User>();

    @Caching(
            put = {
                    @CachePut(value = "user", key = "'id='+#user.id"),
                    @CachePut(value = "user", key = "'name='+#user.username"),
                    @CachePut(value = "user", key = "'mail='+#user.email")
            }
    )
    public User save(User user) {
        users.add(user);
        return user;
    }

    @Caching(
            put = {
                    @CachePut(value = "user", key = "'id='+#user.id"),
                    @CachePut(value = "user", key = "'name='+#user.username"),
                    @CachePut(value = "user", key = "'mail='+#user.email")
            }
    )
    public User update(User user) {
        users.remove(user);
        users.add(user);
        return user;
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "user", key = "'id='+#user.id"),
                    @CacheEvict(value = "user", key = "'name='+#user.username"),
                    @CacheEvict(value = "user", key = "'mail='+#user.email")
            }
    )
    public User delete(User user) {
        users.remove(user);
        return user;
    }

    @CacheEvict(value = "user", allEntries = true)
    public void deleteAll() {
        users.clear();
    }

    @Cacheable(value = "user", key = "'id='+#id")
    public User findById(final Long id) {
        System.out.println("cache miss, invoke find by id, id:" + id);
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }


    @Cacheable(value = "user", key = "'name='+#username")
    public User findByUsername(final String username) {
        System.out.println("cache miss, invoke find by username, username:" + username);
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Cacheable(value = "user", key = "'mail='+#email")
    public User findByEmail(final String email) {
        System.out.println("cache miss, invoke find by email, email:" + email);
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }


}
