package com.dl.mybatis.service;

import com.dl.mybatis.po.Blog;

public interface BlogService {
	Blog selectBlog(long id);
	
	void insert();
	
	void test();
}
