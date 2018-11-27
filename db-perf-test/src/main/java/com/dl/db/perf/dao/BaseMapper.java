package com.dl.db.perf.dao;

import java.util.List;


public interface BaseMapper<T> {
    void insertList(List<T> list);
    
    List<T> getAll();
    
    void deleteAll();
    
    void insert(T object);
}
