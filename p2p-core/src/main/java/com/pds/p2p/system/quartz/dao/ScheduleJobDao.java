package com.pds.p2p.system.quartz.dao;

import java.util.List;

import com.pds.p2p.core.j2ee.persistence.annotation.MyBatisDao;
import com.pds.p2p.system.quartz.domain.ScheduleJob;

@MyBatisDao
public interface ScheduleJobDao {

    int deleteByPrimaryKey(Long jobId);

    int insert(ScheduleJob record);

    int insertSelective(ScheduleJob record);

    ScheduleJob selectByPrimaryKey(Long jobId);

    int updateByPrimaryKeySelective(ScheduleJob record);

    int updateByPrimaryKey(ScheduleJob record);

    List<ScheduleJob> getAll();
}