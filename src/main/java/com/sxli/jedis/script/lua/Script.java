package com.sxli.jedis.script.lua;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class Script {
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
     * SCRIPT FLUSH 清除所有脚本缓存
     * 缓存清除过后，再用EVALSHA根据SHA值来执行脚本会出现找不到脚本异常
     */
    @Test
    public void flush() {
        jedis.scriptFlush();
    }

    /**
     * SCRIPT LOAD 将一个脚本添加到脚本缓存中，但是不立刻去执行它
     */
    @Test
    public void load(){
        //加载一个脚本并且返回一个SHA值
        String sha = jedis.scriptLoad( "return redis.call( 'get' , 'foo' )" );
        System.out.println("执行SHA值为：" +sha + "的脚本的结果是：" + jedis.evalsha(sha));
    }

    /**
     * SCRIPT EXISTS 根据SHA值判断脚本是否被缓存
     */
    @Test
    public void exists() {
        //加载一个脚本并且返回一个SHA值
        String sha = jedis.scriptLoad( "return redis.call( 'get' , 'foo' )" );
        System.out.println("SHA值为：" +sha + "脚本是否在Redis中缓存：" + (jedis.scriptExists(sha) ? "存在" : "不存在"));
        System.out.println("执行SHA值为：" +sha + "的脚本的结果是：" + jedis.evalsha(sha));
        System.out.println("清除所有缓存脚本：" + jedis.scriptFlush());
        System.out.println("SHA值为：" +sha + "脚本是否在Redis中缓存：" + (jedis.scriptExists(sha) ? "存在" : "不存在"));
    }

    /**
     * SCRIPT KILL 杀掉正在执行的脚本
     * 如果当前没有正在执行的脚本会出现异常
     */
    @Test
    public void kill() {
        System.out.println(jedis.scriptKill());
    }
}
