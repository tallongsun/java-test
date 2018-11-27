package com.dl.mybatis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dl.mybatis.dao.UserMapperSqlSessionImpl;
import com.dl.mybatis.po.User;

@Service
@Transactional
public class FooServiceImpl implements FooService {
	@Autowired
	private UserMapperSqlSessionImpl userMapperSqlSessionImpl;
	
	public void setUserMapper(UserMapperSqlSessionImpl userMapperSqlSessionImpl) {
		  this.userMapperSqlSessionImpl = userMapperSqlSessionImpl;
	}
	
	@Override
	public User doSomeBusinessStuff(String userId) {
		return userMapperSqlSessionImpl.getUser(userId);
	}

}
