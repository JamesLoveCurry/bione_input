<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template14.jsp">
<head>
<style>
.singlebox,#allSelect{
  margin-top:5px;
}
</style>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
    //var model_column_width="19%";     //模式列表栏宽度
	var searchNm = '${searchNm}';
	var checkNum = "0,1,2,3";
	
	$(function() {
		$("#center").prepend(
				'<div id="tipMainDiv"'
					+'style="width: 99.8%; margin: 0 auto; overflow: hidden; position: relative; border: 1px solid gray; padding-top: 1px; padding-bottom: 1px;">'
					+'<div id="tipContainerDiv"'
					   +'style="padding: 5px 2px; background: #fffee6; color: #8f5700;">'
					   +'<div id="tipAreaDiv">'
							+'tips : 可勾选搜索类型对下方表格中的结果进行进一步筛选<br />'
						+'</div>'
					+'</div>'
				+'</div>'
				+'<div>'
				    +'<a style="font-size:13px;margin-top:1%;margin-left: 1%;margin-bottom: 1%; ">指标</a><input id="chooseIdx" type="checkbox" onclick="checkList()" name="checkboxList" checked="checked" style="margin-top:1%;margin-left:0.5%;margin-bottom: 1%;">'
				    +'<a style="font-size:13px;margin-top:1%;margin-left: 1%;margin-bottom: 1%; ">报表</a><input id="chooseRpt" type="checkbox" onclick="checkList()" name="checkboxList" checked="checked" style="margin-top:1%;margin-left:0.5%;margin-bottom: 1%;">'
				    +'<a style="font-size:13px;margin-top:1%;margin-left: 1%;margin-bottom: 1%; ">明细模板</a><input id="chooseTmp" type="checkbox" onclick="checkList()" name="checkboxList" checked="checked" style="margin-top:1%;margin-left:0.5%;margin-bottom: 1%;">'
				    +'<a style="font-size:13px;margin-top:1%;margin-left: 1%;margin-bottom: 1%; ">明细模型</a><input id="chooseModule" type="checkbox" onclick="checkList()" name="checkboxList" checked="checked" style="margin-top:1%;margin-left:0.5%;margin-bottom: 1%;">'
                +'</div>');
		//初始化tip
		$("#tipContainerDiv")
				.prepend(
						"<div style='width:24px;float:left;height:16px;background:url(${ctx}/images/classics/icons/comment_yellow.gif) no-repeat' />");
		initGrid();
	});
	
	
	//表格显示
	function initGrid() {
		var columnArr =[{
			display : "编号",
			name : "searchNo",
			width : "16%",
			align : "center"
		}];
		columnArr.push({
			display : "名称",
			name : "searchName",
			width : "20%",
			align : "center"
		}, {
			display : "类型",
			name : "searchType",
			width : "15%",
			align : "center",
			render:function(a,b,c){
				switch(c){
				case '1':
					return '指标';
				case '2':
					return '报表';
				case '3':
					return '明细模板';
				case '4':
					return '明细模型';
				}
			}
		}, {
			display : '操作',
			width : "15%",
			align : 'center',
			render : function(row){
				return "<a href='javascript:void(0)' style='color :blue' class='link' onclick='detail(\""+row.searchId+"\",\"" + row.searchName +"\",\"" + row.searchType + "\")'>"+'查看'+"</a>";
			}
		});

		var url_ = "${ctx}/rpt/frame/colsearch/getColSearchInfoList.json?searchNm="
					+ searchNm;
		
		grid = $("#maingrid").ligerGrid({
			checkbox : false,
			columns : columnArr,
			dataAction : 'server',  //从后台获取数据
			usePager : false,        //服务器分页(不分页)
			alternatingRow : true,  //附加奇偶行效果行
			colDraggable : true,    //是否允许表头拖拽
			url :url_ ,
			sortName : 'searchType',   //第一次默认排序的字段
			sortOrder : 'asc',      //排序的方式
            delayLoad : true,     //延迟加载，若为true，则初始化grid后再加上grid.loadData();
			//pageParmName : 'page',
			//pagesizeParmName : 'pagesize',
			rownumbers : true,      //是否显示行序号
			width : '100%'
		});
		grid.setParm("checkNum",checkNum);
		grid.setHeight($("#center").height()-$("#tipMainDiv").height() - $("#chooseIdx").height()-40);
		grid.loadData();
	};
	
	function checkList(){
		  objName= document.getElementsByName("checkboxList");
		  checkNum = "";
		  for (i=0; i<objName.length; i++){
			  if (objName[i].type=="checkbox" && objName[i].checked){
				  checkNum += i+ ",";
				  }
			  }
		  grid.setParm("checkNum",checkNum);
		  grid.loadData();
		  }
	
	function detail(searchId, searchName,searchType) {
		var width = window.screen.width * 0.9;
		var height = window.screen.height * 0.8;
		if(searchType=='1'){
			window.top.BIONE.commonOpenDialog("指标-"+"\"" + searchName + "\"",
						"alertIndexs",width, height,"${ctx}/report/frame/datashow/idx?searchId="+searchId);
		}
		if(searchType=='2'){
			$.ajax({
				   type: "POST",
				   url: "${ctx}/rpt/frame/rptfav/query/getRptInfo",
				   data: {
						rptId: searchId
					},
				   success: function(data){
					   json = JSON.stringify(data); 
					   if ('04' == data.rptType) {
							window.top.BIONE.commonOpenDialog("明细报表-"+"\"" + searchName + "\"", "alertRptIndexs", width, height,'${ctx}/frs/integratedquery/detailsel?data='+json);
						} else if ('02' == data.rptType) {
							window.top.BIONE.commonOpenDialog("通用报表-"+"\"" + searchName + "\"", "alertRptIndexs", width, height,'${ctx}/frs/integratedquery/rptquery?data='+json);
						}
				   }
				});
		}
		if(searchType=='3'){
			window.top.BIONE.commonOpenDialog("明细模板-"+"\"" + searchName + "\"", "alertDetails", width, height,'${ctx}/report/frame/datashow/detail/showinfo?templateId=' + searchId);
		}
		if(searchType=='4'){
			window.top.BIONE.commonOpenDialog("明细模型-"+"\"" + searchName + "\"", "alertSetModules", width, height,'${ctx}/rpt/frame/dataset/infoFrame?datasetId=' + searchId);
		}
		//${ctx}/rpt/frame/dataset/infoFrame?datasetId="+ rows[0].setId 
		//BIONE.commonOpenLargeDialog( "\"" + userName + "\"" + "登录明细","editDetail","${ctx}/bione/syslog/login/detail?userId=" + userId);
	}
	
	//导入后刷新
	function reloadGrid(){
		grid.loadData();
	}
	
</script>
</head>
<body>
<div id="template.center">
<div id="center" style="width: 100%">
		<div id="maingrid" class="maingrid"></div>
</div>
</div>
</body>
</html>