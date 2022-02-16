/**
 * 
 */
package com.yusys.bione.frame.message.messager;

/**
 * @author	tanxu
 * @email	tanxu@yuchengtech.com
 * @date	2014-1-20
 */
public interface IMessagerFactory<M, E> {
	
	IMessager<M, E> getMessager(String type);
}
