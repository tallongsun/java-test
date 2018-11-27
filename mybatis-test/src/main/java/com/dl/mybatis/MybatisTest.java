package com.dl.mybatis;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.dl.mybatis.dao.BlogMapper;
import com.dl.mybatis.po.Blog;

@SpringBootApplication
public class MybatisTest {
	public static void main(String[] args) throws IOException {
		SpringApplication.run(MybatisTest.class, args);
		
		String resource = "datasource.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

		// SqlSession session = sqlSessionFactory.openSession();
		// try {
		// Blog blog = (Blog)
		// session.selectOne("com.dl.mybatis.dao.BlogMapper.selectBlog", 10);
		// System.out.println(blog);
		// } finally {
		// session.close();
		// }

		SqlSession session = sqlSessionFactory.openSession();
		try {
			BlogMapper mapper = session.getMapper(BlogMapper.class);
			Blog blog = mapper.selectByPrimaryKey(10L);
			System.out.println(blog.getTitle());
			
			blog = new Blog();
			blog.setId(100L);
			blog.setTitle("100 title");
			mapper.insert(blog);
			
			blog = mapper.selectByPrimaryKey(100L);
			System.out.println(blog.getTitle());
			
			blog.setTitle("100 title update");
			mapper.updateByPrimaryKey(blog);
			System.out.println(blog.getTitle());
			
			mapper.deleteByPrimaryKey(100L);
		} finally {
			session.close();
		}
	}
}
