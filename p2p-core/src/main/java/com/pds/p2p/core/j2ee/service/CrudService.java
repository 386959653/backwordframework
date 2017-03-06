package com.pds.p2p.core.j2ee.service;

import java.util.List;

import com.pds.p2p.core.j2ee.persistence.entity.EntityInfo;
import com.pds.p2p.core.j2ee.persistence.entity.EntityInfoFactory;
import com.pds.p2p.system.domain.BaseDomain;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pds.p2p.core.j2ee.persistence.dao.CrudDao;

public abstract class CrudService<D extends CrudDao<T, Long>, T extends BaseDomain> {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 持久层对象
     */
    @Autowired
    protected D dao;

    public T findOne(Long id) {
        return dao.selectByPrimaryKey(id);
    }


    public List<T> findList(T entity) {
        return dao.selectSelective(entity);
    }

    public void delete(Long id) {
        dao.deleteByPrimaryKey(id);
    }

    public int insert(T entity) {
        preInsert(entity);
        return dao.insert(entity);
    }

    public int insertList(List<T> list) {
        Validate.notNull(list);
        for (int i = 0; i < list.size(); i++) {
            T entity = list.get(i);
            preInsert(entity);
        }
        return dao.insertList(list);
    }

    public int insertSelective(T entity) {
        preInsert(entity);
        return dao.insertSelective(entity);
    }

    public int updateByPrimaryKey(T entity) {
        preUpdate(entity);
        return dao.updateByPrimaryKey(entity);
    }

    public int updateByPrimaryKeySelective(T entity) {
        preUpdate(entity);
        return dao.updateByPrimaryKeySelective(entity);
    }

    public int saveOrUpdate(T entity) {

        EntityInfo<T> entityInfo = EntityInfoFactory.forEntityClass((Class<T>) entity.getClass());
        int ret = 0;
        if (entityInfo.getId(entity) != null) {
            ret = this.updateByPrimaryKey(entity);
        } else {
            ret = this.insert(entity);
        }
        return ret;
    }

    public int saveOrUpdateSelective(T entity) {

        EntityInfo<T> entityInfo = EntityInfoFactory.forEntityClass((Class<T>) entity.getClass());
        int ret = 0;
        if (entityInfo.getId(entity) != null) {
            ret = this.updateByPrimaryKeySelective(entity);
        } else {
            ret = this.insertSelective(entity);
        }
        return ret;
    }

    protected void preUpdate(T entity) {

    }

    protected void preInsert(T entity) {

    }

}
