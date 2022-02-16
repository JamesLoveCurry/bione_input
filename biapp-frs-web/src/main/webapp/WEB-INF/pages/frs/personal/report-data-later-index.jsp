<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	// 全局变量
	var grid;
	var nodeType;
	var realId;
	var dialogWidth = 1000;
	var dialogHeight = 500;
	var moduleType = '${moduleType}';
	var selRowNo;
	var orgType = '02';
	var pageName = "report-data-later-index";
	var orgTreeSkipUrl = "${ctx}/frs/rptfill/reject/searchOrgTree?orgType=" + moduleType;

	
	$(function() {
		var parent = window.parent;
		// 初始化dialog高、宽
		dialogWidth = $(parent.window).width() * 0.95;
		dialogHeight = $(parent.window).height() - 30;
		//初始化
		ligerSearchForm();
		ligerGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");

		//渲染查询表单
		function ligerSearchForm() {
			$("#search").ligerForm({
				fields : [{
					display : "机构",
					name : "rptNm",
					newline : true,
					labelWidth : 120, 
					width : 150, 
					type : "select",
					cssClass : "field",
					comboboxName : "handSts_sel",
					attr : { op : "=", field : "due.open_combank_org_id"},
					options : {
					     onBeforeOpen : function() {
						       var rptFillFlag = "detail";
						       var height = $(window).height() - 120;
						       var width = $(window).width() - 480;
						       window.BIONE.commonOpenDialog("机构树", "taskOrgWin", width, height, orgTreeSkipUrl+"&rptFillFlag="+rptFillFlag+"&Dname=handSts_sel", null);
						       return false;
						     }, 
						 cancelable  : true
					}
				},{
					display : "查询开始时间",
					name : "date1",
					newline : false,
					labelWidth : 120, 
					width : 150, 
					type : "date",
					options : {
						format : "yyyyMMdd",
						 onChangeDate : function(){				          
				              var beginTime = $("#date1").val();
					          var endTime = $("#date2").val();
					        	
					          if(beginTime!=null && beginTime!='' && endTime!=null && endTime!=''){
									//var e_date = new Date(endTime);
									//var b_date = new Date(beginTime);
									if(beginTime-endTime > 0){
										BIONE.tip("开始时间大于结束时间。请重新输入！");
									        }
								    }   			
				        }	   
						
					},
					attr : {
						field : "to_char(overdue_date,'yyyymmdd')",
						op : ">="
					}
				},{
					display : "查询截止时间",
					name : "date2",
					newline : false,
					labelWidth : 120, 
					width : 150, 
					type : "date",
					options : {
						format : "yyyyMMdd",
						 onChangeDate : function(){
				              var beginTime = $("#date1").val();
					          var endTime = $("#date2").val();					        	
					          if(beginTime!=null && beginTime!='' && endTime!=null && endTime!=''){
									//var e_date = new Date(endTime);
									//var b_date = new Date(beginTime);
									if(beginTime-endTime > 0){
										BIONE.tip("开始时间大于结束时间。请重新输入！");
									        }
								    }   			
				        }	   
					},
					attr : {
						field : "to_char(overdue_date,'yyyymmdd')",
						op : "<="
					}
				}]
			});
		}
		
		
		//初始化按钮
		function initButtons() {
			var btns = [ {
				text : "数据下载",
				icon : "download",
				click : download,
				operNo : "download"
			}
			];
			BIONE.loadToolbar(grid, btns, function() {
			});
		}
		
		
		//数据下载
		function download(){
			download = $('<iframe id="download"  style="display: none;"/>');
	        $('body').append(download);
	        var data = queryConditionList();
	 	    var condition = data.condition;
			$.ajax({
				type : "POST",
				url : "${ctx}/frs/personal/personalAccount/getCount?pageName="+pageName+"&moduleType="+moduleType+"&condition="+condition,
				success : function(result) {
					if(result>10000){
						$.ligerDialog.confirm("数据量太大,仅支持前一万条记录的下载,是否继续下载?", function(yes) { 
							if(yes){
						 	    var src = "${ctx}/frs/personal/personalAccount/downLoad?pageName="+pageName+"&moduleType="+moduleType+"&isDownload=Y&condition="+condition+"&count=10000";
						 		download.attr('src', src);
							}
						})
					}else{
				 	    var src = "${ctx}${ctx}/frs/personal/personalAccount/downLoad?pageName="+pageName+"&moduleType="+moduleType+"&isDownload=Y&condition="+condition;
				 		download.attr('src', src);
					}
				}
			});
		}
		
		//获取查询条件数据
	    function queryConditionList(){
	   	   var form = $('#formsearch');
	  	   var data = {};
	  	   var rule = BIONE.bulidFilterGroup(form);
	        if (rule.rules.length) {
	  			data.condition=JSON2.stringify(rule);
	  		}else{
	  			data.condition="";
	  		}
	        return data; 
	     }

		//渲染GRID
		function ligerGrid() {
			parent.rptGrid = grid = $("#maingrid").ligerGrid({
				width : "100%",
				height : "99%",
				columns : [ {
					display : "机构",
					name : "openCombankOrgName",
					width : "20%",
					align: "center"
				} ,{
					display : "逾期日期",
					name : "overdueDate",
					width : "10%",
					align: "center",
					render : function(rowdata){
						return rowdata.overdueDate.substr(0,10);
					}
				},{
					display : "任务数量",
					name : "count",
					width : "10%",
					align: "center"
				},{
					display : "柜员号码",
					name : "tellerNo",
					width : "10%",
					align: "center"
				},{
					display : "任务人员",
					name : "tellerNm",
					width : "10%",
					align: "center"
				},{
					display : "逾期已完成数量",
					name : "finished",
					width : "10%",
					align: "center",
					render : function(rowdata){
						if(rowdata.finished!=null){
							return rowdata.finished;
						}else{
							return "0";
						}
					}
				},{
					display : "逾期未完成数量",
					name : "unfinished",
					width : "10%",
					align: "center",
					render : function(rowdata){
						if(rowdata.unfinished!=null){
							return rowdata.unfinished;
						}else{
							return "0";
						}
					}
				} ],
				checkbox : false,
				userPager : true,
				rownumbers : true,
				alternatingRow : true,//附加奇偶行效果行
				colDraggable : false,
				selectRowButtonOnly : true ,
				dataAction : 'server',//从后台获取数据
				method : 'post',
			/* 	toolbar : {}, */
				url : "${ctx}/frs/personal/personalAccount/getReportList?pageName="+pageName+"&moduleType="+moduleType,
	            delayLoad:false,
	            isScroll : true
			});
		}

	});
	
	function test(id, text,Dname){
		$.ligerui.get(Dname).setValue(id);
	 	$.ligerui.get(Dname).setData(id);
	 	$.ligerui.get(Dname).setText(text);
	}
	
</script>
</head>
<body>
</body>
</html>