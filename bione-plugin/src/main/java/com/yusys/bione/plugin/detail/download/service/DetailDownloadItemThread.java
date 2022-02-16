package com.yusys.bione.plugin.detail.download.service;

import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *
 * <pre>
 * Title: 创建报文文件的子线程，即一个任务一个线程!!
 * </pre>
 * @author liangzy5
 * @version 1.00.00
 * @date  2020/8/24
 */
public class DetailDownloadItemThread extends Thread {
	String rid;
	JSONObject param;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private YuFormatUtil bsUtil = new YuFormatUtil();
	private DetailDownloadBS detailDownloadBS = new DetailDownloadBS();
	/**
	 * @方法描述: 构造方法
	 * @创建人: miaokx@yusys.com.cn
	 * @创建时间: 2021/4/29 15:58
	 * @Param: param
	 * @return: 
	 */
	public DetailDownloadItemThread(JSONObject param, String rid) {
		this.rid = rid;
		this.param = param;
	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		try {
			// 第一步就先注册缓存,表示XX机构、XX日期的压缩任务正在执行
			DetailDownloadMonitor.getInstance().putCreateFileThreadRuning(rid);
			// 实际压缩,耗时操作,会阻塞在这里
			long ll_1 = System.currentTimeMillis();
			logger.info("开始生成文件[" + rid + "]...");
			// 修改任务状态为上传中
			String sql = "update detail_download_list set status='2' where rid='" + rid + "'";
			bsUtil.executeUpdate(sql);
			JSONObject jsonObject = new JSONObject();
			if ("EXCEL".equalsIgnoreCase(param.getString("fileType"))) {
				jsonObject = detailDownloadBS.exportDataExcel(param);
			} else {
				jsonObject = detailDownloadBS.exportDataCSV(param);
			}
			if ("success".equalsIgnoreCase(jsonObject.getString("code"))) {
				sql = "update detail_download_list set status='3',remark='文件生成成功!',file_path='" + jsonObject.getString("data") + "' where rid='" + rid + "'";
				bsUtil.executeUpdate(sql);
			} else {
				sql = "update detail_download_list set status='4',remark='" + jsonObject.getString("msg") + "' where rid='" + rid + "'";
				bsUtil.executeUpdate(sql);
			}
			long ll_2 = System.currentTimeMillis();
			logger.info("生成文件[" + rid + "]结束!,耗时【" + (ll_2 - ll_1) + "】毫秒");

		} catch (Throwable _ex) {
			String sql = "update detail_download_list set status='4',remark='" + _ex.getMessage() + "' where rid='" + rid + "'";
			try {
				bsUtil.executeUpdate(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
			_ex.printStackTrace(); //
		} finally {
			// 最后一定要移除缓存标记，否则就控制不准了!
			DetailDownloadMonitor.getInstance().removeCreateFileThreadRuning(rid);
			DetailDownloadMonitor.getInstance().removedataMap(rid);
		}
	}
}
