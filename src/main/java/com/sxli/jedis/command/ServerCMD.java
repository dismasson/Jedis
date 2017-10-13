package com.sxli.jedis.command;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * Redis中Server相关命令
 */
public class ServerCMD {

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
    public void bgrewriteaof(){
        /*
            执行一个 AOF文件 重写操作。重写会创建一个当前 AOF 文件的体积优化版本。
            即使 BGREWRITEAOF 执行失败，也不会有任何数据丢失，因为旧的 AOF 文件在 BGREWRITEAOF 成功之前不会被修改。
            重写操作只会在没有其他持久化工作在后台执行时被触发，也就是说：
            如果 Redis 的子进程正在执行快照的保存工作，那么 AOF 重写的操作会被预定(scheduled)，等到保存工作完成之后再执行
            AOF 重写。在这种情况下， BGREWRITEAOF 的返回值仍然是 OK ，但还会加上一条额外的信息，说明 BGREWRITEAOF
            要等到保存操作完成之后才能执行。在 Redis 2.6 或以上的版本，可以使用 INFO 命令查看 BGREWRITEAOF 是否被预定。
            如果已经有别的 AOF 文件重写在执行，那么 BGREWRITEAOF 返回一个错误，并且这个新的 BGREWRITEAOF
            请求也不会被预定到下次执行。
            从 Redis 2.4 开始， AOF 重写由 Redis 自行触发， BGREWRITEAOF 仅仅用于手动触发重写操作。
         */
        System.out.println(jedis.bgrewriteaof());
    }

    @Test
    public void bgsave(){
        /*
           在后台异步(Asynchronously)保存当前数据库的数据到磁盘。
           BGSAVE 命令执行之后立即返回 OK ，然后 Redis fork 出一个新子进程，原来的 Redis 进程(父进程)继续处理客户端请求，
           而子进程则负责将数据保存到磁盘，然后退出。
           客户端可以通过 LASTSAVE 命令查看相关信息，判断 BGSAVE 命令是否执行成功。
         */
        System.out.println(jedis.bgsave());
    }

    @Test
    public void clientsetname(){
        /*
        为当前连接分配一个名字。
        这个名字会显示在 CLIENT LIST 命令的结果中， 用于识别当前正在与服务器进行连接的客户端。
        举个例子， 在使用 Redis 构建队列（queue）时， 可以根据连接负责的任务（role）， 为信息生产者（producer）和信息消费者（consumer）分别设置不同的名字。
        名字使用 Redis 的字符串类型来保存， 最大可以占用 512 MB 。 另外， 为了避免和 CLIENT LIST 命令的输出格式发生冲突， 名字里不允许使用空格。
        要移除一个连接的名字， 可以将连接的名字设为空字符串 "" 。
        使用 CLIENT GETNAME 命令可以取出连接的名字。
        新创建的连接默认是没有名字的。
        在 Redis 应用程序发生连接泄漏时，为连接设置名字是一种很好的 debug 手段。
         */
        jedis.clientSetname("hello");
    }

    public void clientsetname(Jedis jedis){
        jedis.clientSetname("hello");
    }

    @Test
    public void clientgetname(){
        /*
        返回 CLIENT SETNAME 命令为连接设置的名字。
        因为新创建的连接默认是没有名字的， 对于没有名字的连接， CLIENT GETNAME 返回空白回复。
         */
        System.out.println("\n给客户端连接设置名字前："+jedis.clientGetname());
        clientsetname(jedis);
        System.out.println("\n给客户端连接设置名字后："+jedis.clientGetname());
    }
}
