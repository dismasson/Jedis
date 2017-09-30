package com.sxli.jedis.command;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * Redis数据类型：Set 命令练习
 */
public class SetCMD {

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
    public void SetCMDTest(){
        //sadd将一个或多个元素加入到集合当中，已经存在于集合的元素将被忽略。
        //假如 key 不存在，则创建一个只包含当前写入元素作成员的集合。
        //当 key 不是集合类型时，返回一个错误
        jedis.sadd("names","dismasson");

        //移除集合 key 中的一个或多个元素，不存在的元素会被忽略。
        //当 key 不是集合类型，返回一个错误。2.4版本之前一次只能移除一个元素
        jedis.srem("names","dismasson");

        //SMOVE 是原子性操作。
        //语法： smove set1 set2 value 释义：将set1中的value元素移动到set2后移除set1中的value元素，如果set1中不存在value元素，则不作任何操作
        //如果set2中已经存在value元素，则仅将set1中的value元素给移除，如果set1根set2都不是集合类型，则报异常
        //如果set1存在，set2不存在，那么斤斤将set1中移除value元素
        jedis.smove("names","names2","yangwen");

        jedis.sadd("names","dismasson");

        //sismember 判断 member 元素是否集合 key 的成员
        System.out.println("dismasson是否是names的成员元素之一:"+jedis.sismember("names","dismasson"));
        System.out.println("yangwen是否是names的成员元素之一:"+jedis.sismember("names","yangwen"));

        jedis.del("userName");

        jedis.sadd("userNames","dismasson1");
        jedis.sadd("userNames","dismasson2");
        jedis.sadd("userNames","dismasson3");
        jedis.sadd("userNames","dismasson4");
        jedis.sadd("userNames","dismasson5");

        //spop 移除并返回集合中的一个随机元素
        System.out.println("随机大抽奖，抽出一名幸运用户送出《java从入门到放弃》书籍一套，幸运用户为："+jedis.spop("userNames"));

        //返回集合 key 中的所有成员。不存在的 key 被视为空集合。
        System.out.println("userNames所有name为："+jedis.smembers("userNames"));

        /*srandmember 如果命令执行时，只提供了 key 参数，那么返回集合中的一个随机元素。
        从 Redis 2.6 版本开始， SRANDMEMBER 命令接受可选的 count 参数：
            如果 count 为正数，且小于集合基数，那么命令返回一个包含 count 个元素的数组，
                数组中的元素各不相同。如果 count 大于等于集合基数，那么返回整个集合。
            如果 count 为负数，那么命令返回一个数组，数组中的元素可能会重复出现多次，而数组的长度为 count 的绝对值。
                (就是如果指定count为一个负数，那么返回一个为正数的数组，数组里面的值可以是重复的)
        该操作和 SPOP 相似，但 SPOP 将随机元素从集合中移除并返回，而 SRANDMEMBER 则仅仅返回随机元素，而不对集合进行任何改动。*/
        System.out.println("执行命令：srandmamber userNamse的结果为："+jedis.srandmember("userNames"));
        System.out.println("执行命令：srandmamber userNamse 2的结果为："+jedis.srandmember("userNames",2));
        System.out.println("执行命令：srandmamber userNamse 10的结果为："+jedis.srandmember("userNames",10));
        System.out.println("执行命令：srandmamber userNamse -10的结果为："+jedis.srandmember("userNames",-10));

        //scard 返回集合 key 的基数(集合中元素的数量)。
        System.out.println("userNames中元素数量为："+jedis.scard("userNames"));

        //sdiff 返回一个集合的全部成员，该集合是所有给定集合之间的差集。不存在的 key 被视为空集。
        System.out.println("names跟userNames的差集为："+jedis.sdiff("names","userNames"));
        System.out.println("userNames跟names的差集为："+jedis.sdiff("userNames","names"));

        //sinter 返回一个集合的全部成员，该集合是所有给定集合的交集 不存在的 key 被视为空集。
        //当给定集合当中有一个空集时，结果也为空集(根据集合运算定律)
        System.out.println("未曾添加dismasson到userNames之前，求names跟userNames的交集为："+jedis.sinter("names","userNames"));
        jedis.sadd("userNames","dismasson");
        System.out.println("未曾添加dismasson到userNames之后，求names跟userNames的交集为："+jedis.sinter("names","userNames"));

        System.out.println("names跟不存在的names求交集为："+jedis.sinter("names","names2"));

        //sunion 返回一个集合的全部成员，该集合是所有给定集合的并集。不存在的 key 被视为空集。
        System.out.println("names跟userNames的并集为："+jedis.sunion("names","userNames"));

        //sdiffstore 同sdiff一样求差集，但是会将差集放入到一个集合中
        //语法 sdiffstore names_new userNames names ...
        //释义 求出userNames跟names之间的差集，然后将结果放入到names_new中
        System.out.println("查看全部names_new的值:"+jedis.smembers("names_new"));
        jedis.sdiffstore("names_new","userNames","names");
        System.out.println("查看全部names_new的值:"+jedis.smembers("names_new"));

        //sinterstore 同sinter一样求交集，但是会将交集放入到一个集合中
        //语法 sinterstore names_new2 userNames names ...
        //释义 求出userNames跟names的交集，然后将结果将到names_new2中
        System.out.println("查看全部names_new2的值："+jedis.smembers("names_new2"));
        jedis.sinterstore("names_new2","userNames","names");
        System.out.println("查看全部names_new2的值："+jedis.smembers("names_new2"));

        //sunionstore 同sunion一样求全集，但是会将全集放入到一个集合中
        //语法 sunionstore names_new3 userNames names ...
        //释义 求出userNames跟names的全集然后将结果放入到names_new3中
        System.out.println("查看全部names_new3的值："+jedis.smembers("names_new3"));
        jedis.sunionstore("names_new3","userNames","names");
        System.out.println("查看全部names_new3的值："+jedis.smembers("names_new3"));

        //sscan 命令放在KeyCMD中讲解
    }

}
