/**
 * 
 * <pre>
 * Title: 【利率报备】
 * Description: 常量类定义
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年8月12日
 */

(function($) {
	window['PscsFreeUtil'] = {};
	PscsFreeUtil.reportType = "PSCS";
	
	/**
	 * 【检核结果】--判断总分支行
	 */
	var pscs_org_class = {
		zh : '总行',
		fh : '分行',
		zhh : '支行'
	};
	PscsFreeUtil.getPscsOrgClass = function() {
		return pscs_org_class;
	};

	/**
	 * 获取机构层级，通过当前登录人机构号获取
	 */
	PscsFreeUtil.getOrgClass = function(orgNo) {
		var org_class = "";
		var jso_tr = JSPFree.getHashVOs("select org_class from rpt_org_info where org_no='"+orgNo+"' and org_type='11'");
		if(jso_tr != null && jso_tr.length>0){
			org_class =  jso_tr[0].org_class;
		}
		return org_class;
	};

})(jQuery);

