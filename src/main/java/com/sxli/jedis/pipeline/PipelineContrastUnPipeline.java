package com.sxli.jedis.pipeline;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

/**
 * 事务型流水线跟非事务型流水线的性能对比
 */
public class PipelineContrastUnPipeline {

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

    @Test
    public void notransaction(){
        //获取当前时间(毫秒)
        Long startTime = System.currentTimeMillis();

        for(int i=0;i<10000;i++){
            jedis.set("notransaction"+i,"test"+i);
        }

        //获取当前时间(毫秒)
        Long stopTime = System.currentTimeMillis();
        System.out.println("取消事务执行所需时间（毫秒）:"+(stopTime-startTime));
    }

    @Test
    public void pipeline(){
        //获取当前时间(毫秒)
        Long startTime = System.currentTimeMillis();
        //开启事务
        Transaction tran = jedis.multi();
        for(int i=0;i<10000;i++){
            tran.set("pipeline"+i,"test"+i);
        }
        //提交事务
        tran.exec();
        //获取当前时间(毫秒)
        Long stopTime = System.currentTimeMillis();
        System.out.println("执行事务型流水线所需时间（毫秒）:"+(stopTime-startTime));
    }

    @Test
    public void nopipeline(){
        //获取当前时间(毫秒)
        Long startTime = System.currentTimeMillis();
        Pipeline pipeline = jedis.pipelined();
        for(int i=0;i<10000;i++){
            pipeline.set("nopipeline"+i,"test"+i);
        }
        //获取当前时间(毫秒)
        Long stopTime = System.currentTimeMillis();
        System.out.println("执行非事务型流水线所需时间（毫秒）:"+(stopTime-startTime));
    }

}
