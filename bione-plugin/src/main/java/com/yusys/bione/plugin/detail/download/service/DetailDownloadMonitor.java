package com.yusys.bione.plugin.detail.download.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/***
 *
 * <pre>
 * Title: 任务状态监视器,单例模式!
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date  2020/8/24
 */

public class DetailDownloadMonitor {
	// 静态实例!!
	private static DetailDownloadMonitor monitor = null;

	// 创建报文的主线程是否在运行!
	public boolean isCreateFileMainThreadRuning = false;

	// 生成报文的任务队列,必须使用线程案例的队列对象!
	private ConcurrentLinkedQueue<String> fileWaitTaskQueue = new ConcurrentLinkedQueue<String>(); //

	// 正在创建报文件的线程数量!
	private ConcurrentMap<String, String> createFileThreadMap = new ConcurrentHashMap<String, String>();
	//报文中的数据条数
	private ConcurrentMap<String, String> createFileDataCountMap = new ConcurrentHashMap<String, String>();

	// 压缩线程的Map,必须用这个线程安全的Map
	private ConcurrentMap<String, String> zipThreadMap = new ConcurrentHashMap<String, String>();
	// 和队列同步，案列对应的数据
	private ConcurrentMap<String, JSONObject> dataMap = new ConcurrentHashMap<String,JSONObject>();
	private YuFormatUtil bsUtil = new YuFormatUtil();

	/**
	 * 构造方法是private,别人无法创建!
	 */
	private DetailDownloadMonitor() {
		
	}
	public ConcurrentMap<String, JSONObject> getDataMap() {
		return dataMap;
	}

	public void setDataMap(ConcurrentMap<String, JSONObject> dataMap) {
		this.dataMap = dataMap;
	}

	public void removedataMap(String _taskId) {
		dataMap.remove(_taskId);
	}

	public void putDataMap(String repId, JSONObject map) {
		dataMap.put(repId, map);
	}
	/**
	 * 取得等待队列中的一个新任务
	 * @return
	 */
	public String getFileWaitTask() {
		return fileWaitTaskQueue.poll();
	}

	/**
	 * 计算等待队列的大小!
	 * @return
	 */
	public int getFileWaitTaskSize() {
		return fileWaitTaskQueue.size();
	}

	/**
	 * 加入新的子任务标记，即记录在运行中的子线程
	 * @param _taskId
	 */
	public void putCreateFileThreadRuning(String _taskId) {
		String str_time = bsUtil.getCurrDateSecond(); 
		createFileThreadMap.put(_taskId, str_time);
	}

	/**
	 * 删除子任务的标记，即删除在运行中的子线程
	 * @param _taskId
	 */
	public void removeCreateFileThreadRuning(String _taskId) {
		createFileThreadMap.remove(_taskId);
		createFileDataCountMap.remove(_taskId);
	}
	/***
	 * @方法描述: 删除队列中的任务
	 * @创建人: miaokx@yusys.com.cn
	 * @创建时间: 2021/7/9 15:44
	 * @Param: taskId
	 * @return: void
	 */
	public void removeQueue(String taskId) {
		fileWaitTaskQueue.remove(taskId);
	}
	/**
	 * 记录报文数据的总条数
	 * @param _taskId
	 * @param _count
	 */
	public void putCreateFileDataCount(String _taskId, String _count) {
		createFileDataCountMap.put(_taskId, _count);
	}

	/**
	 * 获取报文数据条数
	 * @param _taskId
	 * @return
	 */
	public String getCreateFileDataCount(String _taskId) {
		return createFileDataCountMap.get(_taskId);
	}

	/**
	 * 计算正在创建的任务的大小..
	 * @return
	 */
	public int getCreateFileThreadSize() {
		return createFileThreadMap.size();
	}

	/**
	 * 设置压缩任务在运行!即往Map中塞入一条记录!
	 * @param _orgNo
	 * @param _date
	 */
	public void putZipThreadRuning(String _orgNo, String _date) {
		String str_time = bsUtil.getCurrDateSecond();
		zipThreadMap.put(_orgNo + "#" + _date, str_time);
	}

	/**
	 * 删除压缩任务状态标记
	 * @param _orgNo
	 * @param _date
	 */
	public void removeZipThreadRuning(String _orgNo, String _date) {
		zipThreadMap.remove(_orgNo + "#" + _date);
	}

	/**
	 * 取得当前机构与日期的任务是否存在
	 * @param _orgNo
	 * @param _date
	 * @return
	 */
	public String getZipThreadRuning(String _orgNo, String _date) {
		return zipThreadMap.get(_orgNo + "#" + _date);
	}

	/**
	 * 取得当前机构与日期的任务是否存在
	 * @param _orgNo
	 * @param _date
	 * @return
	 */
	public boolean isZipThreadRuning(String _orgNo, String _date) {
		return zipThreadMap.containsKey(_orgNo + "#" + _date);
	}

	/**
	 * 取得压缩任务的在线数量,如果超过一定数量,可以控制不能继续处理!
	 * @return
	 */
	public int getZipThreadSize() {
		return zipThreadMap.size();
	}

	/**
	 * 加入任务队列,或者启动主线程,即如果主线程已启动，则直接加入队列
	 * 如果主线程还没有启动,则启动之!
	 * 这个方法必须加synchronized同步控制，否则可能会启动多个主线程!
	 * 必须要保证主线程永远只有一个! 也可以在系统启动时主线程就自动启动.
	 * @param
	 * @return
	 */
	public synchronized void addCreateFileWaitTaskOrStart(String rid) {
		if (!fileWaitTaskQueue.contains(rid)) {
			fileWaitTaskQueue.add(rid);
		}
		// 如果主线程没有启动,则启动主线程.即有可能曾经主线程已经跑结束了,则再次启动
		// 这样保证主线程永远只会有一个!
		if (!isCreateFileMainThreadRuning) {
			new DetailDownloadMainThread().start();
		}
	}

	/**
	 * 创建实例
	 * @return
	 */
	public static synchronized DetailDownloadMonitor getInstance() {
		if (monitor != null) {
			return monitor;
		}
		monitor = new DetailDownloadMonitor();
		
		return monitor;
	}
}
