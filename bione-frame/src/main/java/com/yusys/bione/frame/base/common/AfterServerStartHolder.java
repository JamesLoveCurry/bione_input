package com.yusys.bione.frame.base.common;

/**
 * 
 * <pre>
 * Title:系统启动后开始用户自定义程序持有者
 * Description:
 * </pre>
 * 
 * @author
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：		  修改日期:     修改内容:
 *          </pre>
 */
public class AfterServerStartHolder {
	
	private static Object lock = new Object(); // 信息在缓存中的key值

	/**
	 * 
	 * （目前没有提供前台配置功能，当手工修改数据库的信息后只有等待缓存过期，然后自动从数据库获取最新的的数据）
	 */
	public static void refreshJobInfo() {
		synchronized (lock) {
//			initAfterServerStartEast();
//			initAfterServerStartFsrs();
//			initAfterServerStartCrrs();
//			initAfterServerStartPscs();
//			initAfterServerStartSafe();
//			initAfterServerStartImas();
//			initAfterServerStartBfd();
		}
	}

	/**
	 * 初始化（EAST定时任务）
	 */
	public static void initAfterServerStartEast() {
		String c = "com.yusys.east.scheduled.StartAllEastJob";
		try {
			Class.forName(c).newInstance();
		} catch (Throwable _ex) {
			
		}
	}
	/**
	 * 初始化（金标定时任务）
	 */
	public static void initAfterServerStartFsrs() {
		String c = "com.yusys.fsrs.scheduled.StartAllFsrsJob";
		try {
			Class.forName(c).newInstance();
		} catch (Throwable _ex) {
			
		}
	}
	
	/**
	 * 初始化（客户风险定时任务）
	 */
	public static void initAfterServerStartCrrs() {
		String c = "com.yusys.crrs.scheduled.StartAllCrrsJob";
		try {
			Class.forName(c).newInstance();
		} catch (Throwable _ex) {
			
		}
	}
	
	/**
	 * 初始化（支付合规定时任务）
	 */
	public static void initAfterServerStartPscs() {
		String c = "com.yusys.pscs.scheduled.StartAllPscsJob";
		try {
			Class.forName(c).newInstance();
		} catch (Throwable _ex) {
			
		}
	}
	
	/**
	 * 初始化（外汇局定时任务）
	 */
	public static void initAfterServerStartSafe() {
		String c = "com.yusys.safe.scheduled.StartAllSafeJob";
		try {
			Class.forName(c).newInstance();
		} catch (Throwable _ex) {
			
		}
	}

	/**
	 * 初始化（利率报备定时任务）
	 */
	public static void initAfterServerStartImas() {
		String c = "com.yusys.imas.scheduled.StartAllImasJob";
		try {
			Class.forName(c).newInstance();
		} catch (Throwable _ex) {

		}
	}

	/**
	 * 初始化（金融基础定时任务）
	 */
	public static void initAfterServerStartBfd() {
		String c = "com.yusys.bfd.scheduled.StartAllBfdJob";
		try {
			Class.forName(c).newInstance();
		} catch (Throwable _ex) {

		}
	}
}
