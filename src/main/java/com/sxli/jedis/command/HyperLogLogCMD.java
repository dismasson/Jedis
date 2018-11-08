package com.sxli.jedis.command;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.io.IOException;

/**
 * HyperLogLog 数据结构用来处理统计类数据
 * 如：统计网站每个网页每天的 UV 数据
 */
public class HyperLogLogCMD {

    private Jedis jedis;

    @Before
    public void init() {
        System.out.println("Junit测试初始化Jedis");
        jedis = RedisPool.getJedis();

    }

    @After
    public void destory() {
        System.out.println("\nJunit测试销毁Jedis");
        RedisPool.jedisClose(jedis);
    }

    /**
     * pfadd 命令 同zadd等一样
     */
    @Test
    public void pfadd() throws IOException {
        // 初始化写入一百万条统计数据
        Pipeline pipeline = jedis.pipelined();
        String key = "test_count_page";
        pipeline.del(key);
        for (int i = 0; i < 1000000; i++) {
            pipeline.pfadd(key, i + "");
        }
        pipeline.close();
    }

    /**
     * pfcount 命令
     */
    @Test
    public void pfcount() throws IOException {
        String key = "test_count_page";
        System.out.println(jedis.pfcount(key));
    }

    @Test
    public void pfmerge() throws IOException {
        // 初始化写入一百万条统计数据
        Pipeline pipeline = jedis.pipelined();
        String key = "test_count_page_";
        pipeline.del(key);
        // 定义五个hyperLogLog结构，每个结构初始化XX条数据，最后合并5个结构
        for (int i = 0; i < 5; i++) {
            String key2 = key + i;
            pipeline.del(key2);
            for (int j = 0; j < 100000; j++) {
                pipeline.pfadd(key2, j + "");
            }
        }
        pipeline.close();
        for (int i = 0; i < 5; i++) {
            String key2 = key + i;
            System.out.println(key2 + " 合并结构前：" + jedis.pfcount(key2));
        }
        jedis.pfmerge(key,key + 0,key + 1,key + 2,key + 3,key + 4);
        System.out.println(key + " 合并结构后：" + jedis.pfcount(key));
    }

}
