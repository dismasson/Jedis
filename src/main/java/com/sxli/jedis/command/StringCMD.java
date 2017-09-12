package com.sxli.jedis.command;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

/**
 * Redis数据类型：String 命令练习
 */
public class StringCMD {

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
     * 测试Redis String数据类型相关 命令
     */
    @Test
    public void StringCMDTest() throws InterruptedException {
        /*
         * set 命令基础
         */
        String key="name";
        String value="dismasson";

        //set命令，如果仅仅指定key跟value，它有默认的配置
        jedis.set(key,value);
        System.out.println("\n"+"获取插入的值：key="+key+"\tvalue="+jedis.get(key)+"\n");

        /*
         * set 命令深入
         */
        key = "age";
        value = "27";

        //执行set命令
        jedis.set(key,value);
        //执行incr命令，incr命令在redis中作用是自增+1并且返回自增+1后的值
        System.out.println("自增前：key="+key+"\tvalue="+jedis.get(key));
        System.out.println("自增后：key="+key+"\tvalue="+jedis.incr(key)+"\n");
        //执行decr命令，decr命令在redis中作用是自减-1并且返回自减-1后的值
        System.out.println("自减前: key="+key+"\tvalue="+jedis.get(key));
        System.out.println("自减后: key="+key+"\tvalue="+jedis.decr(key)+"\n");
        //执行incrby命令，incrby命令在redis中作用是给指定的key的值加上指定的value，这个value只能是整数类型的数据，
        //并且之前key储存的value也只能是整数类型的，不然会出现异常
        System.out.println("增加指定数字前：key="+key+"\tvalue="+jedis.get(key));
        System.out.println("增加指定数字2后：key="+key+"\tvalue="+jedis.incrBy(key,2)+"\n");
        try {
            System.out.println("增加指定数字5后：key=name\tvalue="+jedis.incrBy("name",5)+"\n");
        }catch (JedisDataException e){
            System.out.println("给redis中的key=name执行incrby命令给他指定+5后出现异常，异常信息如下:"+e.getMessage()+"\n");
        }
        //执行incrbyfloat命令，incrbyfloat命令是给指定的key的value加上指定的非整数的数值value
        System.out.println("增加指定小数前:key="+key+"\tvalue="+jedis.get(key));
        System.out.println("增加指定小数0.22后:key="+key+"\tvalue="+jedis.incrByFloat(key,0.22));
        System.out.println("增加指定小数0.8后:key="+key+"\tvalue="+jedis.incrByFloat(key,0.8)+"\n");
        //decrby，对指定key进行减少指定的value
        System.out.println("减少指定数前：key="+key+"\tvalue="+jedis.get(key)+"\n");
        try {
            jedis.decrBy(key,2);
        }catch (JedisDataException e){
            System.out.println("给redis中的key="+key+"\tvalue="+jedis.get(key)+"执行decrby命令给他指定-2后出现异常，异常信息如下:"+e.getMessage());
            System.out.println("非整数类型的不能使用decr或者decrby，当我们给key="+key+"重新复制让它成为一个整数的时候，decrby+2操作就成功了!"+"\n");
            jedis.set("age","27");
            jedis.decrBy(key,2);
        }
        System.out.println("减少指定数后：key="+key+"\tvalue="+jedis.get(key)+"\n");
        //append命令在redis中是给指定的key追加value，如果指定的key不存在，就创建一个key的value为空的键值对，然后对这个key的value进行追加value,append命令执行后会返回追加后value的长度
        key = "sex";
        value = "man";
        System.out.println("给一个不存在的key进行追加值前: key="+key+"\tvalue="+jedis.get(key));
        jedis.append(key,value);
        System.out.println("给一个不存在的key进行追加值后: key="+key+"\tvalue="+jedis.get(key));
        System.out.println("给一个存在的key进行增加前: key=name\t+value="+jedis.get("name"));
        jedis.append("name","_01");
        System.out.println("给一个存在的key进行增加后: key=name\t+value="+jedis.get("name")+"\n");

        key = "name";

        //strlen命令在Redis中是获取指定key的value的长度
        System.out.println("使用strlen命令获取指定key="+key+"的value的长度为:"+jedis.strlen(key));
        try {
            System.out.println("----------测试非String类型的命令执行strlen命令是否异常----------");
            //sadd 是Redis中给Set类型的key赋值，如果可不存在就新建一个key并且给key赋值
            jedis.sadd("names","dismasson");
            System.out.println("使用strlen命令获取指定key=names\tvalue的长度为:"+jedis.strlen("names"));
        }catch (JedisDataException e){
            System.out.println("异常了，测试得到strlen只能获取String类型的key的value长度!");
        }
        //setrange命令在Redis中是替换String类型的key的value，指定从(offset)开始替换，后面所有value替换成指定的value,
        // 注意，当我们之前的value的值为dismasson_01的时候，如果执行setrange(key,9,""），最后key的值还是dismasson_01
        // 原因是setrange指定从offset后开始替换，替换的长度就是指定的value的长度，像是setrange(key,startoffset,endoffset,value)
        // 即:给指定的key从startoffset处开始替换，到endoffset处停止(startoffset-endoffset的长度就是value.lenth)，这个区间的值用value来替代
        System.out.println("\n"+"使用setrange命令替换value前，key="+key+"\tvalue="+jedis.get(key));
        System.out.println(jedis.setrange(key,9,"_hello"));
        System.out.println("使用setrange命令替换value后，key="+key+"\tvalue="+jedis.get(key));
        //getrange命令在Redis中是获取指定的key的长度在startoffset到endoffset之间的value，startoffset的值从0开始
        System.out.println("\n"+"使用getrange命令获取key="+key+"\tvalue="+value+"的String类型的键值对的值，指定为从0开始至2结束:"+jedis.getrange(key,0,2));

        value = "dismasson";
        //setnx命令是在指定的key不存在的情况下进行添加，如果存在返回0
        System.out.println("\n"+"使用setnx命令在Redis中新增String类型的键值对，key="+key+"\tvalue="+value+",新增结果:"+(jedis.setnx(key,value) == 0 ? "新增失败，Redis中已经存在key="+key : "新增成功!"));

        key ="name2";

        //保险起见，删除Redis中的key=name2的数据
        jedis.del(key);

        System.out.println("\n"+"使用setnx命令在Redis中新增String类型的键值对，key="+key+"\tvalue="+value+",新增结果:"+(jedis.setnx(key,value) == 0 ? "新增失败，Redis中已经存在key="+key : "新增成功!"));

        //setex命令在Redis中可以给key-value设置过期时间，单位为秒，过期后Redis自动清除key
        jedis.setex(key,2,value);

        try {
            System.out.println("线程停止两.5秒后。。。");
            //线程停止两秒测试是否能通过get去获取key的value
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n"+"使用get命令在Redis中获取key="+key+"的value="+jedis.get(key));

        /*
         * set命令 set(key,value,xxnx,expx,timeout)
         * nx同setnx，即如果指定的key不存在就新增，xx是必须之前存在这个key才能新增
         * ex跟px是指定timeout的单位，ex单位是秒，同setex，px单位是毫秒
         */

        int timeout = 10;

        System.out.println("在Redis中新增一条以前不存在的数据，key=timeout\tvalue=timeout，设置它的过期时间为:"+timeout+"秒!");
        jedis.set("timeout","timeout","nx","ex",10);

        long timeout2 = 20000;

        System.out.println("在Redis中修改key=timeout\tvalue=timeout,修改它的过期时间为:"+timeout2+"毫秒!");
    }
}
