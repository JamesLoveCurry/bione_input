package com.yusys.bione.plugin.yuformat.service;

import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;

/**
 * 导出Excel
 * @author xch
 *
 */
public class ExportTxtBySQLDMO extends AbstractDownloadFile {

	private YuFormatUtil bsUtil = new YuFormatUtil();

	//设置下载类型..
	@Override
	public int getDownloadType() {
		return AbstractDownloadFile.DownLoadAsString; //
	}

	@Override
	public String getDownLoadContentAsString(JSONObject _par) throws Exception {
		String str_ds = _par.getString("DS");
		String str_sql = _par.getString("SQL");
		String str_splitStr = _par.getString("splitStr"); //分割符
		String str_typeStr = _par.getString("typeStr"); //类型，区分是否是银监要求的59张表
		String str_tbNameStr = _par.getString("tabName"); //表名，中文
		
		HashVO[] hvs = null;
		if (str_typeStr.equals("Y")) {
			String sql_value = "";
			HashVO[] hvs_cols = bsUtil.getHashVOs("select * from east_cr_col where tab_name='" + str_tbNameStr + "' and is_export = 'Y' order by col_no");
			if (hvs_cols != null && hvs_cols.length > 0) {
				for (HashVO vo : hvs_cols) {
					sql_value += vo.getStringValue("COL_NAME_EN") + ",";
				}
				sql_value = sql_value.substring(0, sql_value.length()-1);
			}
			
			str_sql = str_sql.replace("*", sql_value);
			hvs = bsUtil.getHashVOsByDS(str_ds,str_sql); //查询数据
		} else {
			hvs = bsUtil.getHashVOsByDS(str_ds,str_sql); //查询数据
		}

		StringBuilder sb_txt = new StringBuilder(); //
		for (int i = 0; i < hvs.length; i++) {
			String[] str_keys = hvs[i].getKeys();
			for (int j = 0; j < str_keys.length; j++) {
				sb_txt.append(hvs[i].getStringValue(str_keys[j], "")); //输出这一列
				sb_txt.append(str_splitStr); //加上分割符
			}
			if (sb_txt.lastIndexOf(str_splitStr) != -1) {
				sb_txt = sb_txt.delete(sb_txt.length()-str_splitStr.length(), sb_txt.length());
			}
			sb_txt.append("\r\n");
		}
		return sb_txt.toString();
	}
}
