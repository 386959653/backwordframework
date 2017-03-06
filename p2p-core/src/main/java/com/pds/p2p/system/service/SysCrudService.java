/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.pds.p2p.system.service;

import java.util.Date;

import com.pds.p2p.core.j2ee.persistence.entity.EntityInfo;
import com.pds.p2p.core.j2ee.persistence.entity.EntityInfoFactory;
import com.pds.p2p.core.j2ee.service.CrudService;
import com.pds.p2p.core.jdbc.pk.SequenceFactory;
import com.pds.p2p.system.domain.BaseDomain;
import com.pds.p2p.system.web.session.SessionUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import com.pds.p2p.core.j2ee.persistence.dao.CrudDao;
import com.pds.p2p.core.jdbc.pk.SequenceGenerator;
import com.pds.p2p.system.web.session.SessionUser;

@Transactional
public abstract class SysCrudService<D extends CrudDao<T, Long>, T extends BaseDomain> extends CrudService<D, T> {
    protected static Logger logger = LoggerFactory.getLogger(SysCrudService.class);

    @Autowired
    @Qualifier("cachedSequenceFactory")
    public SequenceFactory sequenceFactory;

    @Override
    public void preInsert(T entity) {

        /**
         * 设置主键
         */
        EntityInfo<T> entityInfo = EntityInfoFactory.forEntityClass((Class<T>) entity.getClass());
        SequenceGenerator sequenceGenerator = sequenceFactory.name(this.getSequenceName(entity));
        sequenceGenerator.nextLongValue();
        entityInfo.setId(entity, sequenceGenerator.nextLongValue());

        entity.setCreationDate(new Date());
        entity.setLastUpdateDate(entity.getCreationDate());
        SessionUser user = SessionUtils.getSessionUser();
        Validate.notNull(user);
        entity.setCreatedBy(user.getUsername());
        entity.setLastUpdatedBy(user.getUsername());

    }

    @Override
    public void preUpdate(T entity) {
        entity.setLastUpdateDate(new Date());
        SessionUser user = SessionUtils.getSessionUser();
        Validate.notNull(user);
        entity.setLastUpdatedBy(user.getUsername());
    }

    public String getSequenceName(T entity) {
        Class<?> clazz = entity.getClass();
        if (!clazz.getSuperclass().equals(BaseDomain.class)) {
            while (!clazz.getSuperclass().equals(BaseDomain.class)) {
                clazz = clazz.getSuperclass();
            }
        }
        String sequenceName = clazz.getSimpleName();
        return sequenceName;
    }
}
