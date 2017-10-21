package com.sxli.jedis.command;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

/**
 * Redis 中连接命令练习
 */
public class ConnectionCMD {

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

    @Test
    public void select(){
        /*
         select 切换到指定的数据库，数据库索引号 index 用数字值指定，以 0 作为起始索引值。
         */
        System.out.println(jedis.select(1));
    }

    @Test
    public void auth(){
        /*
         auth 如果Redis中设置了密码(requirepass)，name连接后如果不auth password，命令将不会被接受
         */
        //第一步，设置一个密码
        jedis.configSet("requirepass","pwd");
        //测试get name
        try {
            System.out.println(jedis.get("name"));
        }catch (JedisDataException e){
            System.out.println("出现异常，异常信息如下："+e.getMessage());
        }
        //第三步，使用auth password
        jedis.auth("pwd");

        //第四步，测试get name此时是否可用
        System.out.println(jedis.get("name"));
    }

    @Test
    public void echo(){
        /*
         echo 打印一个特定的信息 message ，测试时使用。
         */
        System.out.println(jedis.echo("hello world"));
    }

    @Test
    public void quit(){
        /*
         quit 请求服务器关闭与当前客户端的连接
         因为是让连接池来管理jedis连接的，所以这个时候执行取消连接后，连接池回收资源的时候会出现异常
         */
        jedis.quit();
    }

    @Test
    public void ping(){
        /*
         ping 使用客户端向 Redis 服务器发送一个 PING ，如果服务器运作正常的话，会返回一个 PONG 。
         */
        System.out.println(jedis.ping());
    }
}
