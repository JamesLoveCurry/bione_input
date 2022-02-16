<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%
out.println(new com.yusys.bione.plugin.yuformat.utils.YuFormatUtil().getJspHeadHtml(request));
%>
</head>

<body style="overflow:hidden">
<div id="d1" style="width:100%;height:600px;"></div>

<script type="text/JavaScript">
JSPFree.resetHeight("d1");  //重设一下高度,必须是一个绝对值,设100%会页面出不来,也不知是个bug还是啥原因,以后再研究

JSPFree.createTabb("d1",["按规则分类查看","按58张表查看"]);  //创建多页签

JSPFree.createSplit("d1_1","左右",235);  //第1个页签先左右分割
JSPFree.createSplit("d1_2","左右",350);  //第1个页签先左右分割


JSPFree.createBillList("d1_1_A","east_cr_rule_type_ref");  //第1个页签左边
JSPFree.createBillList("d1_1_B","east_cr_rule_ref");  //第1个页签右边的上边

JSPFree.createBillList("d1_2_A","east_cr_tab_ref");  //第2个页签的左边,即58张表
JSPFree.createBillList("d1_2_B","east_cr_rule_CODE1");  //第2个页签的右边

//绑定表格选择事件,d1_1_A_BillList会根据命名规则已创建
JSPFree.bindSelectEvent(d1_1_A_BillList,function(rowIndex, rowData){
  var str_typename = rowData.typename;  //取得选中记录中的id值
  var str_sqlWhere = "type_cd='"  + str_typename + "'";  //拼SQL条件
  JSPFree.queryDataByConditon(d1_1_B_BillList,str_sqlWhere);  //锁定规则表查询数据
});

//绑定表格选择事件
JSPFree.bindSelectEvent(d1_2_A_BillList,function(rowIndex, rowData){
  var str_tab_name = rowData.tab_name;  //取得选中记录中的id值
  var str_sqlWhere = "tab_name='"  + str_tab_name + "'";  //拼SQL条件
  JSPFree.queryDataByConditon(d1_2_B_BillList,str_sqlWhere);  //锁定规则表查询数据
});
</script>
</body>
</html>
