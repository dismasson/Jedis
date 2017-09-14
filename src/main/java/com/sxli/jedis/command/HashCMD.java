package com.sxli.jedis.command;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis数据类型：Hash类型命令练习
 * Hash数据类型相当于一个小型的Redis，可以将它看做一个对象，
 * 它的key是对象名，本身储存的键值对可以看做属性
 */
public class HashCMD {
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
    public void HashCMDTest(){
        //hset 将哈希表 key 中的域 field 的值设为 value 。如果 key 不存在，
        // 一个新的哈希表被创建并进行 HSET 操作。如果域 field 已经存在于哈希表中，旧值将被覆盖。
        jedis.hset("dismasson","name","dismasson");
        jedis.hset("dismasson","age","27");
        System.out.println(jedis.hgetAll("dismasson"));

        //hdel 删除哈希表 key 中的一个或多个指定field，不存在的field将被忽略。
        jedis.hdel("dismasson","age");
        System.out.println(jedis.hgetAll("dismasson"));

        //hexists 查看哈希表 key 中，给定域 field 是否存在
        System.out.println("查看age是否存在："+jedis.hexists("dismasson","age"));

        //hget 返回哈希表 key 中给定域 field 的值。
        System.out.println("name="+jedis.hget("dismasson","name"));

        //hgetall 返回哈希表 key 中，所有的field和值
        System.out.println("所有信息："+jedis.hgetAll("dismasson"));

        //hincrby 为哈希表 key 中的域 field 的值加上增量 increment 。增量也可以为负数，相当于对给定域进行减法操作。
        //如果 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。
        //如果域 field 不存在，那么在执行命令前，field的值被初始化为 0 。
        //对一个储存字符串值的域 field 执行 HINCRBY 命令将造成一个错误。
        jedis.hincrBy("dismasson","age",27);
        System.out.println("age="+jedis.hget("dismasson","age"));

        //hincrbyfloat为哈希表 key 中的域 field 加上浮点数增量 increment 。
        //如果哈希表中没有域 field ，那么 HINCRBYFLOAT 会先将域 field 的值设为 0 ，然后再执行加法操作。
        //如果键 key 不存在，那么 HINCRBYFLOAT 会先创建一个哈希表，再创建域 field ，最后再执行加法操作。
        //当以下任意一个条件发生时，返回一个错误：
        //field 的值不是字符串类型(因为 redis 中的数字和浮点数都以字符串的形式保存，所以它们都属于字符串类型）
        //field 当前的值或给定的增量 increment 不能解释(parse)为双精度浮点数
        try {
            jedis.hincrByFloat("dismasson","name",0.2);
        }catch (JedisDataException e){
            System.out.println("给name添加一个双精度浮点数出现异常，异常原因如下："+e.getMessage());
        }

        //hkeys 返回哈希表 key 中的所有
        System.out.println("dismasson中所有field："+jedis.hkeys("dismasson"));

        //hlen 返回哈希表 key 中field的数量
        System.out.println("dismasson中所有field的数量为："+jedis.hlen("dismasson"));

        //hmget 返回哈希表 key 中，一个或多个给定域的值。
        //如果给定的域不存在于哈希表，那么返回一个 nil 值。
        //因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
        System.out.println(jedis.hmget("dismasson","name","age"));

        //hmset 同时将多个 field-value (域-值)对设置到哈希表 key 中。
        //此命令会覆盖哈希表中已存在的域。
        //如果 key 不存在，一个空哈希表被创建并执行 HMSET 操作。
        Map<String,String> map = new HashMap<String,String>();
        map.put("height","175cm");
        map.put("weight","82KG");
        jedis.hmset("dismasson",map);
        System.out.println("dismasson中所有field："+jedis.hgetAll("dismasson"));

        //hsetnx 将哈希表 key 中的域 field 的值设置为 value ，当且仅当域 field 不存在。
        //若域 field 已经存在，该操作无效。
        //如果 key 不存在，一个新哈希表被创建并执行 HSETNX 命令。
        jedis.hsetnx("dismasson","name","dismasson");
        jedis.hsetnx("dismasson","sex","man");
        System.out.println("dismasson中所有field："+jedis.hgetAll("dismasson"));

        //hvals 返回哈希表 key 中所有域的值
        System.out.println("dismasson的所有个人信息如下："+jedis.hvals("dismasson"));

        //hscan 命令放在KeyCMD中讲解
    }
}
