//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){

JSPFree.createBillList("d1","xch_user_CODE1");

 // JSPFree.createSplit("d1", "左右", 300); // 页签先左右分割

 // JSPFree.createBillTree("d1_A", "rpt_org_info_CODE1"); //
 // JSPFree.createBillCard("d1_B", "rpt_org_info_CODE1",["新增/onInsert/icon-p11","保存/onSave/icon-p12"]); 

 // JSPFree.bindBillTreeOnSelect(d1_A_BillTree,function(_node){
 // 	console.log(_node); //
 // 	if(JSPFree.isBillTreeNodeRoot(d1_A_BillTree,_node)){
 //        console.log("因为选中的是根结点,所以不处理...");
 // 		return;
 // 	}
 // 	var jso_data = JSPFree.getBillTreeSelectData(d1_A_BillTree);
 // 	var str_org_no = jso_data.org_no;
 // 	JSPFree.queryBillCardData(d1_B_BillCard,"org_no='" + str_org_no + "' and org_type='04'");
 // });

}

function onInsert(){
	alert("新增");
}

function onSave(){
  alert("ok");
}

