/**
 * 
 * <pre>
 * Title: 【客户风险报送】
 * Description: 常量类定义
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2021年10月21日
 */

(function($) {
	window['CrrsFreeUtil'] = {};
	
	/**
	 * 【检核结果】--判断总分支行
	 */
	var crrs_org_class = {
		zh : '总行',
		fh : '分行',
		zhh : '支行'
	};
	CrrsFreeUtil.getCrrsOrgClass = function() {
		return crrs_org_class;
	};
	
	/**
	 * 获取机构层级，通过当前登录人机构号获取
	 */
	CrrsFreeUtil.getOrgNo = function(_loginUserOrgNo) {
		var org_no = "";
		var jso_tr = JSPFree.getHashVOs("select org_no from rpt_org_info where mgr_org_no='"+_loginUserOrgNo+"' and org_type='08'");
		if(jso_tr != null && jso_tr.length>0){
			org_no =  jso_tr[0].org_no;
		}
		return org_no;
	};
	
	/**
	 * 获取机构层级，通过当前登录人机构号获取
	 */
	CrrsFreeUtil.getOrgClass = function(orgNo) {
		var org_class = "";
		var jso_tr = JSPFree.getHashVOs("select org_class from rpt_org_info where org_no='"+orgNo+"' and org_type='08'");
		if(jso_tr != null && jso_tr.length>0){
			org_class =  jso_tr[0].org_class;
		}
		return org_class;
	};
	
	/**
	 * 获取机构层级，通过当前登录人机构号获取
	 */
	CrrsFreeUtil.getOrgNm = function(orgNo) {
		var org_nm = "";
		var jso_tr = JSPFree.getHashVOs("select org_nm from rpt_org_info where org_no='"+orgNo+"' and org_type='08'");
		if(jso_tr != null && jso_tr.length>0){
			org_nm =  jso_tr[0].org_nm;
		}
		return org_nm;
	};
	
	
})(jQuery);

