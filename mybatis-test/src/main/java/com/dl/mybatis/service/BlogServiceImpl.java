package com.dl.mybatis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dl.mybatis.dao.BlogMapper;
import com.dl.mybatis.po.Blog;

@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class BlogServiceImpl implements BlogService{
	@Autowired
	private BlogMapper blogDao;
	
	@Override
	public Blog selectBlog(long id) {
		return blogDao.selectByPrimaryKey(id);
	}
	
	public void insert(){
		List<Blog> list = new ArrayList<>();
		for(int i=1;i<100;i++){
			Blog blog = new Blog();
			blog.setId((long)i+1);
			blog.setTitle("xxxxxxx");
			list.add(blog);
		}
//		long start = System.currentTimeMillis();
//		List<Blog> blogs = blogDao.getAll();
//		long end = System.currentTimeMillis();
//		System.out.println(end-start);
//		for(Blog blog : blogs){
//			System.out.println(blog.getTitle()+","+blog.getId());
//			break;
//		}
//		System.out.println(blogs.size());
		blogDao.insertList(list);
	}

	@Override
	public void test() {
//		Blog blog = new Blog();
//		blog.setTitle("test");
//		blogDao.insertBlog2(blog);
//		System.out.println(blogDao.selectBlog(blog.getId()));
//		System.out.println(blogDao.selectBlogs2(100));
//		
//		blog.setTitle("test2");
//		blogDao.updateBlog(blog);
//		System.out.println(blogDao.selectBlog(blog.getId()));
//		
//		blogDao.deleteBlog(blog);
//		System.out.println(blogDao.selectBlog(blog.getId()));
		
		Map params = new HashMap();
		params.put("appId", 1);
		params.put("groupField", "dim1");
		params.put("startDate", 1);
		params.put("endDate", 1);
		params.put("window", 10);
		

		List funnels = new ArrayList();
		Map funnel1 = new HashMap();
		funnel1.put("actionName", "xxx");
		funnels.add(funnel1);
		Map funnel2 = new HashMap();
		funnel2.put("actionName", "yyy");
		funnels.add(funnel2);
		params.put("funnels", funnels);
		blogDao.query(params);
	}

}
