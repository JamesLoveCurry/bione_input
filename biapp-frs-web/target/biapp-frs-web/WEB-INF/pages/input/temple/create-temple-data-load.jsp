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
	var selection = [ {
		id : "",
		text : "请选择"
	} ];
	var selection2 = [ {
		id : "",
		text : "请选择"
	} ];
	var manager;
	$(function() {
		$.ajax({
			async : false,
			url : '${ctx}/rpt/input/data/getColumnList/' + "${id}",
			success : function(data1) {
				selection = data1;
			}
		});
		
		$.ajax({
			async : false,
			url : '${ctx}/rpt/input/temple/getTempleDataLoadList',
			success : function(data1) {
				selection2 = data1;
			}
		});
		if(!"${lookType}"){
			searchForm();
		}
		
		initGrid();
		if("${id}") {
			$.ajax({
				url : "${ctx}/rpt/input/temple/findTempleDataLoad/${id}.json?d="+new Date(),
				success : function(ajaxData) {
					var manager = $("#maingrid").ligerGetGridManager();
					for(var i=0;i<ajaxData.length;i++){
						manager.addRow({
							templeId : ajaxData[i].templeId,
							conditionCol:ajaxData[i].conditionColumn,
							conditionVal : ajaxData[i].conditionVal
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
			},  {
				display : "模板字段",
				name : 'conditionColValue',
				newline : true,
				type : 'select',
				options : {
					valueFieldID : 'conditionCol',
					data : selection
				}
			}, {
				display : "过滤值",
				name : "conditionValValue",
				newline : false,
				
				type : 'select',
				options : {
					valueFieldID : 'conditionVal',
					data : selection2
				},
				cssClass : "field"
				
			} ]
		}

		)

	}
	function addNewRow() {
		var manager = $("#maingrid").ligerGetGridManager();
		var templeId = ("${id}");
		var conditionCol = document.getElementById('conditionCol').value;
		var conditionVal = document.getElementById('conditionVal').value;
		
		if (conditionCol == "" || conditionCol == null) {
			BIONE.tip('请选择模板字段。');
			return;
		}
		if (conditionVal == "" || conditionVal == null) {
			BIONE.tip('请选择过滤值。');
			return;
		}
		var data = manager.getData();
		manager.addRow({
			templeId : templeId,
			conditionCol : conditionCol,
			conditionVal : conditionVal
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
								display : '模板字段',
								width : "40%",
								name : 'conditionCol'
							},
							{
								display : '过滤值',
								name : 'conditionVal',
								width : '35%',
								render : function(row) {
									for ( var i = 0; i < selection2.length; i++) {
										switch (row.conditionVal) {
										case selection2[i].id:
											return selection2[i].text;
										}
									}
								}
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
					enabledEdit : true,
					clickToEdit : true,
					isScroll : false,
					usePager : false,
					height : '99%',
					width : '100%',
					data : {
						Rows : []
					} 
				});

	}
	function deleteRow(rowid) {
		$.ligerDialog.confirm('确实要删除这条记录吗?', function(yes) {
			if (yes) {
				manager.deleteRow(rowid);
			}
			
		})
	}
	function savekey() {
		var manager = $("#maingrid").ligerGetGridManager(); 
	    var data = manager.getData();
	    var paramStr="";
	    for(i=0;i<data.length;i++){
	    	paramStr = paramStr + data[i].templeId+","+data[i].conditionCol+","+data[i].conditionVal+";;"
	    }
		var info = {
				"paramStr" : paramStr,
				"templeId" : "${id}"
				
		};
		
		
		$.ajax({
			async : false,
			url : "${ctx}/rpt/input/temple/savaTempleDataLoad.json?d="+new Date(),
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
	//对输入信息的提示
	function check() {
		if(!"${lookType}"){
			$("#conditionColValue").click(
					function() {
						checkLabelShow(TempleRemark.global.conditionColValue);
						$("#checkLabelContainer").html(
								GlobalRemark.title+ TempleRemark.global.conditionColValue 
										);
					});
		}
	}
</script>
</head>
<body>
</body>
</html>