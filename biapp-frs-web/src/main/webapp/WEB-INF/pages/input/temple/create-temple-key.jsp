<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template1_9.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/remark/global.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/remark/temple.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var grid, btns, url, ids = [], dialog, buttons = [];
	var updataFlag = '0';
	var rowobjUdp;
	var selection = [ {
		id : "",
		text : "请选择"
	} ];
	var manager;
	$(function() {

		$.ajax({
			async : false,
			url : '${ctx}/rpt/input/data/getColumnList/' + "${id}",
			success : function(data1) {
				/*data1.push({
					id : "SYS_DATA_CASE",
					text : "多批次支持(SYS_DATA_CASE)"
				});
				*/
				selection = data1;
			}
		});
		if(!"${lookType}"){
			searchForm();
		}
		
		initGrid();
		if("${id}") {
			$.ajax({
				url : "${ctx}/rpt/input/temple/findTempleKey/${id}.json?d="+new Date(),
				success : function(ajaxData) {
					var manager = $("#maingrid").ligerGetGridManager();
					for(var i=0;i<ajaxData.length;i++){
						manager.addRow({
							templeId : ajaxData[i].templeId,
							keyName : ajaxData[i].keyName,
							keyType : ajaxData[i].keyType,
							keyColumns : ajaxData[i].keyColumn
						}); 
					
					}
					
				}
			});	
		} 
		if(!"${lookType}"){
			BIONE.createButton({
				appendTo : "#searchbtn",
				text : '添加',
				icon : 'add',
				width : '50px',
				click : function() {
					addNewRow();
				}
			});
			/*去除修改逻辑，直接保存 20190605
			BIONE.createButton({
				appendTo : "#searchbtn",
				text : '修改',
				icon : 'modify	',
				width : '50px',
				click : function() {
					updateKey();
				}
			});
			*/
			BIONE.createButton({
				appendTo : "#searchbtn",
				text : '保存',
				icon : 'save',
				width : '50px',
				click : function() {
					savekey();
				}
			});
		}
		
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '关闭',
			icon : 'delete',
			width : '50px',
			click : function() {
				closeWindos();
			}
		});
		check();
	});
	//搜索表单应用ligerui样式
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				name : "templeId",
				type : "hidden"
			},{
				display : "主键名称<font color='red'>*</font>",
				name : "keyName",
				newline : true,
				type : "text",
				validate : {
					required : true,
					maxlength : 100
				}
				
			}, {
				display : "主键类型",
				name : "keyTypeValue",
				newline : false,
				
				type : 'select',
				options : {
					valueFieldID : 'keyType',
					data : [ {
						text : 'primary',
						id : 'primary'
					}, {
						text : 'unique',
						id : 'unique'
					} ]
				},
				cssClass : "field"
				
			},  {
				display : "主键字段",
				name : 'keyColumnsValue',
				newline : false,
				type : 'select',
				options : {
					isShowCheckBox : true,
					isMultiSelect : true,
					valueFieldID : 'keyColumns',
					data : selection
				}
			} ]
		}

		)

	}
	function isChinese(str){
        if(/[^A-Za-z_]+/.test(str)){
            return true;
        }else{
            return false;
        }
    }
	function addNewRow() {
		var keyName = document.getElementById('keyName').value;

		if(isChinese(keyName)){
			BIONE.tip("主键只能用英文以及下划线组成。");
			return;
		}
		 
		var manager = $("#maingrid").ligerGetGridManager();
		var templeId = ("${id}");
		var keyType = document.getElementById('keyType').value;
		var keyColumns = document.getElementById('keyColumns').value;
		
		if (keyName == "" || keyName == null) {
			BIONE.tip('请填写主键名称。');
			return;
		}
		if (keyType == "" || keyType == null) {
			BIONE.tip('请选择主键类型。');
			return;
		}
		
		if (keyColumns == "" || keyColumns == null) {
			BIONE.tip('请选择主键字段。');
			return;
		}
		var data = manager.getData();
		for(var i =0;i<data.length;i++){
			if(data[i].keyType=='primary' && keyType=='primary' ){
				BIONE.tip('只能存在一个主键。');
				return;
			}
		}
		manager.addRow({
			templeId : templeId,
			keyName:keyName,
			keyType : keyType,
			keyColumns : keyColumns
		});
	 }
	
	function initGrid() {

		grid = manager = $("#maingrid").ligerGrid(
				{
					columns : [
							{
								name : 'templeId',
								hide : 1,
								width : '1%'
							},
							{
								display : '主键名称',
								width : "25%",
								name : 'keyName'
							},
							{
								display : '主键类型',
								name : 'keyType',
								width : '15%',
								render : function(row) {
									switch (row.keyType) {
									case "primary":
										return "primary";
									case "unique":
										return "unique";
									
									}
								}
							},
							{
								display : '主键字段',
								width : "35%",
								name : 'keyColumns'
							},
							{
								display : '操作',
								isSort : false,
								width : '15%',
								render : function(rowdata, rowindex, value) {
									var h = "";
									if (!rowdata._editing && !"${lookType}") {
										h += "<a href='javascript:deleteRow("
												+ rowindex + ")'>删除</a> ";
									}

									return h;
								}
							} ],
					rownumbers : true,
					usePager : false,
					enabledEdit : true,
					clickToEdit : true,
					checkbox : false,
					isScroll : false,
					width : '100%',
					onDblClickRow : function(data, rowindex, rowobj) {
						if(!"${lookType}"){
							liger.get('keyTypeValue').setValue(data['keyType']);
							liger.get('keyColumnsValue').selectValue(data['keyColumns']);
							$("#search [name='keyName']").val(data['keyName']);
							updataFlag = '1';
							rowobjUdp = rowobj;
						}
					},
					data : {
						Rows : [  ]
					},
					height : '99%'
				});

	}
	function deleteRow(rowid) {
		manager.deleteRow(rowid);
	}
	function savekey() {
		var manager = $("#maingrid").ligerGetGridManager(); 
		//去除修改逻辑，直接保存 20190606
		/*
		if(updataFlag == '0'){
			BIONE.tip("请双击要修改的数据修改后再点保存按钮！");
			return;
		}else if(updataFlag == '1'){
			var keyName = document.getElementById('keyName').value;
			var keyType = document.getElementById('keyType').value;
			var keyColumns = document.getElementById('keyColumns').value;
			manager.updateCell("keyType", keyType, rowobjUdp);
			manager.updateCell("keyColumns", keyColumns, rowobjUdp);
			manager.updateCell("keyName", keyName, rowobjUdp);
			updataFlag = '0';
		}*/
		if(rowobjUdp){
			var keyName = document.getElementById('keyName').value;
			var keyType = document.getElementById('keyType').value;
			var keyColumns = document.getElementById('keyColumns').value;
			manager.updateCell("keyType", keyType, rowobjUdp);
			manager.updateCell("keyColumns", keyColumns, rowobjUdp);
			manager.updateCell("keyName", keyName, rowobjUdp);	
		}
		
	    var data = manager.getData();
	    var paramStr="";
	    for(i=0;i<data.length;i++){
	    	paramStr = paramStr + data[i].templeId+","+data[i].keyName+","+data[i].keyType+","+data[i].keyColumns+";;"
	    }
		var info = {
				"paramStr" : paramStr,
				"templeId" : "${id}"
		};
		
		
		$.ajax({
			async : false,
			url : "${ctx}/rpt/input/temple/savaTempleKey.json?d="+new Date(),
			dataType : 'json',
			data : info,
			type : "get",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在保存数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function() {
				BIONE.tip("保存成功！");
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
			
		});
	}
	function closeWindos(){
		BIONE.closeDialog("templeKey");
		
	}
	
	function updateKey(){
		if(updataFlag == '1'){
			var keyName = document.getElementById('keyName').value;
			var keyType = document.getElementById('keyType').value;
			var keyColumns = document.getElementById('keyColumns').value;
			manager.updateCell("keyType", keyType, rowobjUdp);
			manager.updateCell("keyColumns", keyColumns, rowobjUdp);
			manager.updateCell("keyName", keyName, rowobjUdp);
			updataFlag = '0';
		}else{
			BIONE.tip("请双击要修改的数据修改后再点修改按钮！");
		}
	}
	//对输入信息的提示
	function check() {
		$("#keyName").focus(
				function() {
					checkLabelShow(TempleRemark.global.keyName);
					$("#checkLabelContainer").html(
							GlobalRemark.title+ TempleRemark.global.keyName 
									);
				});
		$("#keyTypeValue").change(
				function() {
					checkLabelShow(TempleRemark.global.keyType);
					$("#checkLabelContainer").html(
							GlobalRemark.title+ TempleRemark.global.keyType 
									);
				});
	}
</script>
</head>
<body>
</body>
</html>