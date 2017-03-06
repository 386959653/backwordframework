package com.pds.p2p.core.j2ee.persistence.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.pds.p2p.core.utils.CamelCaseKeyMap;

public interface SqlExecuter {
    @Select(value = "${selectSql}")
    List<CamelCaseKeyMap<Object>> query(@Param("selectSql") String selectSql);

    @Update("${updateSql}")
    int update(@Param("updateSql") String updateSql);
}
