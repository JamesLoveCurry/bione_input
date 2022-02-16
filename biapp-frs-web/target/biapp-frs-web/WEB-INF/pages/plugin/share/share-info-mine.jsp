<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1.jsp">
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">

	var grid;
	function initSearchForm() {
		$("#search").ligerForm({
			fields : [{
				display : '报表名称',
				name : "rptNm",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : 'sare.obj_name'
				}
				
			},{
				display : '用户名',
				name : "userName",
				newline : false,
				cssClass : "field",
				attr : {
					op : "like",
					field : 'usr.user_Name'
				}
			}]
		});
	}; 
	
	function initGrid() {	
		grid = $("#maingrid").ligerGrid({
			columns : [{
				display : '报表名称',
				name : 'objName',
				width : "30%",
				type : 'date',
				format : 'yyyy-MM-dd hh:mm:ss',
				align : 'center'
			},/* {
				display : '对象类型',
				name : 'objType',
				width : "10%",
				align : 'center',
				render : function(row,a,val){
					switch(val){
						case "1":
							return "自定义报表"
							break;
						case "2":
							return "指标查询组"
							break;
					}
				}
			}, */{
				display : '分享用户',
				name : 'objUserName',
				width : "25%",
				align : 'center'
			},{
				display : '分享时间',
				name : 'createTime',
				width : "25%",
				align : 'center',
				type : "date",
				format : "yyyy-MM-dd hh:mm:ss"
			},{
				display : '分享状态',
				name : 'shareSts',
				width : "15%",
				align : 'center',
				render : function(row){
					return renderHandler(row);
				}
			}],
			checkbox: false,
			dataAction : 'server', 
			usePager : true, 
			alternatingRow : true,
			colDraggable : true,
			url : "${ctx}/rpt/frame/rptmgr/share/minelist.json",
 			sortName : 'create_Time',//第一次默认排序的字段
 			sortOrder : 'desc', //排序的方式
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			checkbox : false,
			rownumbers : true,
			onAfterShowData : function(){
				if (!$.browser.msie || parseInt($.browser.version, 10) >= 9) {
					if($(".rcswitcher").length > 0){
						$(".rcswitcher").rcSwitcher({
							onText: '分享',
							offText: '取消',
							height:16.5,
							autoFontSize : true
						}).on({
							'turnon.rcSwitcher': function( e, dataObj ){
								var shareId = dataObj.$input.attr("shareId");
								var rowId = dataObj.$input.attr("rowid");
								var shareUserId = grid.getRow(rowId).objUserId;
								var rptName = grid.getRow(rowId).objName;
								if(shareId
										&& shareId){
									changeSts(shareId , '1' , rowId , shareUserId , rptName);
								}
						    },
						    'turnoff.rcSwitcher': function( e, dataObj ){
						    	var shareId = dataObj.$input.attr("shareId");
						    	var rowId = dataObj.$input.attr("rowid");
						    	var shareUserId = grid.getRow(rowId).objUserId;
								var rptName = grid.getRow(rowId).objName;
								if(shareId
										&& rowId){
									changeSts(shareId , '0' , rowId , shareUserId , rptName);
								}
						    }			
						});
					}
				}
			}
		});
	};
	
	function renderHandler(row){
		if(row.shareSts == "0" || row.shareSts == "1"){
			if ($.browser.msie && parseInt($.browser.version, 10) < 9) {
				if (1== row.shareSts){
					return "分享"
				} else {
					return "取消分享";
				}
			}
			var html = "<input class='rcswitcher' type='checkbox' name='check"+row.shareId+"' shareId='"+row.shareId+"' rowId='"+row.__id+"' ";
			if(row.shareSts == "1"){
				html += "checked";
			}
			html += " />";
			return html;
		}
		else{
			if(row.shareSts == "3"){
				return "已发布";
			}
			if(row.shareSts == "4"){
				return "已删除";
			}
		}
	}
	
	// 更改报表状态
	function changeSts(shareId , sts , rowId , shareUserId , rptName){
		$.ajax({
			cache : false,
			async : true,
			url : '${ctx}/rpt/frame/rptmgr/share/changeShareSts',
			dataType : 'json',
			type : "post",
			data : {
				shareId : shareId,
				sts : sts,
				userId : shareUserId,
				rptName : rptName
			},
			beforeSend : function() {
			},
			success : function(result){
				if(rowId
						&& grid){
					var row = grid.getRow(rowId);
					if(row){
						row.shareSts = sts;
					}
				}
			}
		});	
	}
	
	$(function() {	
		initSearchForm();
		initGrid();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	
	
</script>
</head>
<body>
</body>
</html>