/**
 * 
 * <pre>
 * Title: 【金融基础报送】
 * Description: 常量类定义
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年8月12日
 */

(function($) {
	window['BfdFreeUtil'] = {};
	
	/**
	 * 【检核结果】--判断总分支行
	 */
	var bfd_org_class = {
		zh : '总行',
		fh : '分行',
		zhh : '支行'
	};
	BfdFreeUtil.getBfdOrgClass = function() {
		return bfd_org_class;
	};
	
	/**
	 * 获取机构层级，通过当前登录人机构号获取
	 */
	BfdFreeUtil.getOrgClass = function(orgNo) {
		var org_class = "";
		var jso_tr = JSPFree.getHashVOs("select org_class from rpt_org_info where org_no='"+orgNo+"' and org_type='BFD'");
		if(jso_tr != null && jso_tr.length>0){
			org_class =  jso_tr[0].org_class;
		}
		return org_class;
	};
	
	/**
	 * 获取机构层级，通过当前登录人机构号获取
	 */
	BfdFreeUtil.getOrgNm = function(orgNo) {
		var org_nm = "";
		var jso_tr = JSPFree.getHashVOs("select org_nm from rpt_org_info where org_no='"+orgNo+"' and org_type='BFD'");
		if(jso_tr != null && jso_tr.length>0){
			org_nm =  jso_tr[0].org_nm;
		}
		return org_nm;
	};
	
	/**
	 * 获取报送机构数量
	 */
	BfdFreeUtil.getReportOrgNum = function() {
		var reportOrgNum = "0";
		var jso_tr = JSPFree.getHashVOs("select count(1) c from rpt_org_info where org_type='BFD' and is_org_report='Y'");
		if(jso_tr != null && jso_tr.length>0){
			reportOrgNum =  jso_tr[0].c;
		}
		return reportOrgNum;
	};
	
	/**
	 * 【报表处理】--各种日期字段
	 */
	var bfd_date_string = {
			data_dt : 'DATA_DT',
			tr_dt : 'TR_DT'
		};
	BfdFreeUtil.getBfdDateString = function() {
		return bfd_date_string;
	};
	
	/**
	 * 获取日期字段，通过bfd_cr_col查询该表的日期字段，只查特殊的日期字段，若没有查到，默认返回"DATA_DT"
	 */
	BfdFreeUtil.getDateColumn = function(_tabName) {
		//默认返回DATA_DT
		return BfdFreeUtil.getBfdDateString().data_dt;
	};

	/**
	 * 业务表 数据来源
	 */
	var data_ources = {
		etl_process : '01', //etl加工
		page_entry : '02',	//页面录入
		batch_import : '03'	//批量导入
	}
	BfdFreeUtil.getDataOurces =function (){
		return data_ources;
	}

	/*
	* 业务表修改记录
	*/
	var modified_type = {
		insert : '1', // 新增
		update : '2', // 修改
		delete : '3' // 删除
	}
	BfdFreeUtil.getModifiedType = function (){
		return modified_type;
	}
	/*
	* 业务表修改记录
	*/
	var ope_mod = {
		bbsjwh : '1', // 新增
		bbtb : '2' // 修改

	}
	BfdFreeUtil.getOpeMod = function (){
		return ope_mod;
	}
	/**
	 * 业务表 数据修改 来源
	 */
	var data_modify = {
		data_maintenance : '01',	//数据维护页面
		data_filling : '02'	//数据填报页面
	}
	BfdFreeUtil.getDataModefy =function (){
		return data_modify;
	}

	/**
	 * 数据统计表 统计类型
	 */
	var source_type = {
		etl_process : '1', //etl加工
		page_entry : '2',	//页面录入
		batch_import : '3',	//批量导入
		data_maintenance : '4',	//数据维护页面修改
		data_filling : '5'	//数据填报页面修改
	}
	BfdFreeUtil.getSourceType =function (){
		return source_type;
	}
	
})(jQuery);

