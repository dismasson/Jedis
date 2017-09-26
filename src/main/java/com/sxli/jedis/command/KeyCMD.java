package com.sxli.jedis.command;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

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
         当且仅当 newkey 不存在时，将 key 改名为 newkey 。当 key 存在时，返回一个错误。
         */
        jedis.set("pc","mechrevo");
        System.out.println("pc2是否存在："+jedis.exists("pc2"));
        jedis.renamenx("pc","pc2");
        System.out.println("pc2是否存在："+jedis.exists("pc2"));

        /*randomkey
         从当前数据库中随机返回(不删除)一个 key 。
         */
        System.out.println("随机抽取当前数据库的一个key："+jedis.randomKey());

        /*
         dump 跟 restore 序列化跟反序列化
         dump+restore配合使用可以在不同的redis实例之间复制数据，但是操作是分开的，不是原子性操作
         dump序列化包含value、type，但是不包含生存时间，所以dump序列化后的值，要反序列化到key，则需要另外设置ttl时间
         restore ttl参数如果是0就不设置生存时间，就是永久存在
         */
        //新建一个list类型的key并且储存值
        jedis.del("nums");
        jedis.del("nums_new");
        jedis.rpush("nums","1");
        jedis.rpush("nums","2");
        jedis.rpush("nums","4");
        jedis.rpush("nums","4");
        //dump nums返回序列化后的值然后restore反序列化到nums_new
        jedis.restore("nums_new",0,jedis.dump("nums"));
        System.out.println("nums_new的值："+jedis.lrange("nums_new",0,-1)+
                "\tnums_new的有效时间还有："+jedis.ttl("nums_new")
                +"\tnums_new的类型是："+jedis.type("nums_new"));

        /*migrate
         将 key 原子性地从当前实例传送到目标实例的指定数据库上，一旦传送成功，
         key 保证会出现在目标实例上，而当前实例上的 key 会被删除。
         这个命令是一个原子操作，它在执行的时候会阻塞进行迁移的两个实例，直到以下任意结果发生：迁移成功，迁移失败，等到超时。
         命令的内部实现是这样的：它在当前实例对给定 key 执行 DUMP 命令 ，将它序列化，然后传送到目标实例，目标实例再使用
         RESTORE 对数据进行反序列化，并将反序列化所得的数据添加到数据库中；当前实例就像目标实例的客户端那样，只要看到 RESTORE
         命令返回 OK ，它就会调用 DEL 删除自己数据库上的 key 。
         timeout 参数以毫秒为格式，指定当前实例和目标实例进行沟通的最大间隔时间。这说明操作并不一定要在 timeout 毫秒内完成，
         只是说数据传送的时间不能超过这个 timeout 数。
         MIGRATE 命令需要在给定的时间规定内完成 IO 操作。如果在传送数据时发生 IO 错误，或者达到了超时时间，那么命令会停止执行，
         并返回一个特殊的错误： IOERR 。
         当 IOERR 出现时，有以下两种可能：
         key 可能存在于两个实例
         key 可能只存在于当前实例
         唯一不可能发生的情况就是丢失 key ，因此，如果一个客户端执行 MIGRATE 命令，并且不幸遇上 IOERR 错误，那么这个客户端唯一
         要做的就是检查自己数据库上的 key 是否已经被正确地删除。
         如果有其他错误发生，那么 MIGRATE 保证 key 只会出现在当前实例中。（当然，目标实例的给定数据库上可能有和 key 同名的键，
         不过这和 MIGRATE 命令没有关系）。
         dump+restore+del的组合
         注意：当前使用的是jedis，这是redis 的一个java的客户端，对于migrate这个方法的支持，目前没有发现 [COPY] [REPLACE]
         copy：仅仅只是进行复制，复制完毕后不移除源实例中的key
         replace：如果目标实例中的数据库已经存在这个key，replace就指定替换掉这个key
         timeout参数单位是以毫秒来计算的
         */
        try {
            jedis.migrate("localhost",7001,"nums_new",0,200);
        }catch(JedisDataException e){
            System.out.println("出现异常，异常信息打印如下："+e.getMessage());
        }

        /*
         object 命令允许从内部察看给定 key 的 Redis 对象。
         可选参数：
            encoding：返回指定 key 的编码格式
            idletime：返回给定 key 自储存以来的空转时间(idle， 没有被读取也没有被写入)，以秒为单位。
            refcount：返回给定 key 引用所储存的值的次数。
         redis中的编码格式跟底层数据结构值得我们深入了解，这样才能更好的使用它，在做排错以及优化时，我们才能做到有的放矢
         */
        System.out.println("nums_new的编码类型为："+jedis.objectEncoding("nums_new"));
        System.out.println("查看nums_new自创建以来多久没有被使用过："+jedis.objectIdletime("nums_new")+"秒");
        System.out.println("查看nums_new创建以来被引用过多少次："+jedis.objectRefcount("nums_new"));

        /* 官方说明：
            scan SCAN 命令及其相关的 SSCAN 命令、 HSCAN 命令和 ZSCAN 命令都用于增量地迭代（incrementally iterate）
            一集元素（a collection of elements）：
            SCAN 命令用于迭代当前数据库中的数据库键。
            SSCAN 命令用于迭代集合键中的元素。
            HSCAN 命令用于迭代哈希键中的键值对。
            ZSCAN 命令用于迭代有序集合中的元素（包括元素成员和元素分值）。
            以上列出的四个命令都支持增量式迭代， 它们每次执行都只会返回少量元素， 所以这些命令可以用于生产环境，
            而不会出现像 KEYS 命令、 SMEMBERS 命令带来的问题 —— 当 KEYS 命令被用于处理一个大的数据库时，
            又或者 SMEMBERS 命令被用于处理一个大的集合键时， 它们可能会阻塞服务器达数秒之久。
            不过， 增量式迭代命令也不是没有缺点的： 举个例子， 使用 SMEMBERS 命令可以返回集合键当前包含的所有元素，
            但是对于 SCAN 这类增量式迭代命令来说， 因为在对键进行增量式迭代的过程中， 键可能会被修改， 所以增量式迭代命令只能对被返回的元素提供有限的保证 （offer limited guarantees about the returned elements）。
            因为 SCAN 、 SSCAN 、 HSCAN 和 ZSCAN 四个命令的工作方式都非常相似， 所以这个文档会一并介绍这四个命令， 但是要记住：
            SSCAN 命令、 HSCAN 命令和 ZSCAN 命令的第一个参数总是一个数据库键。
            而 SCAN 命令则不需要在第一个参数提供任何数据库键 —— 因为它迭代的是当前数据库中的所有数据库键。
         */

        /*
            sort返回或保存给定列表、集合、有序集合 key 中经过排序的元素。
            排序默认以数字作为对象，值被解释为双精度浮点数，然后进行比较。
         */

        // TODO:https://yq.aliyun.com/articles/63461 Redis数据编码方式详解
        // TODO:https://segmentfault.com/a/1190000009915519 Redis高级功能 - 慢查询日志
    }
}
