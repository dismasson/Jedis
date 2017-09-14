package com.sxli.jedis.command;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

/**
 * Redis数据类型：List 命令练习
 */
public class ListCMD {

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
     * 测试Redis List数据类型相关命令
     */
    @Test
    public void ListCMDTest(){
        jedis.del("names");
        String key ="names";
        /*
         * List中插入数据
         */
        //lpush 命令是给这个List类型的key中最左边插入value
        jedis.lpush(key,"dismasson");
        //rpush 命令是给这个List类型的key中最右边插入value
        jedis.rpush(key,"wenyang");
        System.out.println(jedis.lrange(key,0,4));
        //lset 命令给指定的key的下标的value重新赋值
        System.out.println(jedis.lset(key,1,"wenwen"));
        try {
            System.out.println(jedis.lset(key,2,"wenwen"));
        }catch (JedisDataException e){
            System.out.println("出现异常，异常信息如下:"+e.getMessage());
        }
        //lrem 命令是删除List中储存的数据，具体删除位置是从指定的下标数开始删到尾部，删除内容是指定的value
        System.out.println(jedis.lrem(key,1,"wenwen"));
        //llen 命令是获取List的长度
        System.out.println("获取key="+key+"的List里面的长度为："+jedis.llen(key));
        //lpop 命令是在List中最左边弹出一个value，弹出后会返回弹出value并且删除弹出的value
        jedis.lpush(key,"new_dismasson");
        jedis.lpush(key,"new_wenwen");
        System.out.println("弹出前key="+key+"的长度为："+jedis.llen(key));
        System.out.println("弹出值为："+jedis.lpop(key));
        System.out.println("弹出后key="+key+"的长度为："+jedis.llen(key));
        //rpop 命令是在List中左右边弹出一个value，弹出后会返回弹出value并且删除弹出的value
        System.out.println("演出前key="+key+"的长度为："+jedis.llen(key));
        System.out.println("弹出值为："+jedis.rpop(key));
        System.out.println("弹出后key="+key+"的长度为："+jedis.llen(key));
        //lindex 命令是在lIst根据下标回去value
        System.out.println("根据下标为0获取到的数据为："+jedis.lindex(key,0));

        jedis.lpush(key,"wenyang");

        //linsert 命令指定在List中value的前后插入指定的value，如果value不存在，就不做任何操作
        System.out.println("linsert命令在List中names的wenyang前面插入hello这个value，执行结果为:" +
                ""+(jedis.linsert(key, BinaryClient.LIST_POSITION.BEFORE,"wenyang","hello") == -1 ? "插入不成功，原因是没有这个value" : "插入成功"));

        System.out.println("新插入后数据："+jedis.lrange(key,0,-1));

        //ltrim 命令是指定key只保留 start - end 之间的数据，就是裁剪
        System.out.println("裁剪前数据为："+jedis.lrange(key,0,-1));
        jedis.ltrim(key,1,10);
        System.out.println("裁剪后数据位："+jedis.lrange(key,0,-1));

        //lpushx | rpushx 命令跟lpush | rpush命令完全不同，如果指定的key不存在就不做操作
        System.out.println("lpushx 操作结果:"+(jedis.lpushx("12","dismasson") ==0 ? "操作无视" : "操作成功，修改后的数值为："+jedis.lrange(key,0,-1)));
        System.out.println("lpushx 操作结果:"+(jedis.rpushx(key,"dismasson") ==0 ? "操作无视" : "操作成功，修改后的数值为："+jedis.lrange(key,0,-1)));

        /*
         *List 阻塞
         */
        //blpop | brpop 命令同lpop | rpop 作用同意是弹出数据，但是blpop | brpop如果指定的key没有元素可供弹出的时候会阻塞连接，
        // 如果一直没有提供可供弹出的元素，只有当连接断开或者设置了超时时间才会释放
        System.out.println("弹出前值为："+jedis.lrange(key,0,-1));
        jedis.blpop(10,key);
        System.out.println("弹出后值为："+jedis.lrange(key,0,-1));
        jedis.blpop(10,key);
        System.out.println("弹出后值为："+jedis.lrange(key,0,-1));
        jedis.brpop(10,key);
        System.out.println("弹出后值为："+jedis.lrange(key,0,-1));
        jedis.brpop(10,key);
        System.out.println("弹出后值为："+jedis.lrange(key,0,-1));

        //rpoplpush | brpoplpush 命令的作用是从一个List中最右边的值弹出并且写入到
        // 另外一个List中，rpoplpush跟brpoplpush的区别在于一个是阻塞的

        /*  官方文档说明
        Redis的列表经常被用作队列(queue)，用于在不同程序之间有序地交换消息(message)。
        一个客户端通过 LPUSH 命令将消息放入队列中，而另一个客户端通过 RPOP 或者 BRPOP 命令取出队列中等待时间最长的消息。

        不幸的是，上面的队列方法是『不安全』的，因为在这个过程中，一个客户端可能在取出一个消息之后崩溃，而未处理完的消息也就因此丢失。

        使用 RPOPLPUSH 命令(或者它的阻塞版本 BRPOPLPUSH )可以解决这个问题：因为它不仅返回一个消息，同时还将这个消息添加到另一个
        备份列表当中，如果一切正常的话，当一个客户端完成某个消息的处理之后，可以用 LREM 命令将这个消息从备份表删除。
        最后，还可以添加一个客户端专门用于监视备份表，它自动地将超过一定处理时限的消息重新放入队列中去(负责处理该消息的客户端可能已经崩溃)，这样就不会丢失任何消息了。
         */
    }
}
