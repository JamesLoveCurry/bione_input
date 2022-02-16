<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	//全局变量
	var selectedStoreCol;
	var selectedStoreColCnNm;
	var select
	var grid;
	var gridLoadOver = false;
	var setId  =  '${setId}';
	var storeCol  =  '${storeCol}';
	var setNm  =  '${setNm}';
	var oldSetId =  '${oldSetId}';
	var tableEnNm = '${tableEnNm}';
	function  setSelectedStoreCol(no,cnName){
		selectedStoreCol=  no+'';
		selectedStoreColCnNm=cnName;
	}
	function  isChecked(no,cnName,setId){
		if(!storeCol||storeCol==""){
			return  "";
		}
		if(storeCol==no&&oldSetId==setId){
			selectedStoreCol  =storeCol;
			selectedStoreColCnNm=cnName;
			return  "checked";
		}
		return  "";
	}
	function closePar(){
		setTimeout('parent.BIONE.closeDialog("rptIdxDatasetSrcWin");',0);
	}
	$(function() {
		if(setId){
			//渲染grid
			ligerGrid();
		}
		//渲染GRID
		function ligerGrid() {
			var gridHeight = $("#center").height() - 15
					- $("#tipMainDiv").height() - 6;
			parent.parent.colGrid = grid = $("#maingrid")
					.ligerGrid(
							{
								width : "98%",
								height : gridHeight,
								columns : [
										{
											display : "",
											name : "measureNm",
											width : '8%',
											render : function(row) {
												return '<input   type="radio"  style="padding-top:3px;margin-top:5px" name="measureradio" '+isChecked(row.enNm,row.cnName,row.setId)+' onclick="setSelectedStoreCol(\''+row.enNm+'\',\''+row.cnNm+'\')"/>';
											}
										},
										{
											display : "度量名称",
											name : "cnNm",
											width : '40%'
										},
										{
											display : "度量编码",
											width : '40%',
											name : "enNm"
										}
								],
								onSelectRow : function(rowdata, rowindex) {
									$("#txtrowindex").val(rowindex);
								},
								dataAction : 'server',//从后台获取数据
								method : 'post',
								url : "${ctx}/report/frame/idx/getMeasureColList.json",
								delayLoad : true,//初始化时不加载数据
								enabledEdit : false,
								checkbox:false,
								rownumbers : true,
								usePager : false,
								alternatingRow : false,//奇偶行效果行
								colDraggable : false,
								rowDraggable : false,
								clickToEdit : false,
								onLoading : function() {
									gridLoadOver = false;
								},
								onLoaded : function() {
									gridLoadOver = true;
								},
								toolbar : {}
							});

			//初始化按钮
			var btns = [ {
				text : "重置",
				icon : "refresh",
				click : function() {
					grid.loadData();
					selectedStoreCol=undefined;
				},
				operNo : "col_refresh"
			}];
			BIONE.loadToolbar(grid, btns, function() {
			});


			//加载数据
			grid.set('parms', {
				setId : setId,
				d : new Date().getTime()
			});
			grid.loadData();
		}

		//添加表单按钮
		var btns = [ {
			text : "取消",
			onclick : function() {
				closePar();
			}
		}, {
			text : "保存",
			onclick : f_save
		} ];
		BIONE.addFormButtons(btns);
		//保存
		function f_save() {
			if (!gridLoadOver) {
				BIONE.tip("点击左侧树节点进行度量选择。");
				return;
			}
			if(!selectedStoreCol){
				BIONE.tip("请选择一个度量");
				return;
			}
			if(!selectedStoreColCnNm||selectedStoreColCnNm == "null"){
				selectedStoreColCnNm = "";
			}
			var  addJson ={
					"dsId":setId,
		    		"storeCol":selectedStoreCol,
		    		"tableEnNm":tableEnNm,
		    		"setNm":setNm,
		    		"cnNm":selectedStoreColCnNm
			};
			if(window.parent.parent.sameValidate(addJson)){
   				    window.parent.parent.addSrc(addJson);
			}else {
					if(!window.parent.parent.isModify){//新增
						BIONE.tip("所选度量关联的数据集已被使用，请选择其他数据集的度量");
	   				    return;
	    			}else{//修改，选择了除自己外已存在的记录
	    				var allRowJsonArr= window.parent.parent.getAllRow(window.parent.parent.grid);
	    				var checkedRow = window.parent.parent.grid.getSelectedRows()[0];
	    				if(addJson.dsId==checkedRow.dsId&&addJson.storeCol!=checkedRow.storeCol){//选了同源数据集其他的度量
	    					window.parent.parent.addSrc(addJson);
	    				}else{
		    				for(var  i = 0 ;i<allRowJsonArr.length;i++){
		    					var  obj  =allRowJsonArr[i];
		    					if(addJson.dsId!=checkedRow.dsId&&addJson.dsId==obj.dsId){
		    						     BIONE.tip("所选度量关联的数据集已被使用，请选择其他数据集的度量");
			    	   				    return;
		    					}
		    				}  
		    		   }
	    			}
			}
			closePar();
		}
	});
</script>
<title></title>
</head>
<body>
	<div id="template.center">
		<div id="maingrid"></div>
	</div>
</body>
</html>