<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template22.jsp">
<style>
#bottom {
	position: relative;
	width: 100%;
	margin-left: auto;
	margin-right: auto;
	overflow: hidden;
	border-top: 1px solid #C6C6C6;
}
</style>
<script type="text/javascript">
	var grid;
	$(function() {
		var currentNode;
		var tabObj;
		var leftTreeObj;
		var newFlag =false;
		var async = {
				data : {
					keep : {
						parent : true
					},
					key : {
						name : "text"
					},
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId",
						rootPId : null
					}
				},
				view : {
					selectedMulti : false,
					showLine : false
				},
				callback:{
					onClick : f_clickNode
				}
			};
		// 加载树节点
		var initNodes = function(searchText){
			if(leftTreeObj == null 
					|| typeof leftTreeObj == "undefined"){
				return ;
			}
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frame/dataset/getDataModuleTree.json?d=" + new Date().getTime(),
				dataType : 'json',
				type : "post",
				data : {
					searchNm:searchText,
				},
				success : function(result){
					if(result != null
							&& typeof result != "undefined"){
						// 移除旧节点
						leftTreeObj.removeChildNodes(leftTreeObj.getNodeByParam("id", '0', null));
						leftTreeObj.removeNode(leftTreeObj.getNodeByParam("id", '0', null),false);
						// 渲染新节点
						leftTreeObj.addNodes(leftTreeObj.getNodeByParam("id", '0', null) , result , true);
						leftTreeObj.expandAll(true);
					}
				},
				error:function(){
					BIONE.tip("加载失败，请联系系统管理员");
				}
			});
		}	
		
		 //树点击事件
		function f_clickNode(event, treeId, treeNode, clickFlag) {
			newFlag =false;
			currentNode = treeNode;
			var  type = currentNode.params.type;
//	 		if(currentNode.id=='0'){
//	 			initCurrentTab('','','');
//	 			return;
//	 		}
			if(type!="setInfo"){
				return ;
			}else{
				initGrid(currentNode.data.setId,currentNode.data.setNm);
			}
			
		} 	
		function  initGrid(setId, setNm){
			var groupData = [ {
				'id' : 'sum',
				'text' : '累加'
			}, {
				'id' : 'avg',
				'text' : '平均'
			}, {
				'id' : 'max',
				'text' : '最大'
			}, {
				'id' : 'min',
				'text' : '最小'
			}, {
				'id' : 'count',
				'text' : '计数'
			} ];
			
			var height = $("#maingrid").height();
			grid = $("#maingrid").ligerGrid(
				{
					width : "98%",
					height: height,
					clickToEdit : true,
					enabledEdit : true,
					checkbox : true,
					columns : [
							{
								display : "维度名称",
								name : "dimTypeName",
								width : '20%'
							},
							{
								display : "度量名称",
								width : '20%',
								name : "cnNm",
								render: function(row){
									if(row.colType == "01")
										return row.measureName || row.cnNm; 
								}
							},{
								display: '度量聚合',
								width: '20%',
								name: 'group',
								editor : {
									type : 'select',
									data : groupData
								},
								render : function(row) {
									if(row.colType == "01"){
										if(row.group){
											for(var i=0;i<groupData.length;i++){
												if(groupData[i].id == row.group){
													return groupData[i].text;
												}
											}
										}else{
											return groupData[0].text;
										}
									}else{
										return "";
									}
								}  
							},{
								display: '维度过滤',
								width: '25%',
								name: 'dimFilter',
								editor: {
									type: 'select',
									onChanged: function(rowdata){
									},
									ext: function(rowdata){
	            	    				return {
			            	    			onBeforeOpen: function(){
			            	    				window.rowdata = rowdata;
			            	    				var modelDialogUri = "${ctx}/report/frame/valid/logic/dimSelect?dimTypeNo="+rowdata.dimTypeNo;
			            	    				BIONE.commonOpenDialog("维度类型选择", "chooseDimItems",400,300, modelDialogUri);
			            	    				return  false;
			            	    			} ,render :function(){
			            	    				if(rowdata.dimFilter){//初始化和后续操作
													return  rowdata.dimFilter;
												}
			            	    				return   "";
			            	    			} 
	            	    				}
	            	    			}
								}
							}
					],
					onBeforeEdit : function(e) {
						var rowdata=e.record;
						if(e.column.columnname=="dimFilter" && rowdata.colType == "02"){
							if(rowdata.dimTypeNo == "DATE" || rowdata.dimTypeNo == "ORG" ||  rowdata.dimTypeNo == "INDEXNO" ){
								BIONE.tip("基础维度不能过滤");
								return false;
							}
							return true;
						} 
						return false;//return (colName != "enNm" && colName != "dbType");
					},
					dataAction : 'server',//从后台获取数据
					method : 'post',
					url : "${ctx}/report/frame/valid/logic/getModelInfo?setId=" + setId,
					rownumbers : true,
					usePager : false,
					alternatingRow : false,//奇偶行效果行
					colDraggable : false,
					rowDraggable : false,
					toolbar : {}
				});
		}
		// 搜索动作
		var searchHandler = function(){
			var searchName = $("#treeSearchInput").val();
			initNodes(searchName);
		}
		
		//初始化树查询按钮事件
		$("#treeSearchIcon").bind("click",function(e){
			searchHandler();
		});
		$("#treeSearchInput").bind("keydown",function(e){
			// 按下回车键进行查询
			var code = e.keyCode;
			if(code == null
					|| typeof code == "undefined"
					|| code != "13"){
				return ;
			}
			searchHandler();
		});
		$("#treeContainer").height($("#left").height() - $("#lefttable").height() - $("#treeToolBar").height() - $("#treeSearchbar").height());
		leftTreeObj = $.fn.zTree.init($("#tree"),async,[]);
		initNodes("");
		var btns = [ {
			id: 'cancel',
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("chooseModel");
			}
		}, {
			text : "保存",
			onclick : function(){
				if(grid){
					var selectRows = grid.getSelectedRows();
					if(selectRows && selectRows.length > 0){
						var string = "";
						var flag = 0;
						for(var i=0;i<selectRows.length;i++){
							if(selectRows[i].colType == "01"){
								flag++;
								if(selectRows[i].group){
									string += "[" + selectRows[i].group + "(" + selectRows[i].enNm + ")" + "],";
								}else{
									string += "[sum("+ selectRows[i].enNm + ")],";
								}
								
							}else{
								if(selectRows[i].dimFilter){
									string += "[" + selectRows[i].enNm + "(" + selectRows[i].dimFilter + ")],";
								}else{
									string += "[" + selectRows[i].enNm + "],";
								}
								
							}
						}
						if(flag == 0){
							BIONE.tip("必须选择一个度量");
							return;
						}
						if(flag > 1){
							BIONE.tip("只能选择一个度量");
							return;
						}
						if(string[string.length - 1] == ","){
							string = string.substring(0, string.length - 1 );
						}
						string = "Sql('" + currentNode.data.setNm + "','" + string + "')";
						window.parent.addText(string);
						BIONE.closeDialog("chooseModel");
						
					}else{
						BIONE.tip("请选择维度和度量");
					}
				}
			}
		} ];
		BIONE.addFormButtons(btns);
		$("#maingrid").height($("#tab").height() - $("#bottom").height() - 3);
	});
</script>
<title></title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">数据集选择</span>
	</div>
	<div id="template.right">
		<div id="tab" style="width: 100%; overflow: hidden;">
		<div id="maingrid"></div>
		<div id="bottom">
		<div class="form-bar">
			<div class="form-bar-inner" style="padding-top: 5px"></div>
		</div>
		</div>
		
	</div>
	</div>
</body>
</html>