/**
 *
 * <pre>
 * Title:申报文件信息反馈自定义情况信息
 * Description:
 * </pre>
 * @author wangxy
 * @version 1.00.00
 * @date 2020/9/15
 */
var fileName = "";
var tab_name = "";
var tab_name_en = "";
var str_subfix = "";
var str_className = "";
var report_type = "";
function AfterInit() {
	// 默认查询所有
    str_subfix = 'ALL';
    report_type = 'ALL';
    // 获取【状态表】常量类
    tab_name = SafeFreeUtil.getTableNames().SAFE_CR_STATUS_CONFIG;
    str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tab_name + "','safe_cr_status_config','" + str_subfix + "')";
    JSPFree.createBillList("d1", str_className, null, {
        isSwitchQuery: "N",
        autoquery: "Y",
        refWhereSQL: "job_type='2'"
    });
}