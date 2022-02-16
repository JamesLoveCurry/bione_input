/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文生成】
 * Description: 报文生成：主页面
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年5月26日
 */

var tab_name = "";
var tab_name_en = "";
var str_subfix = "";
var str_className = "";

function AfterInit() {
	// 获取路径参数
	if (jso_OpenPars != '') {
		if(jso_OpenPars.type != null) {
			str_subfix = jso_OpenPars.type;
		}
	}
	// 获取【报文任务表】常量类
	tab_name = SafeFreeUtil.getTableNames().SAFE_CR_REPORT;
	// 获取英文表名
	var jso_data = JSPFree.doClassMethodCall(
			"com.yusys.safe.base.common.service.SafeCommonBS",
			"getTabNameByEn", {tab_name:tab_name, report_type:str_subfix});
	tab_name_en = jso_data.tab_name_en;

	str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + str_subfix + "','" + str_LoginUserOrgNo + "')";
	JSPFree.createBillList("d1", str_className, null, {list_btns:"[icon-p20]生成报文/onStartReport;[icon-remove]删除报文/onDeleteReport;[icon-p47]打包下载/onZipAndDownload;[icon-p58]查看日志/onViewLog;$VIEWTASK;[icon-p48]上传报文/uploadMessage;", isSwitchQuery:"N",list_ischeckstyle:"Y", list_ismultisel:"Y", orderbys:"org_no",card_size:"900*500"});
}

/**
 * 生成报文
 * @returns
 */
function onStartReport() {
	var selectDatas = d1_BillList.datagrid('getSelections');
	var checkFlag = false;
	var taskRidArray = [];
	if (selectDatas.length > 0) {
		for (var i=0; i<selectDatas.length; i++) {
			if (i > 0) {
				if(selectDatas[i].data_dt != selectDatas[i-1].data_dt) {
					checkFlag = true;
				}
			}
			taskRidArray.push(selectDatas[i].rid);
		}
		if (checkFlag) {
			$.messager.alert('提示', '只能选择同一日期的记录进行操作', 'warning');
			return;
		}
		
		var jso_par = {
			task_rids :taskRidArray,
			report_type: str_subfix,
			status_sql_where: " and (data_status = '0' or data_status is null) and approval_status = '2' "
		};
		// 选择生成报文时，所选择的表都需要通过校验,才可生成报文，否则提示
		var checkResult = JSPFree.doClassMethodCall(
			"com.yusys.safe.reporttask.service.SafeCrReportBS", "getTableNamesByStatus", jso_par);
		if (checkResult.status === 'ok') {
			var tables = checkResult.tableNames;
			if (tables.length > 0) {
				$.messager.alert('提示', JSON.stringify(tables) + '表存在未校验通过数据,不能生成报文,请重新选择', 'info');
				return;
			}
		} else {
			$.messager.alert('创建报文失败！', 'info');
			return;
		}
		
		onStartReportDetail('selections',selectDatas,false);
	} else {
		// steep1：创建任务
		var jso_par = {className:str_className, reportType:str_subfix};  // 参数
		JSPFree.openDialog("生成报文","/yujs/safe/reportTask/report_task_create.js",700,500,jso_par,function(_rtdata){
			if (_rtdata != null) {
				debugger;
				if ("dirclose" == _rtdata.type) {
					return;
				}
				JSPFree.queryDataByConditon(d1_BillList,_rtdata.wheresql);  // 立即查询数据
				var sql = "select * from  safe_cr_report where " + _rtdata.wheresql;
				var taskData = JSPFree.getHashVOs(sql);
				
				var isAllConfirm = _rtdata.isAllConfirm;
				debugger;
				// steep2：启动任务
				onStartReportDetail('unSelections', taskData,isAllConfirm);
			}
		});
	}
}

/**
 * 生成报文后，进行启动任务
 */
function onStartReportDetail(flag,taskData,isAllConfirm) {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if ("unSelections" === flag) {
		selectDatas = taskData;
	}
	
	var isHaveAlreadyStart = false;
	var ary_taskids = [];  // 所有rid数组
	var data_dt = selectDatas[0].data_dt; // 数据日期
	
	var data_dt_start = selectDatas[0].data_dt_start; // 数据开始日期
	var data_dt_end = selectDatas[0].data_dt_end; // 数据结束日期
	var org_no = selectDatas[0].org_no;// 报送机构号
	var ary_tableName = []; // 所有的表名
	
	for (var i=0; i<selectDatas.length; i++) {
		// 判断当前选中的任务，状态是否是已生成完的报文；如果是已生成，则弹出提示，是否要覆盖
		if (SafeFreeUtil.getProcessReportStatus().COMPLETE == selectDatas[i].status) {
			isHaveAlreadyStart = true;
		}
		if ("单位金融机构标识码信息表"==selectDatas[i].tab_name || "SAFE_CORP_FIN_INFO"==selectDatas[i].tab_name_en){
			continue;
		}
		ary_taskids.push(selectDatas[i].rid);
		ary_tableName.push(selectDatas[i].tab_name);
	}
	
	if (isHaveAlreadyStart) {
		var message = "";
		// 全部生成时,如果已经生成过报文，数据未做任何修改，则提示是否需要重新生成
		if (isAllConfirm) {
			// 判断报表数据是否存在修改
			var jso_par = {
				taskids: ary_taskids,
				tasktables: ary_tableName,
				report_type: str_subfix
			}
			// 判断那些报表存在修改的数据,可以生成报文的数据
			var checkResult = JSPFree.doClassMethodCall(
				"com.yusys.safe.reporttask.service.SafeCrReportBS", "getHaveModifyDataTableNamesByBwStatus", jso_par);
			if (checkResult.status === 'ok') {
				if (checkResult.tableNames.length == 0) {
					// 数据未修改，则提示
					message = " 数据没有任何修改，是否确认生成？";
					JSPFree.confirmYesOrNo('提示', str_subfix + message, function (_isOK) {
						if (isAllConfirm && !_isOK) {
							return;
						}
						var param = {
							allTaskIds: ary_taskids,
							data_dt: data_dt,
							allTableNames: ary_tableName,
							report_type: str_subfix,
							is_create_empty_report: _isOK,
							isAllConfirm: isAllConfirm,
							data_dt_start: data_dt_start,
							data_dt_end: data_dt_end,
							org_no:org_no
						};
						openStartReportWindow(param);
					});
				} else {
					// 需要生成报文,无需提示
					var param = {
						allTaskIds: ary_taskids,
						data_dt: data_dt,
						allTableNames: ary_tableName,
						report_type: str_subfix,
						is_create_empty_report: false,
						isAllConfirm: isAllConfirm,
						data_dt_start: data_dt_start,
						data_dt_end: data_dt_end,
						org_no:org_no
					};
					openStartReportWindow(param);
				}
			} else {
				$.messager.alert('提示', '启动报文任务失败，请重新启动！', 'info');
				return;
			}
		} else {
			message = " 模块已生成报文，是否生成空报文?";
			// 国结模块不生成空报文
			if (str_subfix != "CRD" && str_subfix != "CRX") {
				var param = {
					allTaskIds: ary_taskids,
					data_dt: data_dt,
					allTableNames: ary_tableName,
					report_type: str_subfix,
					// 此标识是控制是否生成空报文的,false 则不创建空报文
					is_create_empty_report: false,
					isAllConfirm: isAllConfirm,
					data_dt_start: data_dt_start,
					data_dt_end: data_dt_end,
					org_no:org_no
				};
				openStartReportWindow(param);
			} else {
				// 银行卡/消费 模块需要生成空报文
				JSPFree.confirmYesOrNo('提示', str_subfix + message, function (_isOK) {
					if (isAllConfirm && !_isOK) {
						return;
					}
					var param = {
						allTaskIds: ary_taskids,
						data_dt: data_dt,
						allTableNames: ary_tableName,
						report_type: str_subfix,
						is_create_empty_report: _isOK,
						isAllConfirm: isAllConfirm,
						data_dt_start: data_dt_start,
						data_dt_end: data_dt_end,
						org_no:org_no
					};
					openStartReportWindow(param);
				});
			}
		}
		
	} else {
		message = " 模块已生成报文，是否生成空报文?";
		// 国结模块不生成空报文
		if (str_subfix != "CRD" && str_subfix != "CRX") {
			var param = {
				allTaskIds: ary_taskids,
				data_dt: data_dt,
				allTableNames: ary_tableName,
				report_type: str_subfix,
				// 此标识是控制是否生成空报文的,false 则不创建空报文
				is_create_empty_report: false,
				isAllConfirm: isAllConfirm,
				data_dt_start: data_dt_start,
				data_dt_end: data_dt_end,
				org_no:org_no
			};
			openStartReportWindow(param);
		} else {
			// 银行卡/消费 模块需要生成空报文
			JSPFree.confirmYesOrNo('提示', str_subfix + message, function (_isOK) {
				if (isAllConfirm && !_isOK) {
					return;
				}
				var param = {
					allTaskIds: ary_taskids,
					data_dt: data_dt,
					allTableNames: ary_tableName,
					report_type: str_subfix,
					is_create_empty_report: _isOK,
					isAllConfirm: isAllConfirm,
					data_dt_start: data_dt_start,
					data_dt_end: data_dt_end,
					org_no:org_no
				};
				openStartReportWindow(param);
			});
		}
	}
}

function openStartReportWindow(param) {
	JSPFree.openDialog2("启动选择的任务", "/yujs/safe/reportTask/report_task_start.js", 750, 350, param);
}

/**
 * 删除报文
 * @returns
 */
function onDeleteReport() {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}

	var tab_name = "";
	var filePaths = [];
	var taskIds = [];
	for(var i=0; i<selectDatas.length; i++){
		if (selectDatas[i].status.match(SafeFreeUtil.getProcessReportStatus().PROCESSING)) {
			tab_name += selectDatas[i].tab_name + ","
			filePaths.push(selectDatas[i].filepath);
			taskIds.push(selectDatas[i].rid);
		}
	}
	if (tab_name != null && tab_name != "") {
		$.messager.alert('提示', '包含生成报文中的任务，不能删除', 'warning');
	} else {
		// 执行删除(批量)
		var str_divid = d1_BillList.divid; //
		var str_beforeDeleteFn = "beforeDelete_" + str_divid + "_BillList"; // 删除前校验
		if (typeof self[str_beforeDeleteFn] == "function") { // 如果有这个函数
			var isOK = self[str_beforeDeleteFn](d1_BillList, selectDatas); // 执行
			if (!isOK) {
				return; // 如果失败则返回
			}
		}

		// 警告提醒是否真的删除?
		$.messager.confirm('提示', '你真的要删除选中的记录吗?', function(_isConfirm) {
			if (!_isConfirm) {
				return;
			}

			var jso_templetVO = d1_BillList.templetVO; // 模板对象
			var str_ds = jso_templetVO.templet_option.ds; // 数据源
			var str_templetcode = jso_templetVO.templet_option.templetcode; // 模板编码
			var str_savetable = jso_templetVO.templet_option.savetable; // 保存的表名,删除时不要重新查模板了,直接送表名,性能高一点!
			var selectRows = [];
			for (var i = 0; i < selectDatas.length; i++) {
				selectRows.push(selectDatas[i]);
			}
			// 远程调用,真正删除数据库
			try {
				for (var i = 0; i < selectRows.length; i++) {
					var str_rownumValue = selectRows[i]['_rownum']; // 取得行号数据
					var int_selrow = d1_BillList.datagrid("getRowIndex", str_rownumValue); // 根据_rownum的值,计算出是第几行,后面刷新就是这一行!
					var str_sqlwhere = FreeUtil.getSQLWhereByPK(jso_templetVO, selectRows[i]); // 拼出SQL
					JSPFree.doClassMethodCall("com.yusys.safe.reporttask.service.SafeCrReportBS", "deleteFile", {filepath: selectRows[i].filepath,rid:selectRows[i].rid});
					FreeUtil.execDeleteBillListdData(str_ds, str_templetcode, str_savetable, str_sqlwhere);
					// 从界面上删除行
					d1_BillList.datagrid('deleteRow', int_selrow);
				}
				$.messager.alert('提示', '删除数据成功!', 'info');
			} catch (_ex) {
				console.log(_ex);
				FreeUtil.openHtmlMsgBox("发生异常", 500, 250, _ex)
			}
		});
	}
}

/**
 * 打包下载报文
 * @returns
 */
function onZipAndDownload() {
	var jso_par = {className:str_className, reportType:str_subfix};
	JSPFree.openDialog("一键打包下载本机构某一时间的所有报文", "/yujs/safe/reportTask/report_task_choosedate.js", 500, 400, jso_par);
}

/**
 * 查看日志
 * @returns
 */
function onViewLog() {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'info');
		return;
	} else if (selectDatas.length > 1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'info');
		return;
	}

	var json_data = selectDatas[0];
	if (json_data == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'info');
	}

	JSPFree.openDialog("查看日志","/yujs/safe/reportTask/report_task_viewlog.js",900,420,{rid:json_data.rid, type: str_subfix},function(_rtdata){
		//回调方法,立即查询数据
	});
}

/**
 * 上传报文
 */
function uploadMessage(){
	var jso_par = {className:str_className, reportType:str_subfix};
	JSPFree.openDialog("上传报文","/yujs/safe/reportTask/report_task_send.js",500,400,jso_par,function (_rt){
		if (_rt!=null){
			if (_rt.status=="success"){
				$.messager.alert("提示","上传报文成功","info");
			}else if (_rt.type!="dirclose") {
				$.messager.alert("提示",_rt.msg,"warning");
			}
		}
	});

}
