/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.pds.p2p.system.service;

import java.util.List;

import com.github.pagehelper.PageInfo;

/**
 * Created by wuyunfeng on 2016/5/30.
 */
public interface BaseService<T> {

    public T findOne(Long id);

    public List<T> findList(T entity);

    public void delete(Long id);

    public int insert(T entity);

    public int insertList(List<T> list);

    public int insertSelective(T entity);

    public int updateByPrimaryKey(T entity);

    public int updateByPrimaryKeySelective(T entity);

    public int saveOrUpdate(T entity);

    public int saveOrUpdateSelective(T entity);

}
