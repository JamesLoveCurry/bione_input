<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14_BS.jsp">
<head>
<%@ include file="/common/spreadjs_load.jsp"%>
<script>
	var rptId = '${rptId}';
	var templateId = '${templateId}';
	var rptNum = '${rptNum}';
	var verId = '${verId}';
	var rptIdxObj = {
			id : "",
			text : "",
			nodeType : "rptIdx",
			data : {
				indexNm : "",
				id :{
					indexNo : "",
					indexVerId : ""
				}
			},
			params : {
				dimNos : "DATE,ORG,INDEXNO"
			}
	};
	
	$(function(){
		initRpt();
	});
	
	function initRpt(){
		require.config({
			baseUrl : '${ctx}/plugin/js/',
			paths: {
				design : 'cfg/views/rptdesign'
			}
		});
		require(["design"] , function(design) {
			var settings = {
				ctx : '${ctx}',
				readOnly : true,
				cellDetail: false,
				showType: 'cellnm',
				onEnterCell: function(sender, args, rptIdxTmp) {
					if (rptIdxTmp.realIndexNo) {
						rptIdxObj.data.indexNm = rptIdxObj.text = rptIdxTmp.cellNm;
						rptIdxObj.data.id.indexNo = rptIdxObj.id = rptIdxTmp.realIndexNo;
						if(rptIdxTmp.measureNo){
							rptIdxObj.id = rptIdxTmp.measureNo;
						}
						rptIdxObj.data.id.indexVerId = rptIdxTmp.indexVerId;
						window.parent.addIdx(rptIdxObj, true);
						BIONE.tip("已选择单元格 ：" + rptIdxTmp.cellNm);
					}
				},
				initFromAjax : true,
				ajaxData : {
					templateId : templateId,
					verId : verId
				}
			};
			try {
				spread = design.init($("#spread") , settings);
			} catch (e) {
				BIONE.tip("报表加载异常");
			}
		});
	}
</script>
</head>

<body>
	<div id="template.center">
		<div id="spread" style="width: 100%; height: 100%; border: 1px solid #D0D0D0;"></div>
	</div>
</body>
</html>