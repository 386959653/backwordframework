/**
 * Copyright (C) 2014 Baidu, Inc. All Rights Reserved.
 */
package com.pds.p2p.system.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.pds.p2p.system.config.ConfigConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.pds.p2p.core.j2ee.context.Config;
import com.pds.p2p.core.j2ee.context.SpringContextHolder;
import com.pds.p2p.core.utils.SerializeUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * *******************************  功能模块：Jedis公共类
 * ********************************
 *
 * @author wuyunfeng
 * @version 1.0.0
 *          ********************************
 *          修改项 修改人 修改时间
 *          <p>
 *          ********************************
 * @date 2016-5-12
 * @copyright baidu.com 2016
 */
public class JedisUtil {

    private static final Logger logger = LogManager.getLogger(JedisUtil.class);
    private static final String KEY_PREFIX =
            Config.get(ConfigConstants.REDIS_KEY_PREFIX).toUpperCase() + Config.get(ConfigConstants.APPLICATION_PROFILE)
                    .toUpperCase();

    /**
     * 获取jedis
     *
     * @return
     */
    public static Jedis getJedis() {
        return SpringContextHolder.getBean("jedisPool", JedisPool.class).getResource();

    }

    /**
     * 释放jedis
     *
     * @return
     */
    public static void returnJedis(Jedis jedis) {
        jedis.close();
    }

    /**
     * 向缓存中设置对象
     *
     * @param key
     * @param value
     *
     * @return
     */
    public static boolean set(String key, Object value) {
        Jedis jedis = getJedis();
        try {
            jedis.set(KEY_PREFIX + key, SerializeUtil.serialize(value));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return false;
        } finally {
            returnJedis(jedis);
        }
    }

    /**
     * 向缓存中分组设置对象
     *
     * @param key
     * @param value
     *
     * @return
     */
    public static Long hset(String group, String key, Object value) {
        Jedis jedis = getJedis();
        try {
            jedis.hset(KEY_PREFIX + group, key, SerializeUtil.serialize(value));
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());

        } finally {
            returnJedis(jedis);
        }
        return null;
    }

    /**
     * 向缓存中设置对象
     *
     * @param key
     * @param value
     *
     * @return
     */
    public static boolean setex(String key, int expire, Object value) {
        Jedis jedis = getJedis();
        try {
            jedis.setex(KEY_PREFIX + key, expire, SerializeUtil.serialize(value));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return false;
        } finally {
            returnJedis(jedis);
        }
    }

    /**
     * 删除缓存中得对象，根据key
     *
     * @param key
     *
     * @return
     */
    public static boolean del(String key) {
        Jedis jedis = getJedis();
        try {
            jedis.del(KEY_PREFIX + key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return false;
        } finally {
            returnJedis(jedis);
        }
    }

    /**
     * 删除缓存中得对象，根据key
     *
     * @param key
     *
     * @return
     */
    public static boolean clear() {
        Jedis jedis = getJedis();
        try {
            jedis.flushDB();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return false;
        } finally {
            returnJedis(jedis);
        }
    }

    /**
     * 根据key 获取内容
     *
     * @param key
     *
     * @return
     */
    public static Object get(String key) {
        Jedis jedis = getJedis();
        try {
            Object value = SerializeUtil.unserialize(jedis.get(KEY_PREFIX + key));
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return false;
        } finally {
            returnJedis(jedis);
        }
    }

    /**
     * 根据key 获取内容
     *
     * @param key
     *
     * @return
     */
    public static Map<String, Object> hgetAll(String group) {
        Jedis jedis = getJedis();
        try {
            Map<String, String> result = jedis.hgetAll(KEY_PREFIX + group);
            Map<String, Object> result2 = new HashMap<String, Object>();
            for (Entry<String, String> kv : result.entrySet()) {
                result2.put(kv.getKey(), SerializeUtil.unserialize(getJedis().get(kv.getValue())));
            }
            return result2;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
        } finally {
            returnJedis(jedis);
        }
        return null;
    }

    /**
     * 根据key 获取内容
     *
     * @param key
     *
     * @return
     */
    public static <T> Map<String, T> hgetAllToObject(String group, String key, Class<T> t) {
        try {
            JSONObject.parseObject(JSON.toJSONString(hget(group, key)), new TypeReference<Map<String, T>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
        }
        return null;
    }

    /**
     * 根据key 获取内容
     *
     * @param key
     *
     * @return
     */
    public static Object hget(String group, String key) {
        Jedis jedis = getJedis();
        try {
            return SerializeUtil.unserialize(jedis.hget(KEY_PREFIX + group, key));
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
        } finally {
            returnJedis(jedis);
        }
        return null;
    }

    /**
     * 根据key 获取内容
     *
     * @param key
     *
     * @return
     */
    public static <T> T hgetToObject(String group, String key, Class<T> t) {
        try {
            return JSONObject.parseObject(JSON.toJSONString(hget(group, key)), t);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
        }
        return null;
    }

    /**
     * 根据key 获取内容
     *
     * @param key
     *
     * @return
     */
    public static Set<String> hkeys(String group) {
        Jedis jedis = getJedis();
        try {
            return jedis.hkeys(KEY_PREFIX + group);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
        } finally {
            returnJedis(jedis);
        }
        return null;
    }

    /**
     * 根据key 获取内容
     *
     * @param key
     *
     * @return
     */
    public static List<Object> hvals(String group) {
        Jedis jedis = getJedis();
        try {
            List<String> result = jedis.hvals(KEY_PREFIX + group);
            List<Object> result2 = new ArrayList<Object>();
            for (String str : result) {
                result2.add(SerializeUtil.unserialize(str));
            }
            return result2;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
        } finally {
            returnJedis(jedis);
        }
        return null;
    }

    /**
     * 根据key 获取内容
     *
     * @param key
     *
     * @return
     */
    public static <T> List<T> hvalsToObject(String group, Class<T> t) {
        try {
            return SerializeUtil.unserializeList(JSON.toJSONString(hvals(group)), t);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
        }
        return null;
    }

    /**
     * 根据key 获取内容
     *
     * @param key
     *
     * @return
     */
    public static <T> T getObject(String key, Class<T> clazz) {
        Jedis jedis = getJedis();
        try {
            T value = SerializeUtil.unserializeObject(jedis.get(KEY_PREFIX + key), clazz);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return null;
        } finally {
            returnJedis(jedis);
        }
    }

    /**
     * 根据key 获取内容
     *
     * @param key
     *
     * @return
     */
    public static <T> List<T> getList(String key, Class<T> clazz) {
        Jedis jedis = getJedis();
        try {
            List<T> value = SerializeUtil.unserializeList(jedis.get(KEY_PREFIX + key), clazz);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return null;
        } finally {
            returnJedis(jedis);
        }
    }

    /**
     * @param key
     * @param seconds
     *
     * @return void
     *
     * @function 设置过期时间
     * @date 2014-12-4 下午08:25:51
     * @author v_lianghua
     */
    public static void expire(String key, int seconds) {
        Jedis jedis = getJedis();
        if (seconds <= 0) {
            return;
        }
        try {
            jedis.expire(KEY_PREFIX + key, seconds);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
        } finally {
            returnJedis(jedis);
        }
    }

    /**
     * @param key
     * @param seconds
     *
     * @return void
     *
     * @function 的获取大小
     * @date 2014-12-4 下午08:25:51
     * @author v_lianghua
     */
    public static Long dbsize() {
        Jedis jedis = getJedis();
        try {
            return jedis.dbSize();
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
        } finally {
            returnJedis(jedis);
        }
        return null;
    }

    public static void main(String[] args) {
        /*String applicatonContextUrl = "spring/spring-all.xml";
        ApplicationContext cxt = new ClassPathXmlApplicationContext(applicatonContextUrl);
        JedisPool jedisPool = (JedisPool) cxt.getBean("jedisPool");
        Jedis jedis = jedisPool.getResource();
        //设置group>key>value,如果已经存在，则不更新
        jedis.hsetnx("a", "a1", "a11");
        jedis.hsetnx("a", "a2", "a22");
        jedis.hsetnx("a", "a3", "a33");
         //设置group>key>value,如果已经存在，则更新
        jedis.hsetnx("b", "b1", "b11");
        //jedis.expire("a",0);//分组失效时间秒
        //jedis.expireAt("a", new Date(2015,12, 30).getTime());//分组失效到期日期，时间戳
        System.out.println(jedis.hget("a", "a1"));//获取group.key的值
        System.out.println(jedis.hkeys("a"));//获取group下所有的键
        System.out.println(jedis.hvals("a"));//获取group下所有的值
        System.out.println(jedis.hgetAll("a"));//获取group下所有的键值对
        */

        //      原生态add不封装开始
        //add
        //      jedis.set("mxskey","mxsvalue1");
        //      jedisPool.returnResource(jedis);
        //
        //select
        //      System.out.println(jedis.get("mxskey"));
        //
        //      //delete
        //      jedis.del("mxskey");
        //      System.out.println("delete 后"+jedis.get("mxskey"));
        //      原生态add不封装结束

        //      测试计数器原子性操作开始
        //      for (int i =0;i<10;i++){
        //      Thread thd=new Thread(new Runnable(){
        //          public void run(){
        //              long rtn = RedisUtil.incrTaskCnt("phe:test:3", ""+253);
        //              System.out.println("baymax:" + rtn);
        //              if(rtn ==10){
        //                  System.out.println("this is last");
        //              }
        //
        //          }
        //      });
        //      thd.start();
        //  }
        //      测试计数器原子性操作结束

        //      测试group+batch概念开始
        ////        添加一批次：jedis.hset("requestid", "batchnum", "cnt");
        //jedis.hset("mxstest01", "01", "{\"addnum\": \"1000\",\"msg\": \"ok\"}");
        //jedis.hset("mxstest01", "02", "{\"addnum\": \"3\",\"msg\": \"ok\"}");
        //      取全部批次List<String> list = jedis.hvals("requestid");
        //List<String> list = jedis.hvals("mxstest01");
        //System.out.println(list.size());
        //int sum = 0;
        //for (String str : list) {
        //  JSONObject addnum = JSON.parseObject(str, JSONObject.class);
        //  sum = sum + Integer.parseInt(addnum.getString("addnum"));
        //}
        //System.out.println(sum);
        //      测试group+batch概念结束,注意：你重复调用上边这段，redis是默认给你做update的

        ////        取一个批次jedis.hget("requestid", "batchnum");
        //      String batchStr = jedis.hget("mxstest01", "02");
        //      System.out.println(batchStr);
        //
        ////        设置某个建的有效期的方法jedis.expire(key, seconds);
        ////        jedis.expire("mxstest01", 10);
        //      jedis.del("mxstest01");
        //      System.out.println("delete 后 mxstest01:"+getTaskDoneNum("mxstest01",""));
        //      System.out.println("delete 后 mxstest01:"+jedis.hvals("mxstest01").size());
        //
        //      RedisVO redisVO = new RedisVO();
        //      redisVO.setAddnum("1");
        //      redisVO.setMsg("");
        //      redisVO.setErrorMsg("a");
        //      System.out.println(JSON.toJSONString(redisVO));

        //      jedis.del("ec3f090c-43a3-43ad-91df-3e5ec288c8a9");
        //System.out.println(getTaskDoneNum("",""));

    }
}
