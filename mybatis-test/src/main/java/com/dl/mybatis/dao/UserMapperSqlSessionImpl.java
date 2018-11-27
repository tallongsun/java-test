package com.dl.mybatis.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dl.mybatis.po.User;

@Repository
public class UserMapperSqlSessionImpl implements UserMapper {
	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	public User getUser(String userId) {
	    // note standard MyBatis API usage - opening and closing the session manually
	    SqlSession session = sqlSessionFactory.openSession();

	    try {
	      return (User) session.selectOne("com.dl.mybatis.dao.UserMapper.getUser", userId);
	    } finally {
	      session.close();
	    }
	}

}
