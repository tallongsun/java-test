package com.dl.mybatis.dao;

import java.util.List;
import java.util.Map;

import com.dl.mybatis.po.Blog;

public interface BlogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Blog record);

    int insertSelective(Blog record);
    
    void insertList(List<Blog> list);

    Blog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Blog record);

    int updateByPrimaryKey(Blog record);
    
    List<Blog> getAll();
    
    
    Blog selectBlog(long id);
    List<Map<String,Object>> selectBlogs();
    List<Blog> selectBlogs2(long unused);
    int insertBlog(List<Blog> blog);
    int insertBlog2(Blog blog);
    void updateBlog(Blog blog);
    void deleteBlog(Blog blog);
    
    
    Map query(Map params);
}