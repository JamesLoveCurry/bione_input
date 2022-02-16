/**
 * 
 */
package com.yusys.bione.frame.message.messager;



/**
 * @author	tanxu
 * @email	tanxu@yuchengtech.com
 * @date	2013-12-12
 */
public interface IMessager<M, E> {

	boolean send(M msg) throws BioneMessageException ;
	
	boolean send(M msg, Iterable<E> attaches) throws BioneMessageException ;
	
}
