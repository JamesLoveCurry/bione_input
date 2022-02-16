<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript" src="${ctx}/frs/js/rptfill/rpt-reject-index.js"></script>
<script type="text/javascript">
	var ctx = "${ctx}";
	var moduleType="${moduleType}";
	var operType = "${operType}";
	var QueryInit = false;//查询标识 QueryInit:false 正常值  QueryInit:true 初始值
	var rptOperType = "${rptOperType}";
	var taskComBoBoxUrl = "${ctx}/frs/rptfill/reject/taskNmComBoBox?orgTypes=${moduleType}&flag=1";//一次任务
	var rptComBoBoxUrl = "${ctx}/frs/rptfill/reject/rptNmComBoBox";
	var orgTreeSkipUrl = "${ctx}/frs/integratedquery/rptquery/searchOrgSetInfo?orgType=" + moduleType;
	var fields = [ "handSts", "taskNm", "rptNm", "orgNm","dataDate", "logicRsSts"];
	var gridUrl = "${ctx}/rpt/frs/rptfill/rptExportFrsTaskList?orgTypes=${moduleType}&operType=${operType}&rptOperType=${rptOperType}&notContainSts=0";//过滤掉未归档的任务
	var columns = ["taskNm", "dataDate", "endTime", "taskObjNm", "exeObjNm", "sts", "sumpartRs", "logicRs", "warnRs", "zeroRs"];
	var manager = null;
	//初始化函数
	$(function() {
		searchForm();
		initGrid();
		initButtons();
		initExport();
		addMySearchButtons("#search", grid, "#searchbtn");
		manager = $("#maingrid").ligerGetGridManager();
	});
	
	//搜索表单应用ligerui样式
	function searchForm() {
		var demoWidth = $("#search").width();
		var newLineNum = parseInt(demoWidth/260);
		CommonSerchForm(taskComBoBoxUrl, rptComBoBoxUrl, orgTreeSkipUrl, null, fields, "1", newLineNum);
	}
	
	//初始化Grid
	function initGrid() {
		TaskFillGrid(gridUrl, columns, false,null,false,false);
	}
	
	//初始化Button
	function initButtons() {
		var btns = [
			{ text : '导出Excel', click : oper_fdown, icon : 'fa-download', operNo : 'oper_fdown'},
			{ text : '查看收藏',   click : queryFav,   icon : 'fa-heart', operNo : 'queryFav'},
			{ text : '查看全部',   click : queryAll,   icon : 'fa-star', operNo : 'queryAll'},
			{ text : '设置收藏',  click : setFav,  icon : 'fa-heart-o', operNo : 'setFav'},
			{ text : '列表导出',  click : list_down, icon : 'fa-download', operNo : 'list_down'},
			{ text : '数据回灌',  click : data_imp, icon : 'fa-upload', operNo : 'data_imp'}
        ];
		//1104添加导出xml按钮
		if("02" == moduleType){
			btns.push({ text : '导出XML',  click : xmlDowmload, icon : 'fa-download', operNo : 'xmlDowmload'});
			//1104导出报表的excel命名标准定义
			btns[0].click = downCfg;
		}else if("05" == moduleType){//利率报备
			btns.push({ text : '导出XML',  click : xmlDowmload2, icon : 'fa-download', operNo : 'xmlDowmload2'});
		}
		if("17" == moduleType){
		    btns.push({ text : '整体导出',  click : all_fdown, icon : 'fa-download', operNo : 'all_fdown'});
        }
		BIONE.loadToolbar(grid, btns, function() {});
	}
	
	//导出XML
	function xmlDowmload(){
		var rows = manager.getSelectedRows();
		var rptIds = [];
		if(rows.length > 0){
			for(var i=0; i<rows.length; i++){
				if(rptIds.length == 0){
					rptIds.push(rows[i].taskObjId);
				} else if(rptIds.indexOf(rows[i].taskObjId) == -1){
					rptIds.push(rows[i].taskObjId);
				}else{
					BIONE.tip("一张报表只能选择一条数据，请重新选择！");
					return;
				}
				
				if(rows[i].sts == "0"){
					BIONE.tip("您选择的记录有未归档报表，请重新选择！");
					return;
				}
			}	
			BIONE.commonOpenDialog('报文配置', 'createXmlWin', '400', '350','${ctx}/rpt/frs/rptexport/xmlDowmload');
		}else{
			BIONE.tip("请选择要导出的报表！");
		}
	}
	
	//利率报备导出XML
	function xmlDowmload2(){
		var rows = manager.getSelectedRows();
		var argsArr = [];
		var dataDates = [];
		var orgNos = [];
		if(rows.length > 0){
			for(var i=0; i<rows.length; i++){
				if(orgNos.length == 0){
					orgNos.push(rows[i].exeObjId);
				} else if(orgNos.indexOf(rows[i].exeObjId) != -1){
					orgNos.push(rows[i].exeObjId);
				}else{
					BIONE.tip("只能选择同一机构下的报表，请重新选择！");
					return;
				}
				if(dataDates.length == 0){
					dataDates.push(rows[i].dataDate);
				} else if(dataDates.indexOf(rows[i].dataDate) != -1){
					dataDates.push(rows[i].dataDate);
				}else{
					BIONE.tip("只能选择同一数据日期下的报表，请重新选择！");
					return;
				}
				var rptId = rows[i].taskObjId;
				var rptNm = rows[i].taskObjNm;
				var orgNo = rows[i].exeObjId;
				var orgName = rows[i].exeObjNm;
				var dataDate = rows[i].dataDate;
				var taskInstanceId = rows[i].taskInstanceId;
				var busiLineId = rows[i].lineId;
	    	    if(rptId != null && orgNo != null && dataDate != null){
		    		var argsArr1 = [];
	    	       	var args1 = {'DimNo':'ORG','Op':'=','Value':orgNo};
	    	        argsArr1.push(args1);
	    	        var args = {'orgNo': orgNo, 'orgNm':orgName,'rptNm':rptNm,'rptId':rptId,'dataDate':dataDate,'busiLineId':busiLineId,"taskInstanceId":taskInstanceId,'searchArgs':JSON2.stringify(argsArr1)};
	    	        argsArr.push(args);
	   	      	}else{
		   	       BIONE.tip("数据异常，请联系系统管理员");
		   	    } 
			}	
			if(argsArr.length > 0){
	    		BIONE.ajax({
	    	         async : false,
	    	         url : "${ctx}/rpt/frs/rptexport/downloadXmlFile2",
	    	         dataType : 'json',
	    	         type : 'post',
	    	         data : {
	    	        	 rptList : JSON.stringify(argsArr)
	    	         },
	    	         loading : '正在生成下载文件，请稍等...'
	    	    },
	    	    function (result){
	    	    	if(result.result){
	    	    		if("OK" == result.result){
	    	    			if(result.filePath){
	    	    				 var src = '';
	    	    				 src = "${ctx}/rpt/frs/rptexport/downFile?&zipFilePath="+encodeURI(result.filePath);
	    	    				 window.download.attr('src', src);
	    	    			}else{
	    	    				BIONE.tip("数据异常，请联系系统管理员");
	    	    			}
	    	    		}else{
	    	    			BIONE.tip(result.msg);
	    	    		}
	    	    	}else{
	    	    		BIONE.tip("数据异常，请联系系统管理员");
	    	    	}
	    	    });
	    	}else{
	    		BIONE.tip("数据异常，请联系系统管理员");
	    	}
		}else{
			BIONE.tip("请选择要导出的报表！");
		}
	}
	
	//1104导出报表
	function downCfg(){
		var rows = manager.getSelectedRows();
		var rptIds = [];
		if(rows.length > 0){
			//判断是否有多个机构
			var orgs = [];
			for(var i=0; i<rows.length; i++){
				if(rows[i].sts == "0"){
					BIONE.tip("您选择的记录有未归档报表，请重新选择！");
					return;
				}
				var orgNo = rows[i].exeObjId;
				if(orgs.indexOf(orgNo) == -1){
					orgs.push(orgNo)
				}
			}

			if(orgs.length > 1){
				BIONE.tip("请选择同一机构报表进行导出");
				return;
			}

			BIONE.commonOpenDialog('导出配置', '1104DownCfg', '400', '350','${ctx}/rpt/frs/rptexport/1104DownCfg');
		}else{
			BIONE.tip("请选择要导出的报表！");
		}
	}
	
	//查看全部
	function queryAll(){
		 var form = $('#formsearch');
		   grid.removeParm("queryFav");
			if ($.ligerui.get("logicRsSts_sel") && $.ligerui.get("logicRsSts_sel").getValue()) {
				grid.setParm("logicRsSts", $.ligerui.get("logicRsSts_sel")
						.getValue());
			} else {
				grid.removeParm("logicRsSts");
			}
			if ($.ligerui.get("sumpartRsSts_sel") && $.ligerui.get("sumpartRsSts_sel").getValue()) {
				grid.setParm("sumpartRsSts", $.ligerui.get("sumpartRsSts_sel")
						.getValue());
			} else {
				grid.removeParm("sumpartRsSts");
			}
			if ($.ligerui.get("warnRsSts_sel") && $.ligerui.get("warnRsSts_sel").getValue()) {
				grid.setParm("warnRsSts", $.ligerui.get("warnRsSts_sel")
						.getValue());
			} else {
				grid.removeParm("warnRsSts");
			}
			if ($.ligerui.get("zeroRsSts_sel") && $.ligerui.get("zeroRsSts_sel").getValue()) {
				grid.setParm("zeroRsSts", $.ligerui.get("zeroRsSts_sel")
						.getValue());
			} else {
				grid.removeParm("zeroRsSts");
			}
	       grid.loadData();
	}
	
	//查看收藏 ---add
	function queryFav(){
		 var form = $('#formsearch');
	   	   //收藏查询
		   grid.setParm("queryFav","1");
			if ($.ligerui.get("logicRsSts_sel") && $.ligerui.get("logicRsSts_sel").getValue()) {
				grid.setParm("logicRsSts", $.ligerui.get("logicRsSts_sel")
						.getValue());
			} else {
				grid.removeParm("logicRsSts");
			}
			if ($.ligerui.get("sumpartRsSts_sel") && $.ligerui.get("sumpartRsSts_sel").getValue()) {
				grid.setParm("sumpartRsSts", $.ligerui.get("sumpartRsSts_sel")
						.getValue());
			} else {
				grid.removeParm("sumpartRsSts");
			}
			if ($.ligerui.get("warnRsSts_sel") && $.ligerui.get("warnRsSts_sel").getValue()) {
				grid.setParm("warnRsSts", $.ligerui.get("warnRsSts_sel")
						.getValue());
			} else {
				grid.removeParm("warnRsSts");
			}
			if ($.ligerui.get("zeroRsSts_sel") && $.ligerui.get("zeroRsSts_sel").getValue()) {
				grid.setParm("zeroRsSts", $.ligerui.get("zeroRsSts_sel")
						.getValue());
			} else {
				grid.removeParm("zeroRsSts");
			}
	       var rule = BIONE.bulidFilterGroup(form);
	       if (rule.rules.length) {
	        	grid.setParm("condition",JSON2.stringify(rule));
	        	grid.setParm("newPage",1);
	       } else {
	       		grid.setParm("condition","");
	        	grid.setParm('newPage', 1);
	       }
	       grid.loadData();
	}
	
	

	// 创建表单搜索按钮：搜索、高级搜索
	function addMySearchButtons(form, grid, btnContainer) {
		if (!form)
			return;
		form = $(form);
		if (btnContainer) {
			BIONE.createButton({
				appendTo : btnContainer,
				text : '查询',
				icon : 'fa-search',
				// width : '50px',
				click : function() {
					if ($.ligerui.get("logicRsSts_sel") && $.ligerui.get("logicRsSts_sel").getValue()) {
						grid.setParm("logicRsSts", $.ligerui.get("logicRsSts_sel")
								.getValue());
					} else {
						grid.removeParm("logicRsSts");
					}
					if ($.ligerui.get("sumpartRsSts_sel") && $.ligerui.get("sumpartRsSts_sel").getValue()) {
						grid.setParm("sumpartRsSts", $.ligerui.get("sumpartRsSts_sel")
								.getValue());
					} else {
						grid.removeParm("sumpartRsSts");
					}
					if ($.ligerui.get("warnRsSts_sel") && $.ligerui.get("warnRsSts_sel").getValue()) {
						grid.setParm("warnRsSts", $.ligerui.get("warnRsSts_sel")
								.getValue());
					} else {
						grid.removeParm("warnRsSts");
					}
					if ($.ligerui.get("zeroRsSts_sel") && $.ligerui.get("zeroRsSts_sel").getValue()) {
						grid.setParm("zeroRsSts", $.ligerui.get("zeroRsSts_sel")
								.getValue());
					} else {
						grid.removeParm("zeroRsSts");
					}
					var rule = BIONE.bulidFilterGroup(form);
					if (rule.rules.length) {
						grid.setParm("condition", JSON2.stringify(rule));
						grid.setParm("newPage", 1);
					} else {
						grid.setParm("condition", "");
						grid.setParm('newPage', 1);
					}
					grid.loadData();
				}
			});
	 		BIONE.createButton({
				appendTo : btnContainer,
				text : '重置',
				icon : 'fa-repeat',
				// width : '50px',
				click : function() {
					$(":input", form).not(":submit, :reset,:hidden,:image,:button, [disabled]").each(function() {
						$(this).val("");
					});
					$(":input[ltype=combobox]", form).each(function() {
						var ligerid = $(this).attr(
								'data-ligerid'), ligerItem = $.ligerui
								.get(ligerid);// 需要配置comboboxName属性
						if (ligerid && ligerItem
								&& ligerItem.clear) {// ligerUI
							// 1.2
							// 以上才支持clear方法
							ligerItem.clear();
						} else {
							$(this).val("");
						}
						grid.removeParm("logicRsSts");
						grid.removeParm("sumpartRsSts");
						grid.removeParm("warnRsSts");
					});
					$(":input[ltype=select]", form).each(function() {
						var ligerid = $(this).attr(
								'data-ligerid'), ligerItem = $.ligerui
								.get(ligerid);// 需要配置comboboxName属性
						if (ligerid && ligerItem
								&& ligerItem.clear) {// ligerUI
							// 1.2
							// 以上才支持clear方法
							ligerItem.clear();
						} else {
							$(this).val("");
						}
						grid.removeParm("logicRsSts");
						grid.removeParm("sumpartRsSts");
						grid.removeParm("warnRsSts");
					});
				}
			}); 
		}
	}

	function all_fdown(){
	    QueryInit = true;
        var rows = manager.getSelectedRows();
        if(rows.length > 0){
            var argsArr = [];
            var orgFirst = rows[0].exeObjId;
            var dataFirst = rows[0].dataDate;
            for(var i in rows){
                var orgName = rows[i].exeObjNm;
                var rptId = rows[i].taskObjId;
                var orgNo = rows[i].exeObjId;
                var busiLineId = rows[i].lineId;
                var dataDate = rows[i].dataDate;
                if(orgFirst !=orgNo){
                    BIONE.showError("请选择同一日期同一机构的报表导出");
                    return;
                }
                if(dataFirst!=dataDate){
                    BIONE.showError("请选择同一日期同一机构的报表导出");
                    return;
                }

                if(rptId != null && orgNo != null && dataDate != null){
                       var argsArr1 = [];
                       var args1 = {'DimNo':'ORG','Op':'=','Value':orgNo};
                       argsArr1.push(args1);
                       var args = {'orgNm':orgName,'rptId':rptId,'dataDate':dataDate,'busiLineId':busiLineId,'searchArgs':JSON2.stringify(argsArr1)};
                       argsArr.push(args);
                }else{
                   BIONE.tip("数据异常，请联系系统管理员");
                }
            }

            if(argsArr.length > 0){
                BIONE.ajax({
                    async : false,
                    url : "${ctx}/rpt/frs/rptfill/rptdownloadAllList",
                    dataType : 'json',
                    data : {
                        json : JSON2.stringify(argsArr),
                        QueryInit : QueryInit,
                        moduleType : moduleType
                    },
                    type : 'post',
                    loading : '正在生成下载文件，请稍等...'
                },
                function (result){
                    if(result.result){
                        if("OK" == result.result){
                            if(result.zipFilePath && result.folderinfoPath){
                                 var src = '';
                                 src = "${ctx}/rpt/frs/rptfill/downFile?&zipFilePath="+encodeURIComponent(encodeURIComponent(result.zipFilePath))+"&folderinfoPath=" + encodeURI(result.folderinfoPath) + "&d="+ new Date() + "&taskFillOperType=36&operType="+rptOperType+"&taskInsId=" + rows[0].taskInstanceId;
                                 download.attr('src', src);
                            }else{
                                BIONE.tip("数据异常，请联系系统管理员");
                            }
                        }else{
                            BIONE.tip(result.msg);
                        }
                    }else{
                        BIONE.tip("数据异常，请联系系统管理员");
                    }
                });
            }else{
                BIONE.tip("数据异常，请联系系统管理员");
            }
        }else{
         BIONE.tip("请选择需要下载的记录");
        }
	}
	function oper_fdown(){
		QueryInit = true;
	    var rows = manager.getSelectedRows();
	    if(rows.length > 0){
	    	var argsArr = [];
	    	for(var i in rows){
	    		var orgName = rows[i].exeObjNm;
	    	    var rptId = rows[i].taskObjId;
	    	    var orgNo = rows[i].exeObjId;
	    	    var busiLineId = rows[i].lineId;
	    	    var dataDate = rows[i].dataDate;
	    	
		    	if(rptId != null && orgNo != null && dataDate != null){
		    	       var argsArr1 = [];
		    	       var args1 = {'DimNo':'ORG','Op':'=','Value':orgNo};
		    	       argsArr1.push(args1);
		    	       var args = {'orgNm':orgName,'rptId':rptId,'dataDate':dataDate,'busiLineId':busiLineId,'searchArgs':JSON2.stringify(argsArr1)};
		    	       argsArr.push(args);
	   	      	}else{
		   	       BIONE.tip("数据异常，请联系系统管理员");
		   	    }
	    	}

	    	if(argsArr.length > 0){
	    		BIONE.ajax({
   	        	 	async : false,
    	         	url : "${ctx}/rpt/frs/rptfill/rptdownloadList",
    	         	dataType : 'json',
    				data : {
    					json : JSON2.stringify(argsArr),
    					QueryInit : QueryInit,
    					moduleType : moduleType
    				},
	    	        type : 'post',
	    	        loading : '正在生成下载文件，请稍等...'
	    	    },
	    	    function (result){
	    	    	if(result.result){
	    	    		if("OK" == result.result){
	    	    			if(result.zipFilePath && result.folderinfoPath){
	    	    				 var src = '';
	    	    				 src = "${ctx}/rpt/frs/rptfill/downFile?&zipFilePath="+encodeURIComponent(encodeURIComponent(result.zipFilePath))+"&folderinfoPath=" + encodeURI(result.folderinfoPath) + "&d="+ new Date() + "&taskFillOperType=36&operType="+rptOperType+"&taskInsId=" + rows[0].taskInstanceId;
	    	    				 download.attr('src', src);
	    	    			}else{
	    	    				BIONE.tip("数据异常，请联系系统管理员");
	    	    			}
	    	    		}else{
	    	    			BIONE.tip(result.msg);
	    	    		}
	    	    	}else{
	    	    		BIONE.tip("数据异常，请联系系统管理员");
	    	    	}
	    	    });
	    	}else{
	    		BIONE.tip("数据异常，请联系系统管理员");
	    	}
	    }else{
	     BIONE.tip("请选择需要下载的记录");
	    }
	}
	//批量导出结束
	function setFav(){
		var title = "设置收藏";
		var height = $("#center").height() - 50;
		var width = "300";
		BIONE.commonOpenDialog(title, "setWatchWin", width, height, "${ctx}/rpt/frs/rptexport/setFav?&orgType=${moduleType}", null,null);
	}
	function list_down(){
		 var data = queryDownLoadList();
		 var condition = data.condition;
		 var src = "${ctx}/rpt/frs/rptfill/rptExportFrsTaskList?orgTypes=${moduleType}&operType=${operType}&rptOperType=${rptOperType}&isListDownload=1&isRptExp=Y&notContainSts=0&otherCondition="+encodeURI(encodeURI(condition));
		 download.attr('src', src);
	}
	function initExport(){
	   download = $('<iframe id="download"  style="display: none;"/>');
	   $('body').append(download);  
	}
	function queryDownLoadList(){
		var form = $('#formsearch');
		var data = {};
		if ($.ligerui.get("logicRsSts_sel") && $.ligerui.get("logicRsSts_sel").getValue()) {
			data.logicRsSts = $.ligerui.get("logicRsSts_sel").getValue();
		}
		if ($.ligerui.get("sumpartRsSts_sel") && $.ligerui.get("sumpartRsSts_sel").getValue()) {
			data.sumpartRsSts = $.ligerui.get("sumpartRsSts_sel")
					.getValue();
		}
		if ($.ligerui.get("warnRsSts_sel") && $.ligerui.get("warnRsSts_sel").getValue()) {
			data.warnRsSts = $.ligerui.get("warnRsSts_sel").getValue();
		}
		if ($.ligerui.get("zeroRsSts_sel") && $.ligerui.get("zeroRsSts_sel").getValue()) {
			data.zeroRsSts = $.ligerui.get("zeroRsSts_sel").getValue();
		}
  	 	var rule = BIONE.bulidFilterGroup(form);
  	 	if (rule.rules.length) {
			data.condition=JSON2.stringify(rule);
		}else{
			data.condition="";
		}
  	  	return data; 
	}

	function data_imp(){
		BIONE.commonOpenDialog(
						'数据回灌',
						'uploadWin',
						600,
				        378,
						"${ctx}/frs/integratedquery/rptquery/rptUploadData");
	}
</script>
</head>
<body>
</body>
</html>