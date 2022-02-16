<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<%@ include file="/common/zTree_load.jsp"%>
<head>
<script type="text/javascript">
	var indexNo = '${indexNo}';
	var orgNo = '${orgNo}';
	var dataDate = '${dataDate}';
	$(function() {
		//样式调整
		var winHeight = $(window).height();
		$("#bottom").height(winHeight * 0.95);

		BIONE.createButton({
			text : '按机构下钻',
			width : '120px',
			appendTo : '#bottom',
			operNo : 'saveButton',
			icon : 'refresh',
			click : function() {
				parent.BIONE
				.commonOpenDialog(
						'',
						'showOrgdetailDialog',
						800,
						600,
						'${ctx}/report/frs/rptquery/rptidxQueryController.mo?doFlag=rpt-orgdetail-dialog&indexNo='
								+ indexNo +'&orgNo='+orgNo+'&dataDate='+dataDate+'&showType=02&d=' + new Date().getTime());
				BIONE.closeDialog("chooseTypeDialog");
			}
		});
		BIONE
				.createButton({
					text : '按指标下钻',
					width : '120px',
					appendTo : '#bottom',
					operNo : 'saveButton',
					icon : 'refresh',
					click : function() {
						parent.BIONE
								.commonOpenDialog(
										'',
										'showIdxdetailDialog',
										800,
										600,
										'${ctx}/report/frs/rptquery/rptidxQueryController.mo?doFlag=rpt-idxdetail-dialog&indexNo='
												+ indexNo +'&orgNo='+orgNo+'&dataDate='+dataDate+ '&showType=01&d=' + new Date().getTime());
						BIONE.closeDialog("chooseTypeDialog");
					}
				});
	});
</script>
</head>
<body>
	<div id="bottom"
		style="margin: 0 auto; overflow: hidden; position: relative; margin-top: 50px;"></div>
</body>
</html>