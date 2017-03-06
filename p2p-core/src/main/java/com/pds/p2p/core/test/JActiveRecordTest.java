package com.pds.p2p.core.test;

import com.alibaba.fastjson.JSONObject;
import com.pds.p2p.core.j2ee.context.Config;
import com.pds.p2p.core.jdbc.ar.DB;
import com.pds.p2p.core.jdbc.ar.Record;
import com.pds.p2p.core.jdbc.ar.Table;

public class JActiveRecordTest {

    public static void main(String[] args) {
        /* 连接数据库  */
        String url = Config.get("jdbc.url");
        String username = Config.get("jdbc.username");
        String password = Config.get("jdbc.password");
        DB myDb = DB.open(url, username, password);

		/* 添加   */
        Table zombie = myDb.active("sys_dict");

		
/*		zombie.create(
				//"id","120",
				"value","19999",
				"label","测试",
				"descn","测试描述",
				"type","self_test",
				"sort","1",
				"create_by","1",
				"create_date","2015-12-24 14:16:30",
				"update_by","1",
				"update_date","2015-12-24 14:16:30",
				"remarks","remarks"
				);*/
        //parent_id
		
		/* 查询   */
        Record jim = zombie.findOne("type", "self_test");
        System.out.println(JSONObject.toJSONString(jim));
		
		/* 更新   */
        //		Record jim = zombie.find(3);
        //		jim.set("graveyard","Benny Hills Memorial").save();
        //		jim.update("graveyard","Benny Hills Memorial");
		
		/* 删除  */
        //zombie.delete(jim);
    }
}
