package com.sxli.jedis.command;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import redis.clients.jedis.Jedis;

/**
 * Redis数据类型：String 命令练习
 */
public class StringCMD {
    private Jedis jedis;

    @Before
    public void init() {
        System.out.println("Junit测试初始化Jedis");
        jedis = RedisPool.getJedis();

    }

    @After
    public void destory() {
        System.out.println("Junit测试销毁Jedis");
        RedisPool.jedisClose(jedis);
    }
}
