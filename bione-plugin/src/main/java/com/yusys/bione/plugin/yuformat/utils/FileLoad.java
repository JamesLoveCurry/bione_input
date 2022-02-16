package com.yusys.bione.plugin.yuformat.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.comp.utils.RandomUtils;

/**
 * 文件的上传及下载
 * 
 * @author kf0064
 *
 */
public class FileLoad {
	private YuFormatUtil bsUtil = new YuFormatUtil();

	/**
	 * 上传图片，将文件保存到数据库中
	 * 
	 * @param bytes
	 * @throws Exception
	 */
	public JSONObject uploadImage(String str_filename, byte[] bytes, String batchid, String fromtable, String pkname, String pkvalue) throws Exception {
		JSONObject json_result = new JSONObject();// 返回操作结果
		String str_code64 = bsUtil.convertBytesTo64Code(bytes); // 将字节转成64位编码!
		// Calendar cal = Calendar.getInstance();
		// Date date_curr = cal.getTime();
		// String str_batchid = new
		// SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS").format(date_curr); //主键
		if (str_code64 != null) { // 如果有内容才进行真正的删除!!
			List<String[]> al_data = bsUtil.split(str_code64, 10, 4000); // 每4000个字符分割一次，注意数据库中对应字段的类型及长度
			List<String> al_sql = new ArrayList<String>(); //
			// 默认每张表的每条记录只能关联一张图片
			String str_delSql = "delete from RPT_CABIN_IMGUPLOAD where billtable='" + fromtable + "' and billpkname='" + pkname + "' and billpkvalue='" + pkvalue + "'";//
			al_sql.add(str_delSql);//
			for (int i = 0; i < al_data.size(); i++) { // 遍历各行!
				String[] str_rowData = al_data.get(i); // 该行的数据
				String str_id = RandomUtils.uuid2(); // 主键
				for (int j = 0; j < str_rowData.length; j++) {
					if (j == 0) { // 如果是第一列
						InsertSQLBuilder isql_insert = new InsertSQLBuilder("RPT_CABIN_IMGUPLOAD"); //
						isql_insert.putFieldValue("RECID", str_id); //
						isql_insert.putFieldValue("batchid", batchid); // 批号
						isql_insert.putFieldValue("filename", str_filename); // 批号
						isql_insert.putFieldValue("billtable", fromtable); //
						isql_insert.putFieldValue("billpkname", pkname); //
						isql_insert.putFieldValue("billpkvalue", pkvalue); //
						isql_insert.putFieldValue("seq", "" + (i + 1)); // 序号
						isql_insert.putFieldValue("img0", str_rowData[0]); // 第一列的值
						al_sql.add(isql_insert.getSQL()); //
					} else {
						UpdateSQLBuilder isql_update = new UpdateSQLBuilder("RPT_CABIN_IMGUPLOAD", "RECID='" + str_id + "'"); //
						isql_update.putFieldValue("img" + j, str_rowData[j]); // 第一列的值
						al_sql.add(isql_update.getSQL()); //
					}
				}
			}
			String[] arr_sqls = new String[al_sql.size()];
			int i = 0;
			for (Object s : al_sql) {
				arr_sqls[i++] = s.toString();
			}
			int[] i_res = bsUtil.executeUpdate(arr_sqls); // 实际插入数据!!!
			for (int r : i_res) {
				if (r == 0) {
					json_result.put("result", "error");//
					break;
				}
			}
			json_result.put("result", "success");
		}
		return json_result;
	}

	/**
	 * 下载图片
	 * 
	 * @param _batid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public JSONObject getImageCode(JSONObject json_req) {
		JSONObject json_result = new JSONObject();
		String _batid = (String) json_req.get("batchid");//
		// 根据json_req中存在的字段拼写查询语句，一般查询字段有：BATCHID（批号），BILLTABLE（表名），BILLPKNAME（主键名），
		// BILLPKVALUE（主键值）， FILENAME（文件名）
		String str_querySql = "select * from RPT_CABIN_IMGUPLOAD where 1=1";
		if (json_req.containsKey("batchid")) {
			str_querySql += " and BATCHID='" + StringUtils.defaultString(json_req.getString("batchid")) + "'";
		}
		if (json_req.containsKey("fromtable")) {
			str_querySql += " and BILLTABLE='" + StringUtils.defaultString(json_req.getString("fromtable")) + "'";
		}
		if (json_req.containsKey("pkname")) {
			str_querySql += " and BILLPKNAME='" + StringUtils.defaultString(json_req.getString("pkname")) + "'";
		}
		if (json_req.containsKey("pkvalue")) {
			str_querySql += " and BILLPKVALUE='" + StringUtils.defaultString(json_req.getString("pkvalue")) + "'";
		}
		if (json_req.containsKey("filename")) {
			str_querySql += " and FILENAME='" + StringUtils.defaultString(json_req.getString("filename")) + "'";
		}
		str_querySql += " order by SEQ";
		// System.out.println("--[str_querySql]--: "+str_querySql);
		HashVO[] hvs = bsUtil.getHashVOs(str_querySql); //
		StringBuilder sb_64code = new StringBuilder(); //
		String str_item = null; //
		// System.out.println(hvs.length);
		String str_filename = "";
		if (hvs.length >= 1) {
			str_filename = hvs[0].getStringValue("filename", "");
			// System.out.println("--loadimg--: "+str_filename);
			for (int i = 0; i < hvs.length; i++) { // 遍历!
				for (int j = 0; j < 10; j++) {
					// System.out.println("【allKeys】: "+hvs[i].getKeys().toString());
					str_item = hvs[i].getStringValue("IMG" + j); //
					//
					if (str_item != null && !str_item.equals("")) { // 如果有值
						// System.out.println("【--str_item[j]--】: "+str_item.toString());
						sb_64code.append(str_item.trim()); // 拼接起来!!
					} else { // 如果值为空
						break; // 中断退出!!!因为只可能是最后一行有零头,所以只需要内循环中断即可!!
					}
				}
			}
		}
		// System.out.println("【sb_64code】: "+sb_64code.toString());
		json_result.put("imgCode", sb_64code.toString());
		json_result.put("filename", str_filename);
		return json_result; //
	}
}
