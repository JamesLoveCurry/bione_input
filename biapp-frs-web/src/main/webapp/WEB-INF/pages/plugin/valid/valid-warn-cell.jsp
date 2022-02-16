<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5B.jsp">
<%@ include file="/common/spreadjs_load.jsp"%>
<script type="text/javascript">
var designInfo4Upt;
$(function(){
	initDatas("${rptId}", "${cfgId}");
});
function initDatas(rptId, cfgId){
	if(cfgId != null
			&& cfgId != ""
			&& typeof cfgId != "undefined" && cfgId){
		// 2.设计器信息
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/design/cfg/getDesignInfo",
			dataType : 'json',
			data : {
				templateId : cfgId
			},
			type : "post",
			success : function(result){
				if(result != null
						&& typeof result.tmpInfo != "undefined"){
					designInfo4Upt = result;
					initDesign();
				}
			},
			error:function(){
				BIONE.tip("数据加载异常，请联系系统管理员");
			}
		});
	}
}
function initDesign(){
	require.config({
		baseUrl : "${ctx}/plugin/js/",
		paths:{
			"design" : "cfg/views/rptdesign"
		}
	});
	
	require(["design"] , function(ds){
		var objTmp = designInfo4Upt;
		var jsonStr = "";
		// 修改时，初始化数据
		if(objTmp != null){
			jsonStr = objTmp.tmpInfo.templateContentjson;
			designInfo4Upt = null;
		}
		var settings = {
				targetHeight : ($("#center").height() - 2) ,
				ctx : "${ctx}" ,
				showType:"cellnm",
				readOnly : true ,
				onCellDoubleClick : spreadDbclkCell,
				cellDetail:true,
				toolbar:false,
				// 报表指标数据初始化
				moduleCells : objTmp==null ? null : objTmp.moduleCells,
				formulaCells : objTmp==null ? null : objTmp.formulaCells,
				idxCells : objTmp==null ? null : objTmp.idxCells,
				staticCells : objTmp==null ? null : objTmp.staticCells,
				idxCalcCells : objTmp==null ? null : objTmp.idxCalcCells,
				initJson : jsonStr
		};
		spread = ds.init($("#spread") , settings);
	})
}

function spreadDbclkCell(sender , args , rptIdxTmp){
	if(rptIdxTmp.realIndexNo && rptIdxTmp.realIndexNo != ""){
		rptIdxTmp.realIndexNm = (rptIdxTmp.cellNm)?rptIdxTmp.cellNm:rptIdxTmp.cellNo;
		window.parent.frames["warnAdd"].liger.get("cell").setValue(rptIdxTmp.realIndexNo);
		window.parent.frames["warnAdd"].liger.get("cell").setText(rptIdxTmp.realIndexNm);
		BIONE.closeDialog("cellChoose");
	}else{
		BIONE.tip("请选择指标单元格");
	}
	
}
</script>
</head>
<body>
	<div id="template.center">
		<div id="spread"></div>
	</div>
</body>
</html>