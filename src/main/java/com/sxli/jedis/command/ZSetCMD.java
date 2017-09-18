package com.sxli.jedis.command;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ZParams;

/**
 * Redis 数据类型：ZSet
 */
public class ZSetCMD {
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
    public void ZSetCMDTest(){
        //zadd 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。
        //如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。
        //score 值可以是整数值或双精度浮点数。
        //如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作。
        //当 key 存在但不是有序集类型时，返回一个错误。
        //语法 zadd key soure member [soure member] [soure member] ...
        jedis.zadd("userNos",1.4,"a01");
        jedis.zadd("userNos",2.5,"a02");
        jedis.zadd("userNos",3.6,"a03");

        //zcard 同set的scard一样，返回集合的元素基数(返回元素数量)
        System.out.println("userNos中存在:"+jedis.zcard("userNos")+"个元素！");

        //zcount 语法：zcount key min max
        //释义 返回集合key的score的区间在min-max之间的元素的数量
        System.out.println("userNos中score值在2-4之间的元素数量有："+jedis.zcount("userNos",2,4)+"个！");

        //zrangebyscore 语法：zrangebyscore key min max
        //释义 返回集合key的score的区间在min-max之间的元素的值
        System.out.println("userNos中score值在2-4之间的元素为："+jedis.zrangeByScore("userNos",2,4));

        //zrange 语法 zrange key start stop [withscores]
        //释义 返回集合key的元素位置区间在start-stop之间的元素的值，withscores可选，如果加上那么元素的value联通score一同返回
        //跟zrangebyscore的区别在于，zrange是根据元素的位置来返回，而元素的位置则是按照score的值从小到大呈增，
        //而有序集合也是根据score的值来排序,而zrangebyscore则是根据指定score的区间来返回
        //有序集合中元素位置从0开始
        //下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
        //你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。
        System.out.println("userNos中位置在0-至1之间的元素为:"+jedis.zrange("userNos",0,1));
        System.out.println("userNos中位置在0至-1之间的元素为:"+jedis.zrange("userNos",0,-1));
        System.out.println("zrange userNos 0 -1 witscores命令的执行结果为："+jedis.zrangeWithScores("userNos",0,-1));

        //zincrby 为有序集 key 的成员 member 的 score 值加上增量 increment 。
        //可以通过传递一个负数值 increment ，让 score 减去相应的值，比如 ZINCRBY key -5 member ，就是让 member 的 score 值减去 5 。
        //当 key 不存在，或 member 不是 key 的成员时， ZINCRBY key increment member 等同于 ZADD key increment member 。
        System.out.println("zincrby给a01的score增加2后的score的值:"+jedis.zincrby("userNos",2,"a01"));
        System.out.println("userNos中位置在0至-1之间的元素为:"+jedis.zrange("userNos",0,-1));
        System.out.println("zincrby给a03的score减少2后的score的值:"+jedis.zincrby("userNos",-2,"a03"));
        System.out.println("userNos中位置在0至-1之间的元素为:"+jedis.zrange("userNos",0,-1));
        System.out.println("zincrby给a04的score增加4后的score的值:"+jedis.zincrby("userNos",4,"a04"));
        System.out.println("userNos中位置在0至-1之间的元素为:"+jedis.zrange("userNos",0,-1));

        //zrank 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列。
        //排名以 0 为底，也就是说， score 值最小的成员排名为 0 。
        //返回元素下标
        System.out.println("userNos中a03的下标值为："+jedis.zrank("userNos","a03"));
        System.out.println("userNos中a01的下标值为："+jedis.zrank("userNos","a01"));

        //zscore 返回有序结合中元素的score值
        System.out.println("userNos中a02的score值为:"+jedis.zscore("userNos","a02"));

        //zrem 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略
        System.out.println("移除userNos中a03前："+jedis.zrange("userNos",0,-1));
        jedis.zrem("userNos","a03");
        System.out.println("移除userNos中a03后："+jedis.zrange("userNos",0,-1));

        //zremrangebyrank 移除有序集 key 中，指定排名(rank)区间内的所有成员。
        //区间分别以下标参数 start 和 stop 指出，包含 start 和 stop 在内。
        //下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
        //你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。
        System.out.println("移除前："+jedis.zrange("userNos",0,-1));
        System.out.println("移除userNos下标在1-2之间的元素，移除个数:"+jedis.zremrangeByRank("userNos",1,2));
        System.out.println("移除后："+jedis.zrange("userNos",0,-1));

        //zremrangebyscore 移除有序集 key 中，元素的score在指定的区间的所有成员。
        jedis.zadd("userNos",1.2,"a01");
        jedis.zadd("userNos",2.9,"a04");
        jedis.zadd("userNos",3.8,"a03");
        System.out.println("移除前："+jedis.zrange("userNos",0,-1));
        jedis.zremrangeByScore("userNos",2,3);
        System.out.println("移除前："+jedis.zrange("userNos",0,-1));
        System.out.println("移除score的值是2至-1的成员："+jedis.zremrangeByScore("userNos",2,-1));
        System.out.println("移除前："+jedis.zrange("userNos",0,-1));

        //zrevrange 返回有序集 key 中，指定区间内的成员。
        //其中成员的位置按 score 值递减(从大到小)来排列。
        System.out.println(jedis.zrevrange("userNos",0,-1));

        //zrevrank 返回有序集 key 中，指定元素的位置
        //其中成员的位置按 score 值递减（从大到小）来排列
        System.out.println("倒序后，a03的下标为："+jedis.zrevrank("userNos","a03"));

        //zrevrangebyscore 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。
        System.out.println(jedis.zrevrangeByScore("userNos",10,1));

        //zinterstore 计算多个或者一个指定的有序集的交集放入到指定的有序集中
        System.out.println(jedis.zrange("userNos_new",0,-1));
        jedis.zinterstore("userNos_new","userNos");
        System.out.println(jedis.zrange("userNos_new",0,-1));
        //zunionstore 计算多个或者一个指定的有序集的并集放入到指定的有序集中，
        // 并且可以指定 weights跟aggreaget来指定将结果集的score进行乘(weights)或aggreaget指定score是sum|max|min等方式
        //给新集的元素的score赋值
        jedis.zadd("developer",3000,"dismasson");
        jedis.zadd("developer",2000,"yangwen");
        jedis.zadd("developer",1000,"lowb");
        jedis.zadd("manager",10000,"bob");

        System.out.println("公司决定给除开发人员之外的所有员工涨工资，涨幅为22%，涨幅前："+jedis.zrangeWithScores("manager",0,-1));
        ZParams zp = new ZParams();
        zp.weightsByDouble(1.22);
        jedis.zunionstore("staff",zp,"manager");
        System.out.println("公司决定给除开发人员之外的所有员工涨工资，涨幅为22%，涨幅后："+jedis.zrangeWithScores("staff",0,-1));

        //zscan 命令放在KeyCMD中讲解

    }
}
