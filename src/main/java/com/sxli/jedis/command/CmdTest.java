/**
 * 
 */
package com.sxli.jedis.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sxli.jedis.pool.RedisPool;

import redis.clients.jedis.Jedis;

/**
 * @author sxli
 *
 */
public class CmdTest {
	
	private Jedis jedis;
	
	@Before
	public void init() {
		System.out.println("Junit测试初始化Jedis");
		jedis = RedisPool.getJedis();
	}
	
	@After
	public void destory() {
		System.out.println("Junit测试销毁Jedis");
		RedisPool.jedisClose(jedis);
	}
	
	@Test
	public void get() {
		//get 命令
		System.out.println("Redis命令之:get,根据name取值:"+jedis.get("name"));
	}
	
	@Test
	public void set_basics() {
		//Set命令基础
		System.out.println("Redis命令之:set,向name赋值:saozhuxiang");
		jedis.set("name", "saozhuxiang");
	}
	
	@Test
	public void set_thorough() throws InterruptedException {
		//Set命令深入
		System.out.println("查看之前普通set命令插入的name在缓存中保存的时间:"+(jedis.ttl("name") == -1? "无限" : jedis.ttl("name")));
		
		//Redis set key value [nx(不存在) | xx(存在)] [ex(秒) | px(毫秒)] number
		System.out.println("Redis set插入，必须在缓存中不存在key才可以插入成功，插入结果:"+(jedis.set("name", "saozhuxiang","nx") == null ? "插入失败，原因是缓存中已经存在key" : "插入成功"));
		System.out.println("Redis set插入，必须在缓存中存在key才可以插入成功，插入结果:"+(jedis.set("weight", "82KG" ,"xx") ==null ? "插入失败，失败原因是缓存中没有这个key" : "插入成功"));
		System.out.println("Redis set插入，设置超时时间(秒):"+(jedis.set("height","175cm","nx","ex",3L) != null ? "插入成功" : "插入失败"));
		System.out.println("Redis set插入后设置超时时间:"+(jedis.ttl("height") ==-1 ? "永久" : jedis.ttl("height")));
		//设置阻塞
		Thread.sleep(3000);
		System.out.println("线程阻塞3s后查看超时时间为3s的height是否还存在:"+jedis.get("height"));
		System.out.println("Redis set插入，设置超时时间(毫秒):"+(jedis.set("height", "175cm" , "nx" ,"px", 3000) != null ? "插入成功" : "插入失败"));
		System.out.println("Redis set插入后设置超时时间:"+(jedis.ttl("height") == -1 ? "永久" : jedis.ttl("height")));
		//阻塞线程
		Thread.sleep(3000);
		System.out.println("线程阻塞3000ms后查看超时时间为3000ms的height是否还存在:"+jedis.get("height"));
	}

	@Test
	public void append() {
		//Append命令
		System.out.println("Redis append命令追加:append name 'lihai'");
		System.out.println("append name 'lihai'追加后的长度为:"+jedis.append("name", "lihai"));
		System.out.println("Redis append命令在指定的key不存在的时候就创建一个为空的key然后在后面追加!");
		System.out.println("Redis get notto:"+jedis.get("motto"));
		System.out.println("Redis append motto '人不能因为一点困难就降低自己的要求!:"+jedis.append("motto", "人不能因为一点困难就降低自己的要求!"));
		System.out.println("Redis get notto:"+jedis.get("motto"));
		System.out.println("Redis ttl motto:"+jedis.ttl("motto"));
		System.out.println("Redis 时间序列 fixed-size sample:"+jedis.append("motto", "fixed-size sample"));
		System.out.println("Redis get notto:"+jedis.get("motto"));
	}
	
	@Test
	public void append_fixed_size_sample() {
		//Append 时间序列用法 fixed-size固定大小 sample 样本
		System.out.println("Redis中，如果某些值的长度是一样的长度，如果分别存放会消耗内存，通过时间序列的方式来放入一条数据中，按照时间序列去分别取值!");
		jedis.append("timeseries", "1234");
		jedis.append("timeseries", "5678");
		System.out.println(jedis.getrange("timeseries", 0, 3));
		System.out.println(jedis.getrange("timeseries", 4, 7));
	}
}
