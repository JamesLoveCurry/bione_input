package com.yusys.bione.plugin.yuformat.service;

import java.io.File;
import java.io.FileInputStream;

import com.alibaba.fastjson.JSONObject;

public class TestDownloadFile extends AbstractDownloadFile {

	@Override
	public int getDownloadType() {
		return AbstractDownloadFile.DownLoadAsInputStream;
	}

	@Override
	public DownLoadInStreamVO getDownLoadContentAsInputStream(JSONObject _par) throws Exception {
		DownLoadInStreamVO vo = new DownLoadInStreamVO();

		File file = new File("K:/报表数据299999.xls"); //
//		if (!file.exists()) {
//			throw new BizException("文件不存在,无法下载!!"); //
//		}
		vo.setInputStream(new FileInputStream(file));
		return vo;
	}
}
