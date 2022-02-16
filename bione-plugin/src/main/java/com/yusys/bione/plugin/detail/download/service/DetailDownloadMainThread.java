package com.yusys.bione.plugin.detail.download.service;

import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import netscape.javascript.JSObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * <pre>
 * Title: 导出数据文件的主线程!
 * 在主线程中轮循扫描队列中的任务,如果子线程数还没有越界，则创建几个子线程，并加入计算!
 * </pre>
 * @author mkx
 * @version 1.00.00
 * @date  2020/4/29
 */
public class DetailDownloadMainThread extends Thread {
	private Logger logger = LoggerFactory.getLogger(getClass());
	// 最多只能同时任务在跑!
	private int maxReportFileLimit;
	public DetailDownloadMainThread() {
		PropertiesUtils pUtils = PropertiesUtils.get("database.properties");
		String dialectType = pUtils.getProperty("detail.download.file.task");
		this.maxReportFileLimit = Integer.parseInt(dialectType);
	}

	/**
	 * 线程逻辑
	 */
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		try {
			// 标记主线程正在跑!
			DetailDownloadMonitor.getInstance().isCreateFileMainThreadRuning = true;
			String rid = "";
			// 死循环,跑完队列所有任务
			while (true) {
				// 目前队列的大小,即排队等待处理的报文任务
				int li_waitTaskSize = DetailDownloadMonitor.getInstance().getFileWaitTaskSize();
				//如果待办报文任务数量为零，则跳出while循环
				if (li_waitTaskSize == 0) {
					break;
				}
				// 正在导出数据文件的线程数量，与报文任务数量不是一个概念。比如本次报文任务是50个，但是能同时存在，同时生成报文的线程数量上限是20个。
				int reportFileTaskRuning = DetailDownloadMonitor.getInstance().getCreateFileThreadSize();
				// 如果运行的任务，达到上限，则进行控制，不做任何处理
				if (reportFileTaskRuning >= maxReportFileLimit) {
					logger.info("正在导出数据的任务数量【" + reportFileTaskRuning + "】达到上限【" + maxReportFileLimit + "】,不再增加新的任务..排队任务数【" + li_waitTaskSize + "】");
				} else {
					// 可以跑的数量!上限减去正在运行的，比如上限是30,正在跑的是20个,则还可以新加10个任务.
					// 第一次就是直接同时跑30个
					int li_cando = maxReportFileLimit - reportFileTaskRuning;
					if (li_cando > li_waitTaskSize) {
						li_cando = li_waitTaskSize;
					}
					for (int i = 0; i < li_cando; i++) {
						rid = DetailDownloadMonitor.getInstance().getFileWaitTask();
						ConcurrentMap<String, JSONObject> map =  DetailDownloadMonitor.getInstance().getDataMap();
						if (map != null && !map.isEmpty()) {
							JSONObject object = DetailDownloadMonitor.getInstance().getDataMap().get(rid);
							if (object !=null) {
								// 如果为空,则表示队列空了,直接退出for循环
								if (StringUtils.isEmpty(rid)) {
									break;
								}
								logger.info("启动一个子任务子任务id：" + rid);
								// 创建一个子线程!
								new DetailDownloadItemThread(object,rid).start();
							}

						}
					}
				}
				// 每5秒钟轮循一次!
				Thread.currentThread().sleep(5000);
			}
		} catch (Throwable _ex) {
			_ex.printStackTrace();
		} finally {
			// 当所有子线程创建完毕，则标记主线程不在跑,即已结束!
			DetailDownloadMonitor.getInstance().isCreateFileMainThreadRuning = false;
		}
	}
}
