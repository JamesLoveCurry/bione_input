<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template8.jsp">
<head>
<style>
.indexStsA,.indexNmA{
     width:55%;
     cursor:pointer;
}
.stop{
    color:red;
}
</style>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid;
	var detailList = null;
	var detailManager ;
	var templateName_ = '${templateName}';
	var templateInfos={Rows:null};
	var isNeedCheck =  window.parent.isNeedCheck;
	var canEdit = window.parent.canEdit;
	$(function() {	
		initGrid();
		initData();
		initToolBar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		$("input[name='templateName']").val(templateName_);
		if(isNeedCheck)
			gatherData();
	});
	
	function initData(){

		detailList = window.parent.templateVO.detailList;
		window.parent.taskManage.templateManage = window;
		loadData();
	}
	
	function getOrgInfo(orgNo,orgNm){
		if(orgNo&&orgNo!=null)
		{
			var datas = [];
			var orgNoArray = orgNo.split(",");
			var orgNmArray = orgNm.split(",");
			for(var i = 0 ;i <orgNoArray.length;i++){
				var orgId = orgNoArray[i];
				var orgNm = orgNmArray[i];
				if(orgId&&orgId!="")
					datas.push({orgId:orgId,orgNm:orgNm});
			}
			return datas;
		}
		return null;
	}
	
	function loadData(){
		if(detailList!=null){
			templateInfos.Rows = [];
			for(var i = 0 ; i<detailList.length;i++)
			{
				var detail =  detailList[i];
				
				templateInfos.Rows[i] = {
						"indexNo":detail.indexNo,
						"indexNm":detail.indexNm,
						"orderNum":detail.orderNum,
						"orgMode":detail.orgMode,
						"cfgNm":detail.cfgNm,
						"dimFilterInfo":detail.dimFilterInfos,
						"orgInfos":getOrgInfo(detail.orgNo,detail.orgNm),
						"ruleId" : detail.ruleId,
						"ruleNm" : detail.ruleNm
					};
			}
			initIdxTbl(templateInfos.Rows);
		}
	}
	function initGrid() {
		var  columnArr  =[{
			display : '顺序编号',
			name : 'orderNum',
			width : "5%",
			align : 'left'
		},  {
			display : '配置名称',
			name : 'cfgNm',
			width : "15%",
			align : 'center',
			editor: { type: 'text' }
		},  {
			display : '指标名称',
			name : 'indexNm',
			width : "15%",
			align : 'center'
		},  { 
			display : '机构方式',
			name : 'orgMode',
			width : "15%",
			align : 'center',
			editor: {
				type: 'select',
				data:[{orgMode:"01",text:"取实例机构"},{orgMode:"02",text:"独立配置机构"}],
				valueField: 'orgMode',
				ext:function(rowData,rowNum,value,column){
					return {
						valueField : 'orgMode',
						textField : 'text',
						onSelected : function(value, text) {
							if(value){
								if(value=="01")
								{
									var rowData = grid.getRow(rowNum);
									grid.updateCell("orgNm","", rowData);
									grid.endEdit();
								}
							}
						}
					}
				}
			},
			render:function(row){
				if(row.orgMode==null || typeof row.orgMode=="undefined")
					return "";
				if(row.orgMode=="01")
					return "默认机构";
				if(row.orgMode=="02")
					return "独立配置机构";
			}
		},  { 
			display : '选择机构',
			name : 'orgNm',
			width : "20%",
			align : 'center',
			editor: {
				type: 'select',
				ext : function(rowData,rownum){
					if(rowData.orgMode=="01"){
						return {
							valueField : 'id',
							textField : 'text',
							onBeforeOpen : function(value, text) {
								return false;
							}
						};
						return false;
					}
					var orgInfos = rowData.orgInfos?encodeURI(encodeURI(JSON2.stringify(rowData.orgInfos))):"";
					//window.parent.BIONE.commonOpenDialog('选择机构','selectOrgBox','600','480','${ctx}/rpt/input/idxdatainput/selectOrg?orgInfos='+orgInfos+"&rownum="+rownum+"&d=" + new Date(),null);
					window.parent.BIONE.commonOpenSmallDialog('选择机构', 'chooseOrg', '${ctx}/rpt/input/idxdatainput/chooseOrg?rownum='+rownum);
				}
			},
			render:function(row){
				if(row.orgMode=="01")
					return "";
				var orgInfos = row.orgInfos;
				if(orgInfos){
					var showText="" ;
					var i = 0 ;
					while(orgInfos[i]){
						if(i!=0)
							showText = showText +",";
						showText = showText + orgInfos[i].orgNm;
						i++;
					}
					/*
					if(typeof orgInfos.length !="undefined"){
						for(var i = 0 ;i < orgInfos.length;i++)
						{
							if(i!=0) 
								showText = showText +",";
							showText = showText + orgInfos[i].orgNm;
						}	
					}else{
						var i = 0 ;
						showText = showText +",";
						while(orgInfos[i]){
							showText = showText + orgInfos[i].orgNm;
							i++;
						}
					}
					*/
					return showText;
				}
			}
		},{ 
			display : '校验规则',
			name : 'ruleNm',
			width : "20%",
			align : 'center',
			editor: {
				type: 'select',
				ext : function(rowData,rownum){
					var ruleId = rowData.ruleId;	
					window.parent.BIONE.commonOpenDialog('选择规则', 'chooseRule',  "600", "400", '${ctx}/rpt/input/idxdatainput/chooseRule?rownum='+rownum+'&indexNo='+rowData.indexNo+'&ruleId='+ruleId);
				}
			},
			render:function(row,rownum){
				return  row.ruleNm;
			}
		},{
			display : '',
			name : 'ruleId',
			width : "10%",
			align : 'center',
			render:function(row,rownum){
				//return  "<a href='javascript:void(0);' onClick='clearRule('"+rownum+"')>清除规则</a>";
				return "<a href='##'   class='oper'  onclick=\"clearRule('"+ rownum + "')\" >清除规则</a>";
			}
		}];
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox : false,
			columns : columnArr,
			alternatingRow : true, //附加奇偶行效果行
            delayLoad:true,
			rownumbers : true,
			width : '100%',
			enabledEdit: canEdit=="true",
			clickToEdit:canEdit=="true"
		});
		grid.setHeight($("#center").height() - 40);
	};
	
	function clearRule(rownum){
		var rowData = grid.getRow(rownum);
		rowData.ruleId="";
		rowData.ruleNm = "";
		grid.updateCell("ruleNm","", rowData);
	}
	
	function refreshTable(datas,rownum){
		var rowData = grid.getRow(rownum);
		if(datas){
				rowData.orgInfos=datas;
				grid.updateCell("orgNm",datas, rowData);
		}
		grid.endEdit();
	}
	
	function changeValue(ids,texts,rownum){
		var rowData = grid.getRow(rownum);
		var datas=[];
		for(var i = 0 ;i <ids.length;i++){
			datas.push({orgId:ids[i],orgNm:texts[i]});
		}
		rowData.orgInfos=datas;
		grid.updateCell("orgNm",datas, rowData);
		grid.endEdit();
	}
	function changeRule(ruleId,ruleNm,rownum){
		grid.endEdit();
		var rowData = grid.getRow(rownum);
		rowData.ruleId=ruleId;
		rowData.ruleNm = ruleNm;
		grid.updateCell("ruleNm",ruleNm, rowData);
	}
	
	function initToolBar() {
		var toolBars = [ {
			text : '更新配置',
			click : f_update_cfg,
			icon : 'add'
		}];
		BIONE.loadToolbar(grid, toolBars, function() {
		});
	}
	function f_update_cfg() {
			dialog = window.parent.BIONE.commonOpenDialog("更新配置",
					"rptUpdateCfgBox",$(parent.parent.document).width()-200, $(parent.parent.document).height()-100,
					"${ctx}/rpt/input/idxdatainput/updateCfgBox" ,null);
	}
	function  refreshIt(){
		window.parent.refreshTree();
	}
	
	function getDimFilterInfo(){
		var rows = grid.rows;
		if(rows == null ||typeof rows =="undefined") return null;
		var returnIdxInfo = [];
		for(var i = 0 ; i <rows.length;i++){
			returnIdxInfo.push({
				upId:null,
				filterinfos:rows[i].dimFilterInfo,
				filterFormula:rows[i].filterFormula,
				id:rows[i].indexNo,
				text:rows[i].indexNm,
				cfgNm:rows[i].cfgNm,
				orgInfos:rows[i].orgInfos,
				orgMode:rows[i].orgMode
			});
		}
		return returnIdxInfo;
	}
	function initIdxTbl(datas){
		var rows = grid.rows;
		$.each(rows, function(i, n) {
			grid.deleteRow(n);
		});
		for(var i = 0 ; i <datas.length;i++){
			var data = datas[i];
			var idxId = data.idxId|| data.indexNo;
			var idxNm =data.idxNm|| data.indexNm;
			var row = grid.addRow({
				orderNum: i+1,
				indexNm:idxNm,
				indexNo: idxId,
				cfgNm : data.cfgNm,
				orgMode: data.orgMode,
				dimFilterInfo:data.dimFilterInfo,
				filterFormula:data.filterFormula,
				orgInfos:data.orgInfos,
				ruleId : data.ruleId,
				ruleNm : data.ruleNm
			});
		}
	}
	

	function gatherData(){

		grid.endEdit();
		var rows = grid.rows;
		if(rows == null ||typeof rows =="undefined"||rows.length==0) {
			BIONE.tip('请更新配置' );
			return ;
		}
		var returnIdxInfo = [];
		for(var i = 0 ; i <rows.length;i++){
			var orgNo="";
			var orgNm="";
			
			
			if(rows[i].orgInfos&&rows[i].orgInfos!=null){
				var orgInfos = rows[i].orgInfos;
				var j = 0 ;
				while(orgInfos[j]){
					orgNo = orgNo+orgInfos[j].orgId+",";
					orgNm = orgNm+orgInfos[j].orgNm+",";
					j++;
				}
				/*
				for(var x = 0 ;x <rows[i].orgInfos.length;x++){
					orgNo = orgNo+ rows[i].orgInfos[x].orgId+",";
					orgNm = orgNm+ rows[i].orgInfos[x].orgNm+",";
				}
				*/
			}
			if(!rows[i].orgMode||rows[i].orgMode==null){
				BIONE.tip('请填写指标:['+rows[i].indexNm+']的机构方式' );
				return ;
			}
			if(!rows[i].cfgNm||rows[i].cfgNm==null){
				BIONE.tip('请填写指标:['+rows[i].indexNm+']的配置名称' );
				return ;
			}
			if(rows[i].orgMode=="02"&&orgNo==""){
				BIONE.tip('请选择指标:['+rows[i].indexNm+']的机构' );
				return ;
			}
			returnIdxInfo.push({
				filterinfos:[],
				filterFormula:rows[i].filterFormula,
				idxNo:rows[i].indexNo,
				indexNm:rows[i].indexNm,
				orderNum: rows[i].orderNum,
				orgMode: rows[i].orgMode,
				cfgNm:rows[i].cfgNm,
				orgNm:rows[i].orgNm,
				orgNo:orgNo,
				orgNm:orgNm,
				ruleId : rows[i].ruleId,
				ruleNm : rows[i].ruleNm
			});
			if(rows[i].dimFilterInfo!=null && typeof rows[i].dimFilterInfo !="undefined")
			{
				var temps = rows[i].dimFilterInfo;
				
				for(var j = 0 ;j <temps.length;j++){
					returnIdxInfo[i].filterinfos.push({
						idxNo:rows[i].indexNo,
						filterMode:temps[j].filterMode,
						filterText:temps[j].filterText,
						dimNm:temps[j].dimNm,
						dimNo:temps[j].dimNo,
						filterVal:temps[j].filterVal
					});
				}
			}else{
				BIONE.tip('请更新指标:['+rows[i].indexNm+']的信息' );
				return ;
			}
			
		}
		window.parent.templateVO.detailList = returnIdxInfo;
		window.parent.doTask();
	}
</script>
</head>
<body>

	<div id="template.right.down">
		<div id="aaa">
			<div id="maingrid" style="margin-top: 60px;"></div>
		</div>
	</div>
</body>
</html>