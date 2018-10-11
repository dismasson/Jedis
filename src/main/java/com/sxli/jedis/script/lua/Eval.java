package com.sxli.jedis.script.lua;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class Eval {

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
     * EVAL 执行LUA脚本
     */
    @Test
    public void eval(){
        jedis.eval("return redis.call('set',KEYS[1],'bar1')",1,"foo");
    }

    /**
     * EVALSHA Redis会记住之前执行过的任何脚本，并且采用SHA算法求出一个脚本的哈希值，可以直接用这个HASH值去执行同样的脚本
     */
    @Test
    public void evalsha(){
        System.out.println(jedis.eval("return redis.call('get','foo')"));
        System.out.println(jedis.evalsha("6b1bf486c81ceb7edf3c093f4c48582e38c0e791"));
    }
}
