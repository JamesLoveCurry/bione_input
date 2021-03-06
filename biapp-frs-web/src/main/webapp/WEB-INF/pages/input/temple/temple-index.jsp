<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<head>
<meta name="decorator" content="/template/template2.jsp">
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>

<script type="text/javascript">
	var id, dirId = '',taskState = "";var checkedTempleId = [],checkedTempleName = [];;
	var currentNode;
	var tempState = "0";
	var uri = '${ctx}/rpt/input/temple/list.json';
	$(function() {
		initTree();
		searchForm();
		initGrid();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		/*目录操作方式修改20190612
		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				icon : 'communication',
				text : '目录管理',
				click : dirMananer,
				operNo : 'objDef-management'
			} ]
		});
		*/
		$("#treeToolbar").ligerTreeToolBar({
			items:[{
				icon:'menu',
				text:'目录维护',
				operNo : 'taskCatalog',
				color :'#fcca54',
				menu:{
					width : 90,
					items : [ {
						icon : 'add',
						text : '新建',
						click : addIdxCatalog
					}, {
						line : true
					}, {
						icon : 'modify',
						text : '修改',
						click : updateIdxCatalog
					},{
						icon : 'delete',
						text : '删除',
						click : deleteIdxCatalog
					}, {
						line : true
					}]
				}
			}]
		});
		BIONE.loadToolbar(grid, [ {
			text : '添加',
			click : objDef_add,
			icon : 'fa-plus',
			operNo : 'objDef_add'
		}, {
			text : '修改',
			click : beginEdit,
			icon : 'icon-modify',
			operNo : 'objDef_mod'
		}, {
			text : '删除',
			click : deleteRow,
			icon : 'icon-delete',
			operNo : 'objDef_del'
		},
//		{
//			text : '导入',
//			click : temp_imp,
//			icon : 'import',
//			operNo : 'objDef_imp'
// 		}, {
// 			text : '导出',
// 			click : temp_exp,
// 			icon : 'export'
// 		}, {
// 			text : '启用/停用',
// 			click : startOrEnd,
// 			icon : 'start'
//		},
		{
			text : '模板下载',
			click : downTemp,
			icon : 'fa-download',
			operNo : 'objDef_down'
		}, {
			text : '设置回写表',
			click : reWrite,
			icon : 'icon-export',
			operNo : 'objDef_rew'
		} ]);
		
		
		
		if ("${id}") {
			var templeName = window.parent.templeName.val();
			var templeId = window.parent.templeId.val();
			var roleId = templeId.split(',');
			var roleName = templeName.split(',');
			if(roleId.length>0 && templeId != ""){
				for(i=0;i<roleId.length;i++){
					checkedTempleId.push(roleId[i]);
				}
				
			}
			if(roleName.length>0 && templeName != ""){
				for(j=0;j<roleName.length;j++){
					checkedTempleName.push(roleName[j]);
				}
			}
			
			BIONE.createButton({
				appendTo : "#searchbtn",
				text : '选择',
				icon : 'true',
				width : '50px',
				click : function() {
					var templeName2 = checkedTempleName;
					var templeId2 = checkedTempleId;
					$.ajax({
						url : '${ctx}/rpt/input/temple/findTempleFile',
						type : 'get',
						async : true,
						data : {
							"templeId" : templeId2+""
						},
						success : function(data2) {
							if (data2) {
								window.parent.templeName.val(templeName2);
								window.parent.templeId.val(templeId2);
								window.parent.templeName.focusout();
								BIONE.closeDialog("templeList");
							} else {
								BIONE.tip("存在模板未启用或无启用Excel模板。");
								return;
							}
						}
					});

				}
			});
		}
		$("#tableName").focusout(function() {
			var tableName = document.getElementById("tableName").value;
			$.ligerui.get("tableName").setValue(tableName.toUpperCase());
		});
	});
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "模板名称",
				name : "templeName",
				newline : true,
				width:140,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "temple.templeName"
				}
			}, {
				display : "模板状态",
				name : "stateValue",
				width:140,
				newline : false,
				type : 'select',
				options : {
					valueFieldID : 'state',
					data : [ {
						text : '启用',
						id : '1'
					}, {
						text : '停用',
						id : '0'
					} ]
				},
				cssClass : "field",
				attr : {
					op : "=",
					field : "temple.templeSts"
				}
			}, {
				display : "物理表名",
				width:140,
				name : "tableName",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "temple.tableEnName"
				}
			} ]
		});
	}
	function initGrid() {
		grid = manager = $("#maingrid").ligerGrid(
				{
					width : '100%',
					height : '99%',
					columns : [
							{
								name : 'templeId',
								hide : 1,
								width : '0.5%'
							},
							{
								isSort:true,
								display : '模板名称',
								name : 'templeName',
								align : 'left',
								width : '17%',
								minWidth : '20%',
								render:function(row){
									//判断clientEnv避免js报错
									if(top.window.clientEnv != undefined && top.window.clientEnv["isSuper"] != 'true'){
										if (!top.window["authorizedResOperNo"]
												|| ('objDef_mod' && $.inArray('objDef_mod',top.window["authorizedResOperNo"]) == -1)) {
											return row.templeName;
										}
									}
									return  "<a href='javascript:void(0)'   onclick='getSelectRow(\""+row.templeId+"\")'>"+row.templeName+"</a>";
								}	
							},
							{
								isSort:true,
								display : '物理表名',
								name : 'tableEnName',
								align : 'left',
								width : '18%',
								minWidth : '20%'
							},{
								isSort:true,
								display : '创建日期',
								name : 'createDate',
								align : 'left',
								width : '10%',
								minWidth : '20%',
								format : 'yyyyMMdd'
							},
							{
								isSort:true,
								display : "模板状态",
								name : "templeSts",
								width : '5%',
								minWidth : '10%',
								editor : {
									type : 'select',
									data : [ {
										'id' : '1',
										'text' : '启用'
									}, {
										'id' : '0',
										'text' : '停用'
									} ]
								},
								render : function(row) {
									switch (row.templeSts) {
									case "1":
										return "启用";
									case "0":
										return "停用";
									}
								}
							},
							{
								display : '描述',
								name : 'remark',
								align : 'center',
								width : '32%',
								minWidth : '10%'
							} ,
							{
								display : '操作',
								name : '',
								align : 'center',
								width : '10%',
								minWidth : '10%',
								render : function (row,rowNum,value){
									var showText = "";
									var rewriteText = "启动回写";
									if(row.isStart&&row.isStart=="1"){
										rewriteText = "取消回写";
									}
									

									sendText = "发送短信";
									if(row.isSendNotify&&row.isSendNotify=="1"){
										var sendText = "不发送短信";
									}
									
									return "<a href='javascript:void(0)'   onclick='changeIsStart(\""+row.templeId+"\",\""+row.isStart+"\")'>"+rewriteText+"</a>";
									/* return "<a href='javascript:void(0)'   onclick='changeIsStart(\""+row.templeId+"\",\""+row.isStart+"\")'>"+rewriteText+"</a>"+
									"&nbsp;&nbsp;"+
									"<a href='javascript:void(0)'   onclick='changeIsSendNotify(\""+row.templeId+"\",\""+row.isSendNotify+"\")'>"+sendText+"</a>"; */
								}
							}],
					url : uri+"&d=new Date()",
					checkbox : true,
					width : "100%",
					rownumbers : true,
					dataAction : 'server', //从后台获取数据
					usePager : true, //服务器分页
					alternatingRow : true, //附加奇偶行效果行
					colDraggable : true,
					sortName : 'createDate',//第一次默认排序的字段
					sortOrder : 'desc', //排序的方式
					pageSizeOptions: [100,200,500],
					pageSize: 100, 
					method : 'post',
					pageParmName : 'page',
					pagesizeParmName : 'pagesize',
					toolbar : {},
					onDblClickRow : function (data, rowindex, rowobj){
						getSelectRow(data.templeId);
						//manager.loadData(); 
	                },
	                onCheckRow: f_onCheckRow, onCheckAllRow: f_onCheckAllRow,
					isChecked : f_isChecked
				});
	}
	
	function changeIsStart(templeId,isStart){
		$.ajax({
			async : false,
			type : "POST",
			url : "${ctx}/rpt/input/temple/changeIsStart?templeId="+templeId+"&isStart="+isStart+"&d="+new Date().getTime(),
			success : function() {
				manager.loadData();
			}
		});
	}
	
	function changeIsSendNotify(templeId,isSendNotify){
		$.ajax({
			async : false,
			type : "POST",
			url : "${ctx}/rpt/input/temple/changeIsSendNotify?templeId="+templeId+"&isSendNotify="+isSendNotify+"&d="+new Date().getTime(),
			success : function() {
				manager.loadData();
			}
		});
	}
	
	
	function f_isChecked(rowdata){
	    if ("${id}") {
			
			var templeId = window.parent.templeId.val();
			var roles = templeId.split(',');
			if( $.inArray(rowdata.templeId, roles) != -1 || (findcheckedTemple(rowdata.templeId,checkedTempleId) != -1 && findcheckedTemple(rowdata.templeName,checkedTempleName) != -1)){
				return true;
			}
			return false;
		}else{
			if (findcheckedTemple(rowdata.templeId,checkedTempleId) == -1)
		        return false;
			return true;
		}
	    
	}
	///////////////////////////
	function f_onCheckAllRow(checked){
        for (var rowid in this.records){
            if(checked){
                addcheckedTemple(this.records[rowid]['templeId'],checkedTempleId);
                addcheckedTemple(this.records[rowid]['templeName'],checkedTempleName);
            }else{
            	removecheckedTemple(this.records[rowid]['templeId'],checkedTempleId);
            	removecheckedTemple(this.records[rowid]['templeName'],checkedTempleName);
            }
        }
    }

    /*
    该例子实现 表单分页多选
    即利用onCheckRow将选中的行记忆下来，并利用isChecked将记忆下来的行初始化选中
    */
    
    function findcheckedTemple(templeInfo,checkedTemple){
        for(var i =0;i<checkedTemple.length;i++){
            if(checkedTemple[i] == templeInfo) return i;
        }
        return -1;
    }
    function addcheckedTemple(templeInfo,checkedTemple){
        if(findcheckedTemple(templeInfo,checkedTemple) == -1){
        	checkedTemple.push(templeInfo);
        }
    }
    function removecheckedTemple(templeInfo,checkedTemple){
        var i = findcheckedTemple(templeInfo,checkedTemple);
        if(i==-1){
        	return;
        }else{
        	checkedTemple.splice(i,1);
        }
    }
   
    function f_onCheckRow(checked, data){
        if (checked) {
        	addcheckedTemple(data.templeId,checkedTempleId);
        	addcheckedTemple(data.templeName,checkedTempleName);
        } else {
        	removecheckedTemple(data.templeId,checkedTempleId);
        	removecheckedTemple(data.templeName,checkedTempleName);
        }
    }
    function f_getChecked(){
    	
    }

	function initTree() {
		taskTree = $.fn.zTree.init($("#tree"), {
			async : {
				enable : true,
				url : "${ctx}/rpt/input/catalog/templelistTree.json",
				autoParam:["id=nodeId"],
				dataType : "json"
			},
			data : {
				keep : {
					parent : false
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
				showLine : false
			},
			callback : {
				onClick : zTreeOnClick
			}
		});

		$("#treeTitle").text("模板目录树");
	}
	//树点击事件
	function zTreeOnClick(event, treeId, treeNode) {
		dirId = treeNode.id;
		grid.set('newPage', 1);
		grid.set('url', '${ctx}/rpt/input/temple/list.json?'+ (dirId == null ? "" : 'realId=' + dirId));
		
		currentNode = treeNode;
	} 
	
	function  addIdxCatalog(){
		var id,name ;
		if (!currentNode) {
			BIONE.tip("请选择目录");
			return;
		} else{
			upNo = currentNode.id;
			upName = currentNode.text;
		}
		var modelDialogUri = "${ctx}/rpt/input/catalog/newCatalog?upNo=" + upNo+"&upName="+encodeURI(encodeURI(upName));
		BIONE.commonOpenDialog("目录添加", "inputEditCatalog","660","320",modelDialogUri);
	}
	function  updateIdxCatalog(){
		
		var id,upName;
		if (!currentNode) {
			BIONE.tip("请选择目录");
			return;
		}else {
			parentNode = currentNode.getParentNode();
			if(parentNode ==null)
			 {
				upName = "根目录";
				BIONE.tip("根目录不允许修改！");
				return;
			}else{
				id=currentNode.id;
				upName = parentNode.text;
			}
		}
		var modelDialogUri = "${ctx}/rpt/input/catalog/initUpdateCatalog?catalogId=" + id+"&upName="+ encodeURI(encodeURI(upName));
		BIONE.commonOpenDialog("目录修改", "inputEditCatalog","660","320",modelDialogUri);
	}
	function deleteIdxCatalog() {
		
		if (!currentNode) {
			BIONE.tip("请选择目录");
			return;
		}else {
			parentNode = currentNode.getParentNode();
			if(parentNode ==null)
			 {
				upName = "根目录";
				BIONE.tip("根目录不允许删除！");
				return;
			}
		}
		
		$.ajax({
			type : "POST",
			url : '${ctx}/rpt/input/catalog/canDeleteCatalog?catalogId=' + currentNode.id,
			success : function(result) {
				if(result == 0){
					BIONE.tip("此目录下有补录模板，不能进行删除操作！");
				}else if(result == 1){
					taskTree.reAsyncChildNodes(currentNode.getParentNode(), "refresh");
					BIONE.tip("删除成功！");
					currentNode = null;
				}
			}
		});
	}
	
	function dirMananer() {
		BIONE.commonOpenDialog("模板目录管理", "dirmanager", 800, 450,
				"${ctx}/rpt/input/catalog/1", null);
	}
	
	function beginEdit() {
		achieveIds();
		manager.loadData(); 
		//grid.reRender();
		if (ids.length == 1) {
			getSelectRow(ids[0]);
		}else if (ids.length > 1) {
			BIONE.tip("只能选择一行进行修改");
		} else {
			BIONE.tip("请选择一条数据进行修改");
			return;
		}
	}

	function deleteRow() {

		chackTempleTask();
		if(taskState == "1"){
			if (ids.length > 0) {
				$.ligerDialog.confirm('确实要删除这些记录吗?', function(yes) {
					if (yes) {
						var flag;
						$.ajax({
							async : false,
							type : "POST",
							url : "${ctx}/rpt/input/temple/" + ids.join(',')+"?d="+new Date().getTime(),
							success : function(data) {
								flag = data;
							}
						});
						if (flag == 'true') {
							BIONE.tip('删除成功');
							manager.loadData(); 
						} else if(flag == 'check'){//删除前校验是否有任务关联模板 20190605
							BIONE.tip('所选模板中已有任务关联，请解除关联后删除');
						}else {
							BIONE.tip('删除失败');
						}
					}
				});
			}else {
				BIONE.tip("请选择数据进行删除");
				return;
			}
		}else{
			BIONE.tip("模板已被任务使用，并且任务已启用。");
			return;
		}
		

	}

	function objDef_add(item) {
		if (!currentNode || currentNode.getParentNode()==null) {
			BIONE.tip("请选择非根目录！");
			return;
		}
		else
			{
				BIONE.commonOpenDialog('添加模板', 'objDefManage', $(document).width() - 50, $(document).height() - 30,
						'${ctx}/rpt/input/temple/new?catalogId=' + currentNode.id+'&catalogName='+currentNode.text);
			}
		}


	// 获取选中的行
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].templeId);
			tempState = rows[i].state;
		}
	}

	function temp_imp() {
		if (dirId == '') {
			BIONE.tip('请选择要导入模板的目录');
			return;
		}
		BIONE.commonOpenDialog('文件导入', 'upLoad', 562,
				334, '${ctx}/rpt/input/temple/impTempleData?dirId=' + dirId+"&d="+new Date().getTime());
	}

	function temp_exp() {
		achieveIds();
		if (ids.length > 0) {
			if (!document.getElementById('download')) {
				$('body').append(
						$('<iframe id="download" style="display:none"/>'));
			}
			$("#download").attr('src',
					'${ctx}/rpt/input/temple/fileDownTemp?tempId=' + ids+"&d="+new Date().getTime());

		} else {
			BIONE.tip('请选择要导出的模板');
		}
	}
	
	function startOrEnd(){
		achieveIds();
		chackTempleTask();
		if(taskState == "1"){
			if (ids.length == 1) {
				$.ajax({
					async : false,
					url : '${ctx}/rpt/input/temple/chackTempleState/' + ids[0]+"?d="+new Date().getTime(),
					success : function(data) {
						BIONE.tip(data);
						manager.loadData(); 
					}
				});
			} else if (ids.length > 1) {
				BIONE.tip("只能选择一行进行更改");
			} else {
				BIONE.tip("请选择一条数据进行更改");
				return;
			} 
		}else{
			BIONE.tip("模板已被任务使用，并且任务已启用。");
			return;
		}
	}
	
	function chackTempleTask(){
		achieveIds();
		$.ajax({
			url : '${ctx}/rpt/input/temple/chackTempleTask?d='+new Date().getTime(),
			async : false,
			dataType : 'text',
			type : "post",
			data : {
				"templeId" : ids.join(',')
			},
			success : function(result) {
				if (result == 'ok') {
					taskState = "1";
				} else {
					taskState = "0";
				}
			}
		});
	}
	function getTempleUrl(){
		return '${ctx}/rpt/input/temple/list.json';
	}
	function getSelectRow(id){
		BIONE.commonOpenDialog("修改模板", "objDefManage",$(document).width()-50, $(document).height()-30,
				"${ctx}/rpt/input/temple/edit?id=" + id+"&d=" + new Date().getTime());
	}
	
	function reWrite(){

		var rows = grid.getSelectedRows();
		if(rows.length!=1){
			BIONE.tip("请选择一条记录进行补录");
			return ;
		}
		var options = {
			url : "${ctx}/rpt/input/temple/setReWrite?templeId="+rows[0].templeId,
			dialogname : 'selectSchemaTableBox',
			title : '设置回写表'
		};
		BIONE.commonOpenDialog(options.title, options.dialogname,$(document).width()-50,$(document).height()-30,options.url);
	}
	
	//模板下载
	function downTemp(){
		achieveIds();
		if (ids.length == 1) {
			if (tempState == "0"){
				BIONE.tip("模板已停用。");
				return;
			}
			
			$.ajax({
				url : '${ctx}/rpt/input/temple/findTempleFile',
				type : 'get',
				async : false,
				data : {
					"templeId" : ids[0]
				},
				success : function(data) {
					if (data) {
						if (!document.getElementById('download')) {
							$('body').append(
									$('<iframe id="download" style="display:none"/>'));
						}
						$("#download").attr('src',
								'${ctx}/rpt/input/temple/excel_down_temp_index/' + ids[0]+"?d="+new Date().getTime());
						
					} else {
						BIONE.tip("无启用Excel模板。");
						return;
					}
				}
			});
			
			
		} else if (ids.length > 1) {
			BIONE.tip("只能选择一行进行下载");
		} else {
			BIONE.tip("请选择一条数据进行下载");
			return;
		} 
	}
</script>
</head>
<body>
	<div id="template.left.up">
		<span id="treeTitle" style="font-size: 12">功能树</span>
	</div>
</body>
</html>