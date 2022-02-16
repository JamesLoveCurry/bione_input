<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>

<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	
	var from ;
	var operType = "${operType}";
	var rptObj = [];
	window.parent.selectedRptIdArr = [];
	var btnNm = {
		imp : "导入",
		exp : "导出"
	};
	$(function() {
		
		var fields = [ {
            display: "任务名称",
            name: "taskNm",
            newline: false,
            type: "select",
            width: '140',
            cssClass: "field",
            labelWidth: '90',
            comboboxName: "taskNm_sel",
			options : {
				valueFieldID : "taskId",
				url : window.parent.tmp.taskComBoBoxUrl,
				ajaxType : "get",
				// 联动报表
				onSelected : function(value) {
					if ("" != value) {
						$.ajax({
							async : false,
							type : "post",
							url : window.parent.tmp.rptComBoBoxUrl,
							dataType : "json",
							data : {
								"taskId" : value,
								"flag" : "1"
							},
							success : function(rptData) {
								if(rptData){
									window.parent.selectedRptIdArr = [];
									for(var i = 0; i < rptData.length; i++){
										window.parent.selectedRptIdArr.push(rptData[i].id);
									}
								}
							}
						});
					}
				},
				cancelable : false,
				dataFilter : true
			}
		},{
            display: "机构名称",
            name: "orgNm",
            newline: false,
            type: "select",
            width: '140',
            cssClass: "field",
            labelWidth: '90',
           	comboboxName: "orgNm_sel",
            options : {
			    onBeforeOpen: function() {
			        var taskId = $.ligerui.get("taskNm_sel").getValue();
			        if (taskId) {
			            var height = $(window.parent).height() - 120;
			            var width = $(window.parent).width() - 480;
			            BIONE.commonOpenDialog("机构树", "taskOrgWin", width, height, "${ctx}/frs/integratedquery/rptquery/searchOrgSetInfo?taskId=" + taskId + "&orgType="+window.parent.moduleType, null);
			            return false;
			        } else { BIONE.tip("请选择任务"); return false}
			    },
			    cancelable: false
			}
        },{
            display: "数据日期",
            name: "dataDate",
            newline: false,
            type: "date",
            width: '140',
            cssClass: "field",
            labelWidth: '90',
            options : {
            	cancelable: false,
            	format : "yyyyMMdd"
            }
        } ];
		
		fields.push({
            display: "报表状态",
            name: "sts",
            newline: false,
            type: "select",
            width: '140',
            labelWidth: '90',
            cssClass: "field",
            options: { 
            	cancelable  : false,
            	onBeforeOpen : function(){
            		return operType == "exp";
            	},
            	initValue : operType == "exp" ? "99" : "0",
            	data: [{ text: '全部', id: "99" }, { text: '未归档', id: "0" }, { text: '已归档', id: "1" }] 
			}
		});
		
		
		from = $("#search").ligerForm({
			fields : fields
		});
		
		
		var exportFn = function(callback){
			var con = from.getData();
			if(con.taskNm == ""){
				BIONE.tip("请选择[任务名称]!");
				return;
			}
			if(con.orgNm == ""){
				BIONE.tip("请选择[机构名称]!");
				return;
			}
			if(con.dataDate == ""){
				BIONE.tip("请选择[数据日期]!");
				return;
			}
			
			if(operType == "exp"){
				BIONE.showLoading("正在生成下载文件,请稍等...");
			}
			var selOrgName = $.ligerui.get("orgNm_sel").getText();
			var rptIdsArr = window.parent.selectedRptIdArr;
			var orgList = [];
			var orgNos = con.orgNm.split(",");
			var orgNms = selOrgName.split(",");
			for(var i=0; i< orgNos.length; i++){
				var org = {};
				org.orgNo = orgNos[i];
				org.orgNm = orgNms[i];
				org.orgNm = org.orgNm.replace('('+ org.orgNo +')', '');
				orgList.push(org);
			}
			$.ajax({
               url: "${ctx}/rpt/frs/rptfill/validExportIdx?d=" + new Date().getTime(),
               dataType: 'json',
               data : {
               	rptIdsArr : rptIdsArr.join(","),
               	orgNo : con.orgNm,
               	dataDate : con.dataDate,
               	sts : con.sts
               },
               type: 'post',
               success :function(result){
                   	rptIdsArr = result.rptIds;
                   	if(operType == "exp"){
                   		var argsArr = [];
           				for(var i = 0; i < rptIdsArr.length; i++){
           					for(var j=0; j < orgList.length; j++){
           						var argsArr1 = [];
               	                var args1 = { 'DimNo': 'ORG', 'Op': '=', 'Value': orgList[j].orgNo };
               	                argsArr1.push(args1);
               	                var args = { 'orgNm': orgList[j].orgNm, 'rptId': rptIdsArr[i], 'dataDate': con.dataDate, 'busiLineId': "*", 'searchArgs': JSON2.stringify(argsArr1) };
               	                argsArr.push(args);
           					}
           				}
                           
                        if (argsArr.length > 0) {
                     	   $.ajax({
                                url: "${ctx}/rpt/frs/rptfill/downloadList?d=" + new Date().getTime(),
                                dataType: 'json',
                                data : {
                                	json : encodeURI(encodeURI(JSON2.stringify(argsArr))),
                                	moduleType : window.parent.moduleType
                                },
                                type: 'post',
                                success : function(result){
                                	BIONE.hideLoading();
                                    if (result.result) {
                                        if ("OK" == result.result) {
                                            if (result.zipFilePath && result.folderinfoPath) {
                                                var src = '';
                                                src = "${ctx}/rpt/frs/rptfill/downFile?zipFilePath=" + encodeURI(result.zipFilePath) + "&folderinfoPath=" + encodeURI(result.folderinfoPath) + "&d=" + new Date().getTime();
                                                window.parent.download.attr('src', src);
                                                if(typeof callback == "function"){
                                                	callback();
                                                }
                                            } else {
                                                BIONE.tip("数据异常，请联系系统管理员");
                                            }
                                        } else {
                                            BIONE.tip(result.msg);
                                        }
                                    } else {
                                        BIONE.tip("数据异常，请联系系统管理员");
                                    }
                                
                                },
                                error : function(){
                                	BIONE.hideLoading();
                                }
                            });
                        } else {
                            BIONE.tip("根据所选条件未查询到可下载的报表!");
                            BIONE.hideLoading();
                        }
                   	}else{
	                   	if(rptIdsArr.length > 0){
	                   		window.parent.selectedRptIdArr = result.rptNums;
							window.parent.BIONE.commonOpenDialog('导入数据文件', 'uploadWin', 600, 330, "${ctx}/rpt/frs/rptfill/showTab?path=rpt-todowork-uploadfile&dataDate=" + con.dataDate + "&orgNo=" + con.orgNm + "&rptId="  + "&taskInsId="  + "&lineId=*"  + "&type="  + "&flag=ONE&entry=GRID" + "&taskId=" + con.taskNm + "&taskFillOperType=35&operType=" + window.parent.rptOperType + "&importFileType=${importFileType}");
							BIONE.closeDialog("banchImpWin");
	                   	}else{
	                   		BIONE.tip("该任务下不存在[报表状态]为[未归档]的数据,不可导入!");
	                   	}
                   	}
                   	
               },
               error : function(){
            	   BIONE.hideLoading();
               }
            });
		}
		
		var btnHtml = '<div class="l-btn';
		btnHtml += '"><div class="l-btn-l"></div><div class="l-btn-r"> </div> <span>'+ btnNm[operType] +'</span></div>';
		var btn = $(btnHtml);
		btn.width("80px");
		btn.click(exportFn);
		$("#btn").append(btn);
		
		if(operType == "exp"){
			var closebtnHtml = '<div class="l-btn';
			closebtnHtml += '"><div class="l-btn-l"></div><div class="l-btn-r"> </div> <span>导出并关闭</span></div>';
			var closebtn = $(closebtnHtml);
			closebtn.width("80px");
			closebtn.click(function(){
				exportFn(function(){
					BIONE.closeDialog("banchImpWin");
				});
			});
			
			$("#btn").append(closebtn);
			
		}else{
			var tipHtml = '<span style="color:#3c8dbc;"><h3 style="margin-left:1em;margin-top:1em;">'+
			'提示:<br/>&nbsp;&nbsp;&nbsp;&nbsp;1.批量导入不会导入已归档的任务<br/>&nbsp;&nbsp;&nbsp;&nbsp;<h3></span>';
			$("#center").append(tipHtml);
			var tipHtml2 = '<span style="color:#3c8dbc;"><h3 style="margin-left:1em;">'+
			'&nbsp;&nbsp;&nbsp;&nbsp;2.文件格式：报表编号-机构号-数据日期.xls,如：G0101-001-20191219.xls<br/>&nbsp;&nbsp;&nbsp;&nbsp;<h3></span>';
			$("#center").append(tipHtml2);			
		}
		
		initData();
	});
			
	//初始化数据
	function initData(){
		var taskId = window.parent.$.ligerui.get("taskNm_sel").getValue();
		var taskNm = window.parent.$.ligerui.get("taskNm_sel").getText();
		var orgNo = window.parent.$.ligerui.get("orgNm_sel").getValue();
		var orgNm = window.parent.$.ligerui.get("orgNm_sel").getText();
		var dataDate = window.parent.$.ligerui.get("dataDate").getValue();
		if(taskId){
			$.ligerui.get("taskNm_sel").setValue(taskId);
			$.ligerui.get("taskNm_sel").setText(taskNm);
		}
		if(orgNo){
			$.ligerui.get("orgNm_sel").setValue(orgNo);
			$.ligerui.get("orgNm_sel").setText(orgNm);
		}
		if(dataDate){
			$.ligerui.get("dataDate").setValue(dataDate);
		}
	}
</script>
</head>
<div id="template.center">
	
<br/>
	<div id=search></div>
	
</div>
<div id="template.button">
	<div id=btn style="float:right;"></div>
</div>
<body>
</body>
</html>