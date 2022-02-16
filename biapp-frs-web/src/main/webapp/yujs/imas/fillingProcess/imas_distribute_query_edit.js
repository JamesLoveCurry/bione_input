//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/fillingProcess/distribute_examine.js】
var data = "";
function AfterInit(){
	data = jso_OpenPars.data;
	var tabName = data.tab_name;
	var tabNameEn = data.tab_name_en;
	var dataDt = data.data_dt;
	var taskId = data.rid;

	var str_className2 = "Class:com.yusys.east.business.model.service.East58ModelTempletBuilder2.getTemplet('"+tabName+"','"+tabNameEn+"','"+str_LoginUserOrgNo+"','_R','"+taskId+"')";
	JSPFree.createBillList("d1",str_className2,null,{list_btns:"$VIEW",isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"N",list_ispagebar:"N"});
}