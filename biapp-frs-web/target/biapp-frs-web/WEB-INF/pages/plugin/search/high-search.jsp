<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var mainform;
	var parentSearchObj = JSON2.parse('${searchObj}');
	$(function(){
		searchForm(parentSearchObj.searchType);
		var btns = [{
			text : "重置",
			onclick : resetSearch
		},{
			text : "搜索",
			onclick : highSearch
		}];
		BIONE.addFormButtons(btns);
		//回显上次搜索的条件内容
		if(parentSearchObj.indexNo || parentSearchObj.indexNo == "" 
				|| parentSearchObj.rptNum || parentSearchObj.rptNum == ""){//判断是高级搜索
			/* $.ajax({//处理部门信息
				async:true,
				type:"GET",
				dataType:"json",
				url:"${ctx}/report/frame/idx/getDeptById?deptId="+parentSearchObj.belongDeptNm,
				beforeSend : function(a, b, c) {
					BIONE.showLoading('正在加载数据中...');
				},
				success:function(data){
					if(data.dept){
						$.ligerui.get("belongDeptNm")._changeValue(parentSearchObj.belongDeptNm,data.dept.deptName);
					}
					BIONE.hideLoading();
				},
				error : function(result) {
					BIONE.hideLoading();
				}
			}); */
			if(parentSearchObj.searchType == "idx"){
				var newObj = {//回显除部门其余信息
						indexNo : parentSearchObj.indexNo,
						indexNm : parentSearchObj.indexNm,
						indexType : parentSearchObj.indexType,
						isCabin : parentSearchObj.isCabin,
						isSepaInbIdx : parentSearchObj.isSepaInbIdx
				};
			}else{
				var newObj = {//回显除部门其余信息
						rptNum : parentSearchObj.rptNum,
						rptNm : parentSearchObj.rptNm,
						isCabin : parentSearchObj.isCabin
				};
			}
			mainform.setData(newObj);
		}
	})
	
	function resetSearch(){
		if(parentSearchObj.searchType == "idx"){
			mainform.setData({indexNo : "",indexNm : "",indexType : "",isCabin : "",belongDeptNm : "",isSepaInbIdx : "1"});
		}else{
			mainform.setData({rptNum : "",rptNm : "",isCabin : "",belongDeptNm : ""});
		}
	}
	
	function searchForm(searchType) {
		if(searchType == "idx"){
			mainform = $("#mainform").ligerForm({
				fields : [{
					display : '指标编号',
					name : "indexNo",
					newline : true,
					labelWidth : "90",
					type : "text"
				}, {
					display : '指标名称',
					name : "indexNm",
					labelWidth : "90",
					newline : false,
					type : "text"
				}, {
					display : '指标类型',
					name : 'indexType',
					labelWidth : "90",
					newline : true,
					type : "select",
					comboboxName:"index_type_box",
					options : {
						url : "${ctx}/report/frame/idx/indexTypeList.json"
					}
				},{
					display : "是否管驾指标",
					name : "isCabin",
					labelWidth : "90",
					newline : false,
					type : "hidden",
					options :{
						data :[{
							id : "1",
							text : "是"
						},{
							id : "0",
							text : "否"
						}]
					}
				}]
			});
		}else if(searchType == "rpt"){
			mainform = $("#mainform").ligerForm({
				fields:[{
					display : '报表编号',
					name : "rptNum",
					labelWidth : "100",
					newline : true,
					type : "text"
				},{
					display : '报表名称',
					name : "rptNm",
					labelWidth : "100",
					newline : false,
					type : "text"
				},{
					display : "是否管驾报表",
					name : "isCabin",
					labelWidth : "100",
					newline : true,
					type : "hidden",
					options :{
						data :[{
							id : "1",
							text : "是"
						},{
							id : "0",
							text : "否"
						}]
					}
				}]
			});
		}
	}
	
	function highSearch(){
		var searchObj = mainform.getData();
		searchObj.exeFunction = parentSearchObj.exeFunction;
		searchObj.searchType = parentSearchObj.searchType;
		var obj = parentSearchObj.obj? parentSearchObj.obj : "searchObj";
		if(parentSearchObj.searchType == "idx"){
			if(searchObj.indexNo == "" && searchObj.indexNm == "" && searchObj.indexType == ""
				&& searchObj.belongDeptNm == "" && searchObj.isCabin == "" && searchObj.isSepaInbIdx == "1"){//此条件为空搜索
				parent[obj]= {exeFunction : parentSearchObj.exeFunction,searchType : parentSearchObj.searchType};
				parent[obj].obj = obj;
				callFunction(parentSearchObj.exeFunction,null,null);
			}else{
				parent[obj] = searchObj;
				parent[obj].obj = obj;
				callFunction(parentSearchObj.exeFunction,"highSearch",JSON2.stringify(searchObj));
			}
		}else{
			if(searchObj.rptNum == "" && searchObj.rptNm == ""
				&& searchObj.belongDeptNm == "" && searchObj.isCabin == ""){//此条件为空搜索
				parent[obj] = {exeFunction : parentSearchObj.exeFunction,searchType : parentSearchObj.searchType};
				parent[obj].obj = obj;
				callFunction(parentSearchObj.exeFunction,null,null);
			}else{
				parent[obj] = searchObj;
				parent[obj].obj = obj;
				callFunction(parentSearchObj.exeFunction,null,JSON2.stringify(searchObj));
			}
		}
		BIONE.closeDialog("highSearch");
	}
	//动态加载父页面方法
	function callFunction(functionNm,param1,param2){
		eval("parent."+functionNm+"("+param1+","+param2+")");
	}
</script>
</head>
<body>
<div id="template.center">
	<form name="mainform" id="mainform"></form>
</div>
</body>
</html>