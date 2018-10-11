package com.sxli.jedis.command;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.List;
import java.util.Set;

public class ScanCMD {

    private Jedis jedis;

    @Before
    public void init() {
        System.out.println("Junit测试初始化Jedis\n");
        jedis = RedisPool.getJedis();

    }

    @After
    public void destory() {
        System.out.println("\nJunit测试销毁Jedis");
        RedisPool.jedisClose(jedis);
    }

    /**
     * 初始化相关key，总计录入10000条key
     */
    @Test
    public void initKey() {
        Pipeline pipeline = jedis.pipelined();
        for (int i = 0; i < 100000; i++) {
            String key = "key" + i;
            pipeline.set(key, "");
        }
    }

    /**
     * 单独使用keys命令查看效率
     */
    @Test
    public void keys() {
        //开始时间
        Long beginDate = System.currentTimeMillis();
        Set<String> keys = jedis.keys("key*");
        //结束时间
        Long endDate = System.currentTimeMillis();
        System.out.println("耗时:" + (endDate - beginDate) + "毫秒，获取到：" + keys.size() + "条数据");
    }

    /**
     * 测试scan命令的缺点
     * 1：查询到重复的数据
     * 2：查询的数据有遗漏
     */
    @Test
    public void scan() {
        // 第一步，录入5条数据，A1、A2、A4、A5、A6
        for (int i = 1; i <= 6; i++) {
            if (i != 3) {
                String key = "A" + i;
                jedis.set(key, "");
            } else {
                jedis.del("A3");
            }
        }
        ScanParams scanParams = new ScanParams();
        scanParams.match("A*").count(50000);
        String cursor = "0";
        // 第二步，进行scan命令获取
        ScanResult<String> result = jedis.scan(cursor, scanParams);
        cursor = new String(result.getCursorAsBytes());
        List<String> keys = result.getResult();
        for (String key : keys) {
            System.out.println("1-" + key);
        }
        // 此时新增一条A3纪录
        jedis.set("A3", "");
        result = jedis.scan(cursor, scanParams);
        keys = result.getResult();
        for (String key : keys) {
            System.out.println("2-" + key);
        }
    }
}
