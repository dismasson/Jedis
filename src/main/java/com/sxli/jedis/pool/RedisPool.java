/**
 * 
 */
package com.sxli.jedis.pool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis连接池-单例
 * @author sxli
 *
 */
public class RedisPool {
	/**
	 * redis连接池对象
	 */
	private static JedisPool pool = null;
	
	/**
	 * 获取jedis实例对象，内部集成redis连接池获取jedis实例
	 * @return
	 */
	public static Jedis getJedis() {
		//判断连接池对象是否为空
		if(pool == null) {
			synchronized(RedisPool.class) {
				if(pool == null) {
					/**
					 * redis连接池配置 可以配置连接池可以拥有多少jedis实例，允许同时存在多少个空闲jedis实例
					 */
					JedisPoolConfig config = new JedisPoolConfig();
					//设置连接池最多拥有多少实例
					config.setMaxTotal(5);
					//设置连接池最多拥有多少空闲jedis实例
					config.setMaxIdle(2);
					//设置等待时间，超时会出现异常
					config.setMaxWaitMillis(1);
					//实例化jedis连接池
					pool = new JedisPool(config,"47.98.116.157",6379);
				}
			}
		}
		//从连接池获取jedis对象
		return pool.getResource(); 
	}
	
	/**
	 * jedis实例回收，新版Jedis不用通过连接池去回收，jedis内部直接集成了以前pool回收的机制，直接jedis.close()即可
	 */
	public static void jedisClose(Jedis jedis) {
		jedis.close();
	}
}
