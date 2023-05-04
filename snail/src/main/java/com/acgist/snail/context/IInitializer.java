package com.acgist.snail.context;

/**
 * 初始化接口
 * 
 * @author acgist
 */
public interface IInitializer {

	/**
	 * 同步执行初始化方法
	 */
	void sync();
	
	/**
	 * 异步执行初始化方法
	 */
	void asyn();
	
	/**
	 * 销毁方法
	 */
	void destroy();
	
}
