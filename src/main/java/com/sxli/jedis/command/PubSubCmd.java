package com.sxli.jedis.command;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * Redis对于Pub/Sub的一系列操作命令
 * 发布/订阅
 */
public class PubSubCmd {

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
    public void PubSubCMDTest(){
        /*
          publish channel message
          将 message 发送到指定的 channel 。
         */
        System.out.println("当前在监听channel的客户端有："+jedis.publish("channel","message"));

        //MyPubSub pubSub = new MyPubSub();

        /*
          subscribe channel
          订阅给定的一个或多个 channel 的 message
         */
        /*jedis.subscribe(pubSub,"channel");
        jedis.publish("channel","message");
        System.out.println("当前在监听channel的客户端有："+jedis.publish("channel","message"));*/

        /*
          psubscribe
          订阅给定的一个或多个符合给定模式的 channel
          如：psubscribe a* b* 就是代表订阅所有以a或者b开头的 channel
         */

        /*
          punsubscribe
          退订当前客户端所订阅为指定模式的 channel 如果不指定模式，就退订所有的
         */

        /*
          unsubscribe
          退订当前客户端订阅的一个或者多个渠道
         */

        /*
          pubsub 是一个查看订阅与发布系统状态的内省命令， 它由数个不同格式的子命令组成， 以下将分别对这些子命令进行介绍
          pubsub channels 列出当前的活跃频道。活跃频道指的是那些至少有一个订阅者的频道， 订阅模式的客户端不计算在内。
          pubsub numsub 返回给定频道的订阅者数量， 订阅模式的客户端不计算在内。
          pubsub numpat 返回订阅模式的数量。注意， 这个命令返回的不是订阅模式的客户端的数量， 而是客户端订阅的所有模式的数量总和。
         */
        System.out.println(jedis.pubsubChannels(""));
    }
}
