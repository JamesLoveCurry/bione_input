<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">

	//01 保存 02 提交  03 驳回
	function save(operType) {
		var rsInfo = {
			taskId : taskId,
			taskInstanceId : taskInstanceId,
			templateId : templateId,
			dataDate : dataDate,
			dataInputList : []
		};
		for ( var i = 0; i < gridManager.length; i++) {
			var dataInputObj = [];
			var gridObj = gridManager[i].gridObj;
			gridObj.endEdit();
			var columns = gridObj.columns;
			var rows = gridObj.rows;
			var tmpCheckArray = [];
			for(var x =0;x <rows.length;x++){
				var colObj = [];
				var row = gridObj.rows[x];
				var tmpKey = "";
				for ( var c = 0; c < columns.length; c++) {
					var column = columns[c];
					var dimTypeNo;
					var dimItemValue;
					if (column.name == "indexNm") {
						dimItemValue = row.idxInfo.idxId;
						dimTypeNo = "INDEX_NO";
					} else if (column.name == "currency") {
						dimTypeNo = "CURRENCY";
						dimItemValue = row.currencyInfo.currencyId;
					} else if (column.name == "org") {
						dimTypeNo = "org";
					} else if (column.name == "value") {
						dimTypeNo = "INDEX_VAL";
						var tmpValue = row.value;
						if(!tmpValue||tmpValue==null||tmpValue=="")
							tmpValue = "";
						dimItemValue = tmpValue;
					}  else if(column.name=="oper"){
						dimTypeNo = "oper";
					}else {
						var colName = column.name;
						var cols = colName.split("_");
						dimTypeNo = cols[1];
						dimItemValue = row[colName].dimNo;
					}
					if (dimTypeNo != "org"&&dimTypeNo!="oper"){
						colObj.push({
							dimTypeNo : dimTypeNo,
							dimItemValue : dimItemValue
						});
						if(column.name != "value"){
							tmpKey = tmpKey + dimItemValue+"_";
						}
					}
				}
				colObj.push({
					dimTypeNo : "ORG_NO",
					dimItemValue : row.orgInfo.orgId
				});
				tmpKey = tmpKey + row.orgInfo.orgId+"_";
				colObj.push({
					dimTypeNo : "operOpinion",
					dimItemValue : row.operOpinion
				});
				tmpKey = tmpKey + row.operOpinion+"_";
				colObj.push({
					dimTypeNo : "CFG_ID",
					dimItemValue : gridManager[i].key
				});
				tmpKey = tmpKey +  gridManager[i].key;
				if(tmpCheckArray[tmpKey]!=null){
					BIONE.tip(gridObj._columns['c101'].display+",指标名"+row.idxInfo.idxNm+"第"+ tmpCheckArray[tmpKey] +"行数据和第"+(x+1)+"行数据完全一致,请检查");
					return ;
				}else{
					tmpCheckArray[tmpKey] = x+1;
				}
				dataInputObj.push(colObj);
			}
			rsInfo.dataInputList.push(dataInputObj);
		}
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/idxdatainput/saveDataInputData?d="
					+ new Date().getTime(),
			dataType : 'json',
			type : "post",
			data : {
				"data" : JSON2.stringify(rsInfo)
			},
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				if (operType == "01")
					window.parent.closeDialog("01");
				else if (operType == "02")
					window.parent.doSubmit();
				else if (operType == "03")
					window.parent.doRebut();
			},
			error : function(result, b) {
				BIONE.tip('保存失败,发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
</script>

<title>指标目录管理</title>
</head>
<body style="width: 80%">
	<div id="template.center">
		<div id="gridContainer"></div>
	</div>
</body>
</html>