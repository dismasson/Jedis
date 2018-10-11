package com.sxli.jedis.command;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class PipelineCMD {

    private Jedis jedis;

    @Before
    public void init() {
        System.out.println("Junit测试初始化Jedis");
        jedis = RedisPool.getJedis();
        initKey();
    }

    @After
    public void destory() {
        System.out.println("Junit测试销毁Jedis");
        RedisPool.jedisClose(jedis);
    }

    /**
     * 初始化相关key，总计录入10000条key
     */
    private void initKey() {
        Pipeline pipeline = jedis.pipelined();
        for (int i = 0; i < 100000; i++) {
            String key = "key" + i;
            pipeline.set(key, "");
        }
    }

    /**
     * 不使用pipeline
     */
    @Test
    public void notUsePipeline() {
        //开始时间
        Long beginDate = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            String key = "key" + i;
            jedis.get(key);
        }
        //结束时间
        Long endDate = System.currentTimeMillis();
        System.out.println("不使用pipeline，耗时:" + (endDate - beginDate) + "毫秒");
    }

    /**
     * 使用pipeline提高性能
     */
    @Test
    public void usePipeline() {
        //开始时间
        Long beginDate = System.currentTimeMillis();
        Pipeline pipeline = jedis.pipelined();
        for (int i = 0; i < 100000; i++) {
            String key = "key" + i;
            pipeline.get(key);
        }
        //结束时间
        Long endDate = System.currentTimeMillis();
        System.out.println("使用pipeline，耗时:" + (endDate - beginDate) + "毫秒");
    }
}
