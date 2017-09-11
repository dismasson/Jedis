/**
 * 
 */
package com.sxli.jedis.pool;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import redis.clients.jedis.Jedis;

/**
 * 测试redis连接池
 * @author sxli
 *
 */
public class RedisPoolTest {

	@Test
	public void test() {
		Jedis jedis = RedisPool.getJedis();
		jedis.set("name", "sxli");
		String name = jedis.get("name");
		System.out.println(name);
		RedisPool.jedisClose(jedis);
	}
	
	@Test
	public void threadTest() throws UnsupportedEncodingException {
		/*for(int i=0;i<10;i++) {
			Runnable t = new Runnable() {
				public void run() {
					Jedis jedis = RedisPool.getJedis();
					jedis.set("name", "sxli");
					String name = jedis.get("name");
					System.out.println(jedis);
					System.out.println(name);
					RedisPool.jedisClose(jedis);
				}
			};
			t.run();
		}*/
		
		String xx="一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十";
		System.out.println(xx.getBytes("GBK").length);
	}

}
