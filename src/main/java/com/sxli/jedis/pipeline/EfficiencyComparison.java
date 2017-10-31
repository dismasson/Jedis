package com.sxli.jedis.pipeline;

import com.sxli.jedis.pool.RedisPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

/**
 * Redis 执行命令效率对比
 * 1、不用事务查看执行命令效率
 * 2、增加事务查看执行命令效率
 * 3、不用事务并且用pipeline查看执行命令效率
 * 4、尝试在pipeline中增加事务查看效率
 * 5、命令中增加乐观锁查看执行命令效率
 * 6、pipeline中增加命令并且添加乐观锁查看命令执行效率
 */
public class EfficiencyComparison {

    private Jedis jedis;

    @Before
    public void init() {
        jedis = RedisPool.getJedis();

    }

    @After
    public void destory() {
        RedisPool.jedisClose(jedis);
    }

    /**
     * 1、不使用事务查看Redis执行1万个写入命令的效率
     */
    @Test
    public void noTransaction(){
        //开始时间
        Long beginDate = System.currentTimeMillis();

        for(int i=0;i<10000;i++){
            jedis.set("n"+i,i+"");
        }

        //结束时间
        Long endDate = System.currentTimeMillis();

        System.out.println("耗时:"+(endDate-beginDate)+"毫秒");
    }

    /**
     * 2、使用事务查看Redis执行1万个写入命令的效率
     */
    @Test
    public void transaction(){
        //开启事务
        Transaction transaction = jedis.multi();

        //开始时间
        Long beginDate = System.currentTimeMillis();

        for(int i=0;i<10000;i++){
            transaction.set("t"+i,i+"");
        }

        //提交事务
        transaction.exec();

        //结束时间
        Long endDate = System.currentTimeMillis();

        System.out.println("耗时:"+(endDate-beginDate)+"毫秒");
    }

    /**
     * 3、使用pipeline查看Redis执行1万个写入命令的效率
     */
    @Test
    public void pipeline(){
        //使用pipeline
        Pipeline pipeline = jedis.pipelined();

        //开始时间
        Long beginDate = System.currentTimeMillis();

        for(int i=0;i<10000;i++){
            pipeline.set("n"+i,i+"");
        }
        //结束时间
        Long endDate = System.currentTimeMillis();

        System.out.println("耗时:"+(endDate-beginDate)+"毫秒");
    }

    /**
     * 4、使用pipeline查看Redis执行1万个写入命令的效率
     */
    @Test
    public void pipelineandTransaction(){
        //使用pipeline
        Pipeline pipeline = jedis.pipelined();
        //开启事务
        pipeline.multi();
        //开始时间
        Long beginDate = System.currentTimeMillis();

        for(int i=0;i<10000;i++){
            pipeline.set("n"+i,i+"");
        }
        //提交事务
        pipeline.exec();
        //结束时间
        Long endDate = System.currentTimeMillis();

        System.out.println("耗时:"+(endDate-beginDate)+"毫秒");
    }

    /**
     * 5、使用事务查看Redis执行1万个写入命令的效率
     */
    @Test
    public void transactionandWatch(){
        //开启事务
        Transaction transaction = jedis.multi();

        //开始时间
        Long beginDate = System.currentTimeMillis();

        for(int i=0;i<10000;i++){
            transaction.watch("t"+i);
            transaction.set("t"+i,i+"");
        }

        //提交事务
        transaction.exec();

        //结束时间
        Long endDate = System.currentTimeMillis();

        System.out.println("耗时:"+(endDate-beginDate)+"毫秒");
    }

    /**
     * 6、使用pipeline查看Redis执行1万个写入命令的效率
     */
    @Test
    public void pipelineandTransactionandWatch(){
        //使用pipeline
        Pipeline pipeline = jedis.pipelined();
        //开启事务
        pipeline.multi();
        //开始时间
        Long beginDate = System.currentTimeMillis();

        for(int i=0;i<10000;i++){
            pipeline.watch("n"+i);
            pipeline.set("n"+i,i+"");
        }
        //提交事务
        pipeline.exec();
        //结束时间
        Long endDate = System.currentTimeMillis();

        System.out.println("耗时:"+(endDate-beginDate)+"毫秒");
    }

    /**
     * 测试多个命令组合在一起并且使用事务
     * 因为事务是要么执行成功，要么执行不成功，所以如果其中某个命令插入的时候出现异常，会导致该事务所有命令都是会提交失败，所以不推荐使用批量命令组合在一个事务中提交
     */
    @Test
    public void mergeCMD() throws Exception {
        //开启事务
        Transaction transaction = jedis.multi();

        for(int i=0;i<10000;i++){
            transaction.set("mergeCMD"+i,i+"");
            if(i==8888){
                throw new Exception("人为制造异常!");
            }
        }

        //提交事务
        transaction.exec();
    }
}
