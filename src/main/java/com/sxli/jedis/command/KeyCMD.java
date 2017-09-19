package com.sxli.jedis.command;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 *Redis 中对key进行操作的一系列命令
 */
public class KeyCMD {
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
    public void KeyCMDTest(){
        //del 删除指定的一个key或者多个key，不存在的key会被忽略
        jedis.del("userNos");

        //exists 判断一个key是不是在redis中存在
        System.out.println( (jedis.exists("userNos_new") ? jedis.del("userNos_new") : "false"));

        /* keys 返回所有符合条件的key
           列举常用的条件：
           key *：返回所有key
           key ?:返回name的长度只有一位的key
           key [abc]*:返回所有以abc开头的任意长度的key*/
        jedis.set("a","a");
        jedis.set("b","a");
        jedis.set("c","a");
        jedis.set("d","a");
        jedis.set("e","a");
        jedis.set("a1","a");
        jedis.set("b1","a");
        jedis.set("e1","a");
        jedis.set("c1","a");
        System.out.println("返回所有key："+jedis.keys("*"));
        System.out.println("返回keyname的长度只有一位的key："+jedis.keys("?"));
        System.out.println("返回keyname的包含了abc中任意一个的key："+jedis.keys("[abc]*"));
        System.out.println("测试keys [*]的结果："+jedis.keys("[*]"));
        jedis.set("*","a");
        System.out.println("测试keys [*]的结果："+jedis.keys("[*]"));
        /* 实验得到keys [*] 没有返回任何一个key，所以[*]会返回key的name长度只有1个的并且为*的key */

        /*move将当前数据库的 key 移动到给定的数据库 db 当中。
          如果当前数据库(源数据库)和给定数据库(目标数据库)有相同名字的给定 key ，或者 key 不存在于当前数据库，那么 MOVE 没有任何效果。
          因此，也可以利用这一特性，将 MOVE 当作锁(locking)原语(primitive)
          Redis中数据库db的概念：一台服务器上都快开启200个Redis实例了，看着就崩溃了。这么做无非就是想让不同类型的数据属于不同的应用程序而彼此分开。
          那么，redis有没有什么方法使不同的应用程序数据彼此分开同时又存储在相同的实例上呢？就相当于MySQL数据库，不同的应用程序数据存储在不同的数据库下。
          Redis中，数据库是由一个整数索引标识，而不是由一个数据库名称。默认情况下，一个客户端连接到数据库0。Redis配置文件中下面的参数来控制数据库总数：
          redis.conf文件中，有个配置项 databases = xx xx的数目就是Redis中数据库的数目
         */
        System.out.println("连接到Redis中第二个数据库："+jedis.select(1));
        System.out.println("查看第二个数据库中是否存在name："+jedis.exists("name"));
        System.out.println("连接到Redis中第一个数据库："+jedis.select(0));
        if (!jedis.exists("name")) {
            jedis.set("name", "dismasson");
        }
        System.out.println("将数据库一中的name移动到数据库二中："+jedis.move("name",1));
        System.out.println("连接到Redis中第二个数据库："+jedis.select(1));
        System.out.println("查看第二个数据库中是否存在name："+jedis.exists("name"));
        System.out.println("连接到Redis中第一个数据库："+jedis.select(0));
        System.out.println("查看第一个数据库中是否存在name："+jedis.exists("name"));

        /*type 返回 key 所储存的值的类型*/
        jedis.set("age","27");
        jedis.rpush("names1","dismasson");
        jedis.hset("dismasson","name","dismasson");
        jedis.sadd("names2","dismasson");
        jedis.zadd("names3",1,"dismasson");
        System.out.println("age储存的值类型是："+jedis.type("age"));
        System.out.println("names1储存的值类型是："+jedis.type("names1"));
        System.out.println("dismasson储存的值类型是："+jedis.type("dismasson"));
        System.out.println("names2储存的值类型是："+jedis.type("names2"));
        System.out.println("names3储存的值类型是："+jedis.type("names3"));

        /*rename key newkey
          将 key 改名为 newkey 。
          当 key 和 newkey 相同，或者 key 不存在时，返回一个错误。
          当 newkey 已经存在时， rename 命令将覆盖旧值。
          例：执行 rename name name_new 命令的时候，已经存在name_new的key，那么name的值会覆盖掉name_new的旧值
         */
        System.out.println("是否存在age_new："+jedis.exists("age_new"));
        jedis.rename("age","age_new");
        System.out.println("是否存在age_new："+jedis.exists("age_new"));

        /*expire key seconds
         为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
         如果创建key的时候没有指定生存时间，默认就是-1(永久)
         */
        System.out.println("age_new剩余生存时间为："+jedis.ttl("age_new"));
        jedis.expire("age_new",100);
        System.out.println("age_new剩余生存时间为："+jedis.ttl("age_new"));

        /*expireat key timestamp
         为给定 key 设置生存时间，作用跟expire相同，不过expire是设置这个 key 多长时间后消亡，单位是秒
         二expireat 是设置一个时间戳参数，标明这个 key 到了时间戳表示的那个时间点后就消亡
         */
        System.out.println("dismasson剩余生存时间为："+jedis.ttl("dismasson"));
        long timestamp = (System.currentTimeMillis() + 1000 * 10)/1000;
        jedis.expireAt("dismasson",timestamp);
        System.out.println("dismasson剩余生存时间为："+jedis.ttl("dismasson"));

        /*persist key
         移除给定 key 的生存时间，将这个 key 从『易失的』(带生存时间 key )转换成『持久的』(一个不带生存时间、永不过期的 key )。
         */
        jedis.persist("dismasson");
        System.out.println("dismasson剩余生存时间为："+jedis.ttl("dismasson"));

        /*pexpire
         这个命令和 expire 命令的作用类似，但是它以毫秒为单位设置 key 的生存时间，而不像 expire 命令那样，以秒为单位。
         */
        jedis.pexpire("dismasson",10000);
        System.out.println("dismasson剩余生存时间为："+jedis.ttl("dismasson"));

        /*pexpireat
        这个命令和 expireat 命令类似，但它以毫秒为单位设置 key 的过期 unix 时间戳，而不是像 expireat 那样，以秒为单位。
         */
        timestamp = System.currentTimeMillis() + 1000 * 100;
        jedis.pexpireAt("dismasson",timestamp);
        System.out.println("dismasson剩余生存时间为："+jedis.ttl("dismasson"));

        /*pttl
         这个命令类似于 ttl 命令，但它以毫秒为单位返回 key 的剩余生存时间，而不是像 ttl 命令那样，以秒为单位。
         */
        System.out.println("dismasson剩余生存时间为："+jedis.pttl("dismasson")+"毫秒");

        /*renamenx
         当且仅当 newkey 不存在时，将 key 改名为 newkey 。当 key 不存在时，返回一个错误。
         */
        jedis.set("pc","mechrevo");
        System.out.println("pc2是否存在："+jedis.exists("pc2"));
        jedis.renamenx("pc","pc2");
        System.out.println("pc2是否存在："+jedis.exists("pc2"));

        /*randomkey
         从当前数据库中随机返回(不删除)一个 key 。
         */
        System.out.println("随机抽取当前数据库的一个key："+jedis.randomKey());

        /*dump 官方中文文档说是序列化

         */
        System.out.println(jedis.dump("name"));
    }
}
