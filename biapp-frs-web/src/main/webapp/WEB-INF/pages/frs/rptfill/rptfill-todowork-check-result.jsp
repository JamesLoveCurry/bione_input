<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template9.jsp">
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
	var ctx = '${ctx}', dataDate = '${dataDate}', exeObjId = '${orgNo}', templateId = '${templateId}', templateType = '${templateType}';
	var checkType= '${checkType}';
</script>
<script type="text/javascript">
	$(function() {
		tmp.init();
		tmp.initStsInfo();
	});
	var tmp = {
		//TAB
		logicUrl: '',
		sumpartUrl: '',
		warnUrl: '',
		logicFlag: true,
		sumpartFlag: true,
		warnFlag: true,
		zeroFlag: true,
		tabHeight: 0,
		urlTmp : ['${ctx}/frs/verificationLogic/logicTab',
		          '${ctx}/frs/verificationLogic/logicByExternalTab',
		          '${ctx}/frs/verificationTotal',
		          '${ctx}/frs/verificationWarning',
		          '${ctx}/frs/verificationZero'],
		tabTmp : ['logictab', 'logicByExternalTab', 'sumparttab', 'warntab', "zerotab"],  //与url顺序对应
		RptIdxInfo : null
	};
	
	
	tmp.initStsInfo = function(){
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/getRptStsInfo",
			dataType : 'json',
			data : {
				rptId : "${rptId}", dataDate : dataDate, orgNo : exeObjId, templateId : templateId
			},
			type : "post",
			success : function(result){
					if("${type}"){
						tmp.logicRs =tmp.initSts(result.logicRs);
						tmp.sumpartRs = tmp.initSts(result.sumpartRs);
						tmp.warnRs = tmp.initSts(result.warnRs);
						tmp.zeroRs = tmp.initSts(result.zeroRs);
						if("06" == result.logicInTableRes){
							$("li[tabid=logictab]").css("border", "2px solid #ff0000").css("display", "block");
						}
						if("06" == result.logicByExternalRes){
							$("li[tabid=logicByExternalTab]").css("border", "2px solid #ff0000").css("display", "block");
						}
						if("06" == result.sumpartRs){
							$("li[tabid=sumparttab]").css("border", "2px solid #ff0000").css("display", "block");
						}
						if("06" == result.warnRs){
							$("li[tabid=warntab]").css("border", "2px solid #ff0000").css("display", "block");
						}
						if("06" == result.zeroRs){
							$("li[tabid=zerotab]").css("border", "2px solid #ff0000").css("display", "block");
						}
					}
			}
		})
	}
	
	tmp.initSts = function(sts){
		switch(sts){
			case '01' : return '等待运行';
			case '02' : return '运行中';
			case '03' : return '成功';
			case '04' : return '失败';
			case '05' : return '通过';
			case '06' : return '未通过';
			case '07' : return '无需校验';
			default : return '未校验';
		}
	
	}
	
	tmp.init =  function(){
		this.tabObj =$("#tab").ligerTab({
			changeHeightOnResize : true,
			dragToMove: false,
			contextmenu : false,
			onAfterSelectTabItem : function(tabId) {
				if(!tmp[tabId+"Flag"]&&$("#"+tabId+"tab").attr('src')==""){
					$("#"+tabId+"tab").attr('src',tmp[tabId+'Url']);
					tmp[tabId+"Flag"]=true;
				}
			}
		});
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/showTemplateId",
			dataType : 'text',
			data : {
				rptId : "${rptId}",
				dataDate : "${dataDate}"
			},
			type : "post",
			success : function(result){
				if(result && result != "ERROR"){
					for(var i = 0; i < tmp.urlTmp.length; i++){
						var temp = tmp.urlTmp[i] + "?";
						temp += "rptTemplateId=" + JSON.parse(result).tmpId + "&";
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
					tmp.addTabItem(tmp.tabObj,tmp.tabTmp[0],"表内校验结果");
					tmp.addTabItem(tmp.tabObj,tmp.tabTmp[1],"表间校验结果");
					if("01" != templateType){//明细报表只有逻辑校验
                        tmp.addTabItem(tmp.tabObj,tmp.tabTmp[2],"总分校验结果");
						tmp.addTabItem(tmp.tabObj,tmp.tabTmp[3],"预警校验结果");
						tmp.addTabItem(tmp.tabObj,tmp.tabTmp[4],"零值校验结果");
					}
					
					if("${state}"!=null && "${state}"!=""){
						$("#${state}tab").attr('src')==""
						tmp["${state}Flag"]=false;
						tmp.tabObj.selectTabItem("${state}"+"tab");
					}else{
						if(checkType && checkType != "null" && checkType != "NO" ){
							tmp.tabObj.selectTabItem(checkType+"tab");
						}else{
							tmp.tabObj.selectTabItem(tmp.tabTmp[0]);
						}
					}
						
					
					$('.l-tab-links > ul').append(
						'<li>																									' +
						'	<div class="l-dialog-btn" style="width: 95px; height: 23px; margin-top: 3px;">						' +
						'		<div class="l-dialog-btn-inner" style="margin-top: -1px; color: white; padding-left: 14px; 		' + 
						'			background: #32A5F5 url(${ctx}/images/classics/menuicons/excel.png) no-repeat scroll 5%;"	' +
						'			onclick="tmp.exportExcel();">导出 Excel</div>													' +
						'	</div>																								' +
						'</li>																									');
					/* tmp.tabObj.selectTabItem("logictab"); */
				}else{
					BIONE.tip("没有对应的报表模板");
				}
			},
			error:function(){
				//BIONE.tip("数据加载异常，请联系系统管理员");
			}
		});
	};
	
	tmp.addTabItem = function(tabObj,tabId, tabText){
		tmp.tabHeight =  $(window).height() - 27;
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
			var rptId = '${rptId}';
			var type = '${type}';
			var taskId = '${taskId}';
			var parms = dataDate + ',' + exeObjId + ',' + templateId + ',' + rptId + ',' + type + ',' + taskId;
			$('body').append('<iframe id="download" src="' + ctx + '/frs/dataValid/exportResultDetail?dateOrgTempParms=' + parms + '"></iframe>');
		} else {
			BIONE.tip('无数据可以导出');
		}
	};
</script>
</head>
<body>
</body>

</html>