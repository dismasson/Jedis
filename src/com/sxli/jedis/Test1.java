/**
 * 
 */
package com.sxli.jedis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;

/**
 * 简易测试下redis功能
 * @author sxli
 *
 */
public class Test1 {
	
	Jedis jedis = null;
	
	@Before
	public void before() {
		//初始化
		jedis = new Jedis();
	}
	
	@After
	public void after() {
		//调试结束
		jedis.close(); 
	}

	@Test
	public void test() {
		System.out.println(jedis.set("name","sxli"));
	}

}
