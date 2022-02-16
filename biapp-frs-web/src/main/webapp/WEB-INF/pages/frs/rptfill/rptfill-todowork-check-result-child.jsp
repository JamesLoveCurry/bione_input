<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template9.jsp">
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
	var ctx = '${ctx}', dataDate = '${dataDate}', exeObjId = '${orgNo}', templateId = '${templateId}';//templateId = (templateId = '${templateId}') != 'ERROR' ? templateId : '';
</script>
<script type="text/javascript">
	$(function() {
		tmp.init();
	});
	var tmp = {
		//TAB
		logicUrl: '',
		sumpartUrl: '',
		warnUrl: '',
		logicFlag: true,
		sumpartFlag: true,
		warnFlag: true,
		tabHeight: 0,
		urlTmp : ['${ctx}/report/frs/datavalid/verificationLogicController.mo','${ctx}/report/frs/datavalid/verificationWarningController.mo'],
		tabTmp : ['logictab','warntab']  //与url顺序对应
	}
	
	
	
	tmp.init =  function(){
		this.tabObj =$("#tab").ligerTab({
			changeHeightOnResize : true,
			contextmenu : false,
			onAfterSelectTabItem : function(tabId) {
				if(!tmp[tabId+"Flag"]&&$("#"+tabId+"tab").attr('src')==""){
					$("#"+tabId+"tab").attr('src',tmp[tabId+'Url']);
					tmp[tabId+"Flag"]=true;
				}
			}
		});
		if("${rptId}" && "${templateId}" && "${dataDate}" && "${orgNo}"){
			for(var i = 0; i < tmp.urlTmp.length; i++){
				var temp = tmp.urlTmp[i] + "?";
				temp += "rptTemplateId=${templateId}&";
				if("${rptId}"){
					temp += "rptId=${rptId}&";
				}
				if("${dataDate}"){
					temp += "dataDate=${dataDate}&";
				}
				if("${orgNo}"){
					temp += "orgNo=${orgNo}&";
				}
				temp += "d=" + new Date().getTime();
				tmp[tmp.tabTmp[i]+'Url']=temp;
			}
			tmp.addTabItem(tmp.tabObj,tmp.tabTmp[0],"逻辑校验结果");
			tmp.addTabItem(tmp.tabObj,tmp.tabTmp[1],"预警校验结果");
			tmp.tabObj.selectTabItem(tmp.tabTmp[0]);
			
			$('.l-tab-links > ul').append(
					'<li>																									' +
					'	<div class="l-dialog-btn" style="width: 95px; height: 23px; margin-top: 3px;">						' +
					'		<div class="l-dialog-btn-inner" style="margin-top: -1px; color: white; padding-left: 14px; 		' + 
					'			background: #32A5F5 url(${ctx}/images/classics/menuicons/excel.png) no-repeat scroll 5%;"	' +
					'			onclick="tmp.exportExcel();">导出 Excel</div>													' +
					'	</div>																								' +
					'</li>																									');
		}else{
			BIONE.tip("数据异常，请联系系统管理员");
		}
	};
	
	tmp.addTabItem = function(tabObj,tabId, tabText){
		tmp.tabHeight = 400;
		tabObj.addTabItem({//添加标签tab页
			tabid : tabId,
			text : tabText,
			showClose : false,
			content : "<div id='" + tabId + "' style='height:" + tmp.tabHeight
					+ "px;width:100%;overflow: hidden;'></div>"
		});
		content = "<iframe frameborder='0' id='"+tabId+"tab' name='"+tabId+"tab' style='height:100%;width:100%;' src=''></iframe>";
		$("#"+tabId).html(content);
		tmp[tabId+"Flag"]=false;
	};
	tmp.exportExcel = function() {
		if (dataDate && exeObjId && templateId) {
			var parms = dataDate + ',' + exeObjId + ',' + templateId+',${rptId},${type},${taskId}';
			$('body').append('<iframe id="download" src="' + ctx + '/report/frs/datavalid/dataValidController.mo?_type=data_event&_field=exportResultDetail&_event=POST&_comp=main&Request-from=dhtmlx&doFlag=rptfill-todowork-check-result-child&dateOrgTempParms=' + parms + '"></iframe>');
		} else {
			BIONE.tip('无数据可以导出');
		}
	};
	
	
	
</script>
</head>
<body>
</body>

</html>