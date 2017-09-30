package com.sxli.jedis.command;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * Redis中事务处理的相关命令
 */
public class TransactionCMD {

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
    public void test1(){
        /*
            出现NullPointexception
         */
        jedis.watch("key");
        Transaction transaction = jedis.multi();
        transaction.set("key","value");
        jedis.unwatch();
        transaction.exec();
    }

    @Test
    public void test2(){
        /*
            出现NullPointexception
         */
        jedis.watch("key");
        Transaction transaction = jedis.multi();
        jedis.unwatch();
        transaction.set("key","value");
        transaction.exec();
    }

    @Test
    public void test3(){
        /*
            未曾出现NullPointexception
         */
        jedis.watch("key");
        jedis.unwatch();
        Transaction transaction = jedis.multi();
        transaction.set("key","value");
        transaction.exec();
    }

    @Test
    public void TransactionsTest(){
        /*
          multi 标记一个事务块的开始。 随后的指令将在执行 exec 时作为一个原子执行。
         */
        Transaction transaction = jedis.multi(); //开启一个事务
        transaction.set("testTransaction","hello"); //执行一个写入命令，如果后面没有执行 exec ，写入就不生效

        /*
          exec 执行事务中所有在排队等待的指令并将链接状态恢复到正常
         */
        transaction.exec();

        /*
          watch 监视一个或者多个key，如果在事务执行之前这个(或这些) key 被其他命令所改动，那么事务将被打断
          注意：watch 必须在 multi 跟 exec 或者是 discard之前使用，否则会出现异常或者无效
         */
        //测试不用watch 监视，如果这个时候 key 被其他客户端修改后，新提交的 key 的值会覆盖
        Transaction t1 = jedis.multi();
        System.out.println("修改 key：name的 value 为：hello1");
        t1.set("name","hello1");
        //try {
            System.out.println("阻塞线程10秒钟让我们有时间在别的客户端给name插入新的值,打开一个新的客户端并且给key name赋值 hello2");
            //Thread.sleep(10000);
       // } catch (InterruptedException e) {
            //e.printStackTrace();
        //}
        t1.exec();
        System.out.println("经过测试发现，当执行完 exec 后，name的值被覆盖了");

        //测试使用 watch 监视，可以在监视的key被别的客户端修改后，事务将会被打断
        jedis.watch("name2");
        Transaction t2 = jedis.multi();
        System.out.println("修改 key：name2的 value 为：hello3");
        t2.set("name2","hello3");
        try {
            System.out.println("阻塞线程10秒钟让我们有时间在别的客户端给name2插入新的值,打开一个新的客户端并且给key name2赋值 hello2");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t2.exec();
        System.out.println("经过测试发现，当执行完 exec 后，name2的值并没有被覆盖");
    }

    @Test
    public void unwatch(){
        /*
          unwatch 取消 watch 命令对所有 key 的监视
          注意：如果事务提交 exec 或者 事务取消 discard，那么久不需要执行 unwatch 了，因为在事务提交或者事务取消的同时并且也会
          取消对 key 的监视，这个时候就没有必要 取消监视了。
          发现一个问题：unwatch在官方文档中说明在执行exec 或者 discard之后执行没有意义，但是放在 multi 之后，exec之前
          竟然在Jedis中出现NullPointException，jedis版本为：2.9.0
         */
        jedis.watch("name3");
        Transaction transaction = jedis.multi();
        System.out.println("修改 key：name3的 value 为：hello1");
        transaction.set("name3","hello1");
        System.out.println("阻塞线程10秒钟让我们有时间在别的客户端给name3插入新的值,打开一个新的客户端并且给key name3赋值 hello2");
        jedis.unwatch();
       /* try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        transaction.exec();
        System.out.println("经过测试发现，当执行完 exec 后，name3的值被覆盖了");
    }

    @Test
    public void discard(){
        /*
          discard 取消事务，放弃执行事务块内的所有命令。
          注意：不能跟 exec 同时使用，使用了 discard 就不能 exec

         */
        Transaction transaction = jedis.multi();
        transaction.set("key","value2");
        transaction.discard();
        //transaction.exec();
    }
}
