(function($) {
	$.extend(app, {
		// 构建全行报表异步树
		iniRptTree : function(data) {
			if(data){
				liger.get('orgTypeBox').setValue(data);
				$('#orgTypeBox').blur();
			}else{
				var setting = {
						check : {
							enable : app.nodeType == '2',	// 如果是报表指标查询的话没有复选框
							chkStyle : "checkbox",
							chkboxType: { "Y": "", "N": "ps" }
						},
						data : {
							key : {
								name : 'text'
							},
							simpleData : {
								enable : true,
								idKey : "id",
								pIdKey : "upId"
							}
						},
						callback : {
							beforeCheck : function(treeId, treeNode) {
								var nodes = $(app.zTreeObj.getCheckedNodes(true)).filter(function(i, n) {
									return n.nodeType == '2';	// nodeType: 1.目录; 2.报表; 3.条线
								}).toArray();
								app.canEditBusiType = nodes.length == 0;
							},
							onCheck : function(event, treeId, treeNode) {
								var nodes = app.zTreeObj.getCheckedNodes(true);
								if (app.canEditBusiType) {
									var busiType = null;
									for (var i = 0, len = nodes.length; i < len; i++) {
										if (nodes[i].busiType) {
											var busiType = nodes[i].busiType;
											break;
										}
									}
									liger.get('orgTypeBox').setValue(busiType);
									$('#orgTypeBox').blur();
								}
								if (treeNode.nodeType == '3') {	// nodeType: 1.目录; 2.报表; 3.条线
									app.zTreeObj.checkNode(treeNode.getParentNode(), true, false, true);
								}
							},
							onClick : function(event, treeId, treeNode) {
								app.zTreeObj.expandNode(treeNode);	// 点击节点名称也展开节点
							}
						}
					};
				app.zTreeObj = $.fn.zTree.init($("#tree"), setting);
				BIONE.ajax({
					data : { nodeType : app.nodeType ,defSrc:"01"},
					url : app.ctx + '/frs/integratedquery/rptquery/searchRptForTree.json'
				}, function(result) {
					$(result).each(function(i, n) {
						if (n.nodeType == '1') {
							n.icon = app.iconFolder;
						} else if (n.nodeType == '2') {
							n.icon = app.iconRpt;
						} else if (n.nodeType == '3') {
							n.icon = app.iconLine;
						}
					});
					var zTreeObj = app.zTreeObj;
					result.unshift({ id: '0', text: '报表树', upId: '0', isParent: true, open: true });
					zTreeObj.addNodes(zTreeObj.getNodeByParam('id', '0'), result);
					// 反选报表
					if (app.queryObj && typeof app.queryObj === 'object') {
						if (app.nodeType == '2') {
							$(app.queryObj.rptInfo).each(function(i, n) {
								var node = zTreeObj.getNodeByParam('id', n.id);
								zTreeObj.checkNode(node, true, false, true);
								// zTreeObj.expandNode(node, true);
								zTreeObj.expandNode(node.getParentNode(), true);
							});
						} else if (app.nodeType == '3') {
							var node = zTreeObj.getNodeByParam('id', app.queryObj.rptInfo[0].id);
							zTreeObj.selectNode(node, false);
							zTreeObj.expandNode(node, true);
							app.rptIdx_onClickRptNode(node);
							grid.addRows(app.queryObj.rptidxRel);
							
							
						}
					}
				});
			}
		},
		iniOrgRptTree : function(userInfo) {
			var setting = {
				check : {
					enable : app.nodeType == '2',	// 如果是报表指标查询的话没有复选框
					chkStyle : "checkbox",
					chkboxType: { "Y": "", "N": "ps" }
				},
				data : {
					key : {
						name : 'text'
					},
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId"
					}
				},
				callback : {
					beforeCheck : function(treeId, treeNode) {
						var nodes = $(app.zTreeObj_org.getCheckedNodes(true)).filter(function(i, n) {
							return n.nodeType == '2';	// nodeType: 1.目录; 2.报表; 3.条线
						}).toArray();
						app.canEditBusiType = nodes.length == 0;
					},
					onCheck : function(event, treeId, treeNode) {
						var nodes = app.zTreeObj_org.getCheckedNodes(true);
						if (app.canEditBusiType) {
							var busiType = null;
							for (var i = 0, len = nodes.length; i < len; i++) {
								if (nodes[i].busiType) {
									var busiType = nodes[i].busiType;
									break;
								}
							}
							liger.get('orgTypeBox').setValue(busiType);
							$('#orgTypeBox').blur();
						}
						if (treeNode.nodeType == '3') {	// nodeType: 1.目录; 2.报表; 3.条线
							app.zTreeObj_org.checkNode(treeNode.getParentNode(), true, false, true);
						}
					},
					onClick : function(event, treeId, treeNode) {
						app.zTreeObj_org.expandNode(treeNode);	// 点击节点名称也展开节点
					}
				}
			};
			app.zTreeObj_org = $.fn.zTree.init($("#tree1"), setting);
			BIONE.ajax({
				data : { nodeType : app.nodeType ,defSrc:"02"},
				url : app.ctx + '/frs/integratedquery/rptquery/searchOrgRptForTree.json'
			}, function(result) {
				$(result).each(function(i, n) {
					if (n.nodeType == '1') {
						n.icon = app.iconFolder;
					} else if (n.nodeType == '2') {
						n.icon = app.iconRpt;
					} else if (n.nodeType == '3') {
						n.icon = app.iconLine;
					}
				});
				var zTreeObj_org = app.zTreeObj_org;
				//result.unshift({ id: '0', text: userInfo.orgName, upId: '0', isParent: true, open: true });
				zTreeObj_org.addNodes(zTreeObj_org.getNodeByParam('id', '0'), result);
				
				// 反选报表
				if (app.queryObj && typeof app.queryObj === 'object') {
					if (app.nodeType == '2') {
						$(app.queryObj.rptInfo).each(function(i, n) {
							var node = zTreeObj_org.getNodeByParam('id', n.id);
							zTreeObj_org.checkNode(node, true, false, true);
							// zTreeObj.expandNode(node, true);
							zTreeObj_org.expandNode(node.getParentNode(), true);
						});
					} else if (app.nodeType == '3') {
						var node = zTreeObj_org.getNodeByParam('id', app.queryObj.rptInfo[0].id);
						zTreeObj_org.selectNode(node, false);
						zTreeObj_org.expandNode(node, true);
						app.rptIdx_onClickRptNode(node);
						grid.addRows(app.queryObj.rptidxRel);
					}
				}
			});
		},
		// 构建用户自定义报表异步树
		iniUserRptTree : function(userInfo) {
			var setting = {
				check : {
					enable : app.nodeType == '2',	// 如果是报表指标查询的话没有复选框
					chkStyle : "checkbox",
					chkboxType: { "Y": "", "N": "ps" }
				},
				data : {
					key : {
						name : 'text'
					},
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId"
					}
				},
				callback : {
					beforeCheck : function(treeId, treeNode) {
						var nodes = $(app.zTreeObj_user.getCheckedNodes(true)).filter(function(i, n) {
							return n.nodeType == '2';	// nodeType: 1.目录; 2.报表; 3.条线
						}).toArray();
						app.canEditBusiType = nodes.length == 0;
					},
					onCheck : function(event, treeId, treeNode) {
						var nodes = app.zTreeObj_user.getCheckedNodes(true);
						if (app.canEditBusiType) {
							var busiType = null;
							for (var i = 0, len = nodes.length; i < len; i++) {
								if (nodes[i].busiType) {
									var busiType = nodes[i].busiType;
									break;
								}
							}
							liger.get('orgTypeBox').setValue(busiType);
							$('#orgTypeBox').blur();
						}
						if (treeNode.nodeType == '3') {	// nodeType: 1.目录; 2.报表; 3.条线
							app.zTreeObj_user.checkNode(treeNode.getParentNode(), true, false, true);
						}
					},
					onClick : function(event, treeId, treeNode) {
						app.zTreeObj_user.expandNode(treeNode);	// 点击节点名称也展开节点
					}
				}
			};
			app.zTreeObj_user = $.fn.zTree.init($("#tree2"), setting);
			BIONE.ajax({
				data : { nodeType : app.nodeType ,defSrc:"03"},
				url : app.ctx + '/frs/integratedquery/rptquery/searchUserRptForTree'
			}, function(result) {
				$(result).each(function(i, n) {
					if (n.nodeType == '1') {
						n.icon = app.iconFolder;
					} else if (n.nodeType == '2') {
						n.icon = app.iconRpt;
					} else if (n.nodeType == '3') {
						n.icon = app.iconLine;
					}
				});
				var zTreeObj_user = app.zTreeObj_user;
				result.unshift({ id: '0', text: userInfo.userNo, upId: '0', isParent: true, open: true });
				zTreeObj_user.addNodes(zTreeObj_user.getNodeByParam('id', '0'), result);
				
				// 反选报表
				if (app.queryObj && typeof app.queryObj === 'object') {
					if (app.nodeType == '2') {
						$(app.queryObj.rptInfo).each(function(i, n) {
							var node = zTreeObj_user.getNodeByParam('id', n.id);
							zTreeObj_user.checkNode(node, true, false, true);
							// zTreeObj.expandNode(node, true);
							zTreeObj_user.expandNode(node.getParentNode(), true);
						});
					} else if (app.nodeType == '3') {
						var node = zTreeObj_user.getNodeByParam('id', app.queryObj.rptInfo[0].id);
						zTreeObj_user.selectNode(node, false);
						zTreeObj_user.expandNode(node, true);
						app.rptIdx_onClickRptNode(node);
						grid.addRows(app.queryObj.rptidxRel);
					}
				}
			});
		},
		// 构建我的查询异步树
		myQuery_iniAsyncTree : function() {
			var setting = {
				async: {
					enable : true,
					dataType : 'json',
					otherParam : {},
					autoParam : ['id'],
					url : app.ctx + '/frs/integratedquery/myrpt/searchFolderForTree.json?d='+new Date().getTime(),
					dataFilter: function(treeId, parentNode, responseData) {
						$.each(responseData, function (i, n) {
							if(parentNode)
								n.upId = parentNode.id;
							n.isParent = n.isParent == '1';
						});
						return responseData;
					}
				},
				check : {
					enable : false
				},
				data : {
					key : {
						name : 'text'
					},
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId"
					}
				},
				view : {
					selectedMulti : false
				},
				callback : {
					beforeClick : function(treeId, treeNode, clickFlag) {
						return treeNode.id != '0';
					},
					onClick : function(event, treeId, treeNode) {
						if (treeNode.hasRpt) {
							$('.l-btn .icon-refresh2').parent().click();
							$('#folderId').val(treeNode.id);
							$('.l-btn .icon-search').parent().click();
						} else {
							if (window.grid && grid.currentData && grid.currentData.Rows) {
								grid.currentData.Rows = [];	// 先清除备份
								grid._clearGrid();			// 再清空展示区的 Grid
							}
						}
						app.zTreeObj.expandNode(treeNode, true);	// 点击节点名称也展开节点
					},
					onRemove : function(event, treeId, treeNode) {
						var node = treeNode.getParentNode();
						node.isParent = true;
						node.open = false;
						app.zTreeObj.updateNode(node, false);
					},
					onAsyncSuccess : function(event, treeId, treeNode, msg) {
						// app.zTreeObj.expandAll(true);
					},
					beforeAsync : function(treeId, treeNode) {
						if(treeNode)
						return treeNode.isParent;
					}
				}
			};
			// 根节点不再使用“文件夹”这个节点, 因此做了些调整 begin
			/*app.zTreeObj = $.fn.zTree.init($("#tree"), setting, { id : '0', text : '文件夹', upId : '0', isParent : true });*/
			$(app.folderInfo).each(function(i, n) {
				n.isParent = n.isParent == '1';
			});
			app.zTreeObj = $.fn.zTree.init($("#tree"), setting, app.folderInfo);
			/*app.zTreeObj.reAsyncChildNodes(app.zTreeObj.getNodeByParam('id', '0'), "add", false);*/
			// 根节点不再使用“文件夹”这个节点, 因此做了些调整 end
		},
		// 我的收藏、查询表单
		myQuery_addSearchForm: function() {
			$("#search").ligerForm({
				fields : [ {
					display : "查询名称", name : "queryNm", newline : true, type : "text", cssClass : "field",
					attr : {
						op : "like", field : "ins.queryNm"
					}
				}, {
					display : "创建时间", name : "createTime", newline : false, type : "date", cssClass : "field",
					options : {
						onChangeDate : function(value) {
							var vl = value.split('-');
							var dt = new Date(new Number(vl[0]), new Number(vl[1]) - 1, new Number(vl[2]) + 1);
							var year = dt.getFullYear(), mont = dt.getMonth(), date = dt.getDate();
							liger.get('createTime_0').setValue(year + '-' + (mont + 1) + '-' + date);
						}
					},
					attr : {
						vt : 'date', op : ">", field : "ins.createTime"
					}
				}, {
					display : "创建时间", name : "createTime_0", newline : false, type : "date", cssClass : "field",
					attr : {
						vt : 'date', op : "<=", field : "ins.createTime"
					}
				}, {
					display : "文件夹ID", name : "folderId", newline : true, type : "hidden", cssClass : "field", 
					attr : {
						op : "=", field : "rel.folder_id"
					}
				} ]
			});
			$('#createTime_0').closest('.l-fieldcontainer').hide();
			// 添加我的查询搜索按钮
			BIONE.addSearchButtons("#search", grid, "#searchbtn");
			$('.l-trigger-hover.l-trigger-cancel').live('click', function() {
				var id = $(this).siblings('input').attr('id');
				if (id == 'createTime') {
					$('#createTime_0').val('');
				}
			});
			$('.icon-refresh2').parent().click(function() {
				$('#createTime_0').val('');
			});
		},
		// 报表查询表单
		rptQuery_addSearchForm: function() {
			$("#search").ligerForm({
				fields : [ {
					display : "业务类型", name : "orgType", newline : false, type : "select", cssClass : "field", comboboxName : 'orgTypeBox',
					options : {
						valueFieldID : 'orgType',
						url : app.ctx + "/report/frame/datashow/idx/getIdxTaskType.json",
						onSelected : function(value) {
							//判断隐藏或显示年终结算
							var dataDate = $("#dataDate").val();
							if ((value == '02' || value == '03') && dataDate.indexOf("1231") != -1) {//1104、大集中并且时间点值是年底
								$('#settlementType').closest('.l-fieldcontainer').show();
							}else{
								$('#settlementType').closest('.l-fieldcontainer').hide();
							}
						}
					},
					attr : {
						op : "like", field : "module.moduleName"
					},
					validate : { required : true }
				}, {
					display : "选择机构", name : "orgNo", newline : false, type : "select", cssClass : "field", comboboxName : 'orgNm_sel',
					options : {
						onBeforeOpen : function() {
							if (BIONE.validator.element('#orgTypeBox')) {
								var orgType = $('#orgType').val();
								if(!orgType){
									orgType = "01";
								}
								BIONE.commonOpenDialog("机构树", "taskOrgWin", "450", "600", app.ctx + '/frs/integratedquery/rptquery/searchOrgSetInfo?&orgType='+orgType, null);
							}
							return false;
						},
						onSelected : function(value, text) {
							$('#orgNm_sel').blur();
						},
						cancelable:true
					},
					attr : {
						op : "=", field : "org"
					},
					validate : { required : true }
				}, {
					display : "查询方式", name : "checkType", newline : false, type : "select", cssClass : "field", comboboxName : 'checkTypeCombox',
					options : {
						valueFieldID : 'checkType',
						data : [ { text : '时间点值', id : '01' }, { text : '时间段值', id : '02' } ],
						onSelected : function(value, text) {
							if (value == '01') {
								$('#dataDate').closest('.l-fieldcontainer').show();
								$('#cycle').closest('.l-fieldcontainer').hide();
								$('#startDate').closest('.l-fieldcontainer').hide();
								$('#endDate').closest('.l-fieldcontainer').hide();
								$('#dataDate').removeAttr('disabled');
								liger.get('cycleBox').setDisabled();
								$('#cycle').attr('disabled', 'disabled');
								liger.get('startDate').setDisabled();
								liger.get('endDate').setDisabled();
								
								//显示年中结算
								if ($('#orgType').val() == '03' && $('#dataDate').val().indexOf("1231") != -1) {//大集中并且时间点值是年底
									$('#settlementType').closest('.l-fieldcontainer').show();
								}
							} else if (value == '02') {
								$('#dataDate').closest('.l-fieldcontainer').hide();
								$('#cycle').closest('.l-fieldcontainer').show();
								$('#startDate').closest('.l-fieldcontainer').show();
								$('#endDate').closest('.l-fieldcontainer').show();
								$('#dataDate').attr('disabled', 'disabled');
								liger.get('cycleBox').setEnabled();
								$('#cycle').removeAttr('disabled');
								liger.get('startDate').setEnabled();
								liger.get('endDate').setEnabled();
								$('#settlementType').closest('.l-fieldcontainer').hide();
							}
							$('#checkTypeCombox,#cycleBox').blur();
						}
					},
					attr : {
						op : "like", field : "module.moduleName"
					}
				}, {
					display : "查询频度", name : "cycle", newline : false, type : "select", cssClass : "field", comboboxName : 'cycleBox',
					options : {
						valueFieldID : 'cycle', initValue : '01',
						data : [ { text : '日', id : '01' }, { text : '旬', id : '02' }, { text : '月', id : '03' }, { text : '季度', id : '04' }, 
						         { text : '半年', id : '05' }, { text : '年', id : '06' }, { text : '周', id : '07' } ]
					},
					attr : {
						op : "=", field : "module.moduleNo"
					}
				}, {
					display : "时间点值", name : "dataDate", newline : false, type : "date", cssClass : "field",
					options : { 
						// cancelable : false,
						format: 'yyyyMMdd',
						onChangeDate : function(value, text) {
							var dataDateHideVal = $('#dataDateHide').val(), dateArr = [];
							if (dataDateHideVal) {
								dateArr = dataDateHideVal.split(',');
							}
							dateArr.push(value);
							var dateObj = {};
							$(dateArr).each(function(i, n) {
								dateObj[n] = '';
							});
							dateArr = [];
							for (var p in dateObj) {
								dateArr.push(p);
							}
							dataDateHideVal = dateArr.join(',');
							liger.get('dataDate').setValue(dataDateHideVal);
							$('#dataDateHide').val(dataDateHideVal);
							BIONE.validator.element('#dataDate');
							
							//判断隐藏或显示年终结算
							var orgType = $('#orgType').val();
							if ((orgType == '02' || orgType == '03') && dataDateHideVal.indexOf("1231") != -1) {//1104、大集中并且时间点值是年底
								$('#settlementType').closest('.l-fieldcontainer').show();
							}else{
								$('#settlementType').closest('.l-fieldcontainer').hide();
							}
							
							
						}
					},
					attr : {
						op : "like", field : "module.moduleName"
					},
					validate : { required : true }
				}, {
					display : "开始日期", name : "startDate", newline : false, type : "date", cssClass : "field",
					options : {
						format: 'yyyyMMdd',
						onChangeDate : function(value, text) {
							BIONE.validator.element('#startDate');
						}
					},
					attr : {
						vt : 'date', op : "=", field : "module.moduleNo"
					},
					validate : { required : true }
				}, {
					display : "结束日期", name : "endDate", newline : false, type : "date", cssClass : "field",
					options : {
						format: 'yyyyMMdd',
						onChangeDate : function(value, text) {
							BIONE.validator.element('#endDate');
						}
					},
					attr : {
						vt : 'date', op : "like", field : "module.moduleName"
					},
					validate : { required : true }
				}, {
					display : "展示类型", name : "showType", newline : false, type : "select", cssClass : "field", comboboxName : 'showTypeBox',
					options : {
						valueFieldID : 'showType', initValue : '01',
						data : [ { text : '网页展示', id : '01' }, { text : 'EXCEL', id : '02' }, { text : 'ZIP', id : '03' } ]
					},
					attr : {
						op : "like", field : "module.moduleName"
					}
				},{
					display : "年终结算", name : "settlementType", newline : false, type : "select", cssClass : "field", comboboxName : 'settlementTypeBox',
					options : {
						valueFieldID : 'settlementType', initValue : '01',
						data : [ { text : '年结前数据', id : '01' }, { text : '年结后数据', id : '02' } ]
					},
					validate : { required : true }
				}, {
					display : '数据日期', name : 'dataDateHide', newline: false, type : 'hidden', cssClass : 'field',
					attr : {
						op : "like", field : "module.moduleName", disabled : 'disabled'
					}
				},{
					display : "数据类型", name : "archiveType", newline : false, type : "select", cssClass : "field", comboboxName : 'archiveTypeBox',
					options : {
						valueFieldID : 'archiveType', initValue : '04',
						data : [{ text : '初始数据(元)', id : '05' },{ text : '初始数据', id : '03' }, { text : '填报数据', id : '04' }, { text : '归档数据', id : '01' }, { text : '回灌数据', id : '02' } ]
					},
					validate : { required : true }
				}/*, {
					display : '统计类型', name : 'orgType', newline: false, type : 'hidden', cssClass : 'field',
					attr : {
						op : "like", field : "module.moduleName"
					}
				}*/ ]
			});
			app.rptQuery_addSearchButtons("#search", "#searchbtn");
			//modify--给表单增加气泡信息
			var targets = "li[fieldindex=4]";
			$(targets).live("mouseover",function(){
				$(targets).ligerTip({content:"时间框有日期，点击页面有数据"})
			});
			$(targets).live("mouseout",function(){
				$(targets).ligerHideTip();
			});
			//end
			
			// liger.get('checkTypeCombox').setValue('01');
			// 由于 IE7 在页面初始化时无法触发上面这种写法的联动, 为下面这种写法 begin
			$('#dataDate').closest('.l-fieldcontainer').show();
			$('#cycle').closest('.l-fieldcontainer').hide();
			$('#startDate').closest('.l-fieldcontainer').hide();
			$('#endDate').closest('.l-fieldcontainer').hide();
			$('#dataDate').removeAttr('disabled');
			liger.get('cycleBox').setDisabled();
			$('#cycle').attr('disabled', 'disabled');
			liger.get('startDate').setDisabled();
			liger.get('endDate').setDisabled();
			// end
			liger.get('checkTypeCombox').setValue('01');
			$('#settlementType').closest('.l-fieldcontainer').hide();

			$(':input').blur();
			jQuery.metadata.setType("attr", "validate");
			BIONE.validate('#formsearch');
			$('.l-trigger-hover.l-trigger-cancel').live('click', function() {
				var id = $(this).siblings('input').attr('id');
				if (id == 'dataDate') {
					$('#dataDateHide').val('');
				}
			});
		},
		// 我的查询 grid 表格
		myQuery_initGrid : function() {
			var data = {
				Rows : [
					{
						queryNm : '我的查询', cureateUser : '许广源', createTime : '1407845587504', remark : '我的查询测试数据, 勿删, 谢谢合作！'
					},
				],
				Total : 1
			};
			grid = $("#maingrid").ligerGrid({
				columns : [
					{
						display : '查询名称', name : 'queryNm', align : 'center', minWidth : '', width : '',
						render : function(rowdata, index, value, column) {
							return '<a name="' + column.name + '" class="queryName" rowid="' + rowdata.__id + 
									'" onclick= "onQueryName(\''+ rowdata.__id+'\')"><span class="query">' + value + '</span></a>';
						}
					}, {
						display : '创建用户', name : 'createUser', align : 'center', minWidth : '', width : '', 
						render : function(rowdata) {
							var arr = rowdata.createUser.split('_');
							return arr[1]; 
						}
					}, {
						display : '创建时间', name : 'createTime', align : 'center', minWidth : '', width : '', type : 'date', format : 'yyyy-MM-dd'
					}, {
						display : '备注', name : 'remark', align : 'center', minWidth : '', width : ''
					} ],
				checkbox : true,
				usePager : true,
				isScroll : false,
				rownumbers : true,
				alternatingRow : true, /* 附加奇偶行效果行 */
				colDraggable : true,	/* 是否允许表头拖拽 */
				dataAction : 'local',	/* 从后台获取数据 */
				method : 'post',
				url : app.ctx + '/frs/integratedquery/myrpt/list.json',
				sortName : 'createTime', //第一次默认排序的字段
				sortOrder : 'desc',
				delayLoad : true,
				height : '100%',
				width : '100%',
				toolbar : {}
			});
			onQueryName = function(rowid){
				var $this = $(this), name = $this.attr('name');
				var row = grid.getRow(rowid), instId = row.instanceId, instType = row.instanceType;
				var width = $(window).width() + 5, height = $(window).height() - 3;
				var title = (instType == '01') ? '我的报表' : (instType == '03' ? '我的指标' : '');
				if(instType == '01'){
					BIONE.commonOpenDialog(title, "RptDialog", width, height, app.ctx + '/frs/integratedquery/myrpt/rptQuery?instId=' + instId + '&instType=' + instType);
				}else if(instType == '03'){
					var modelDialogUri = app.ctx +"/report/frame/datashow/idx?storeId="+instId+"&mode="+'1';
					BIONE.commonOpenDialog("指标查询", "alertRptIndex",width, height, modelDialogUri);
				}
				
			}
			BIONE.loadToolbar(grid, [ {
				text : '查询', icon : 'fa-search', operNo : 'query', click : function() {
					var rows = grid.getSelectedRows();
					if (rows.length == 1) {
						var data = rows[0];
						var instId = data.instanceId, instType = data.instanceType;
						var width = $(window).width() + 5, height = $(window).height() - 3;
						var title = (instType == '01') ? '我的报表' : (instType == '02' ? '我的指标' : '');
						BIONE.commonOpenDialog(title, "RptDialog", width, height, app.ctx + '/frs/integratedquery/myrpt/rptQuery?instId=' + instId + '&instType=' + instType);
					} else {
						top.BIONE.tip('请选择一条记录');
					}
				}
			}, {
				text : '删除', icon : 'fa-trash-o', operNo : 'deleteMyQuery', click : function() {
					var rows = grid.getSelectedRows();
					if (rows.length > 0) {
						$.ligerDialog.confirm('确实要删除这些记录吗?', function(yes) {
							if(yes) {
								var ids = [rows[0].instanceType, $(rows).map(function(i, n) {
									return n.instanceId;
								}).toArray().join()].join();
								BIONE.ajax({
									type : 'post',
									url : app.ctx + '/frs/integratedquery/myrpt/destroy?ids=' + ids
								}, function(result) {
									top.BIONE.tip('删除成功');
									grid.loadData();
								});
							}
						});
					} else {
						top.BIONE.tip('请选择记录');
					}
				}
			}]);
		},
		// 从我的查询跳转到报表查询/报表指标查询时对查询表单灌入数据
		injectQueryData : function (data) {
			// var data = app.queryObj;
			// 对表单灌入数据
			for ( var p in data) {
				var ele = $("[name=" + p + "]");
				if (ele.is(":text") && ele.attr("ltype") == "date") {
					if (data[p]) {
						var date = null;
						if (data[p].time){
							date = new Date(data[p].time);
						} else {
							if (typeof data[p] == 'string' && data[p].indexOf('-') > -1 && data[p].length >= 10){
								date = new Date(new Number(data[p].substr(0,4)),new Number(data[p].substr(5,2))-1,new Number(data[p].substr(8,2)));
					    	} else {
					    		date = new Date(data[p]);
					    	}
						}
						var yy = date.getFullYear();
						var Mm = ((date.Mm = date.getMonth() + 1) < 10) ? ('0' + date.Mm) : date.Mm;
						var dd = (date.getDate() < 10) ? ('0' + date.getDate()) : date.getDate();
						ele.val(yy + '-' + Mm + '-' + dd);
					}
				} else {
					ele.val(data[p]);
				}
			}
			// 加载报表树节点选中状态
			var zTreeObj = app.zTreeObj;
			$(app.queryObj.querysRel).each(function(i, n){
				zTreeObj.selectNode(zTreeObj.getNodeByParam('id', n.id.rptId), true);
			});
			// 下面是更新表单的样式
			var managers = $.ligerui.find($.ligerui.controls.Input);
			for ( var i = 0, l = managers.length; i < l; i++) {
				// 改变了表单的值，需要调用这个方法来更新ligerui样式
				var o = managers[i];
				o.updateStyle();
				if (managers[i] instanceof $.ligerui.controls.TextBox)
					o.checkValue();
			}
		},
		// 我的查询_删除文件夹
		myQuery_delFolder : function(folderId, together) {
			BIONE.ajax({
				data : { folderId : folderId, together : together },
				url : app.ctx + '/frs/integratedquery/myrpt/deleteFolder'
			}, function(result) {
				$('.l-btn .l-icon-refresh2').parent().click();
				grid.currentData && (grid.currentData.Rows = []);	// 先清除备份
				grid._clearGrid();			// 再清空展示区的 Grid
				top.BIONE.tip('删除成功');
			});
		},
		// 我的查询_treeToolbar
		myQuery_treeToolbar : function() {
			$("#treeToolbar").ligerTreeToolBar({
				items : [ {
					icon : 'add', 
					text : '新增', 
					click : function() {
						var nodes = app.zTreeObj.getSelectedNodes();
						if (nodes.length > 0 && nodes[0].isParent == '1') {
							app.folderId = nodes[0].id;
							BIONE.commonOpenDialog('新建文件夹', 'folderDialog', 420, 160,  app.ctx + '/frs/integratedquery/myrpt/newFolder');
						} else {
							top.BIONE.tip('请先选择父目录');
						}
					}
				}, {
					icon : 'modify', 
					text : '修改', 
					click : function() {
						var nodes = app.zTreeObj.getSelectedNodes();
						if (nodes.length > 0 && nodes[0].isParent == '1') {
							app.folderId = nodes[0].id;
							BIONE.commonOpenDialog('修改文件夹', 'folderDialog', 420, 160,  app.ctx + '/frs/integratedquery/myrpt/newFolder?folderId=' + app.folderId);
						} else {
							top.BIONE.tip('请先选择父目录');
						}
					}
				}, {
					icon : 'delete', 
					text : '删除', 
					click : function() {
						var nodes = app.zTreeObj.getSelectedNodes();
						if (nodes.length > 0 && nodes[0].isParent == '1') {
							if (nodes[0].upId == null) {
								top.BIONE.tip('该目录不可删除!');
							} else {
								var folderId = nodes[0].id;
								BIONE.ajax({
									data : { folderId : folderId },
									url : app.ctx + '/frs/integratedquery/myrpt/searchIsEmptyFolder.json'
								}, function(result) {
									if (result) {
										$.ligerDialog.confirm('确定删除该目录吗?', function(yes) {
											if (yes) {
												app.myQuery_delFolder(folderId);
												app.zTreeObj.removeNode(nodes[0], true);
											}
										});
									} else {
										$.ligerDialog.confirm('目录非空, 要整体删除吗?', function(yes) {
											if (yes) {
												app.myQuery_delFolder(folderId, true);
												app.zTreeObj.removeNode(nodes[0], true);
											}
										});
									}
								});
							}
						}
					}
				} ]
			});
		},
		// 我的查询_事件支持
		myQuery_addDomEvent : function () {
			// 目录树, “放大镜”查询按钮点击事件
			$('#treesearchbtn').click(function() {
				var zTreeObj = app.zTreeObj, text = $('#treesearchtext').val(), data = {};
				if (text) {
					data.text = text;
				} else {
					data.id = '0';
				}
				$(zTreeObj.getNodes()).each(function(i, n) {
					zTreeObj.removeNode(n, false);
				});
				BIONE.ajax({
					data : data,
					url : app.ctx + '/frs/integratedquery/myrpt/searchFolderForTree.json?d='+new Date().getTime()
				}, function(result) {
					$(result).each(function(i, n) {
						n.isParent = n.isParent == '1';
						n.upId = '0';
					});
					//result.push({ id : '0', text : '文件夹', upId : '0', isParent : true, open : true });
					zTreeObj.addNodes(null, result, true);
				});
			});
			// 目录树搜索框回车事件
			$('#treesearchtext').keydown(function(ev) {
				if (ev.keyCode && (ev.keyCode == 13)) {
					$('#treesearchbtn').click();
				}
			});
		},
		// 报表树，点击'搜索' 或 回车 时执行查询的事件
		addRptQueryEvent : function (len) {
			$('#treeSearchIcon').click(function() {
				app.treeSearchIconClick("01");
				if(len.contains(2)){
					app.treeSearchIconClick("02");
				}
				if(len.contains(3)){
					app.treeSearchIconClick("03");
				}
			});
			$('#treesearchtext').keydown(function(ev) {
				if (ev.keyCode && (ev.keyCode == 13)) {
					$('#treeSearchIcon').click();
				}
			});
		},
		treeSearchIconClick:function(treeType){
			var zTreeObj =null;
			if(treeType=="02"){//本机构
				zTreeObj = app.zTreeObj_org;
			}else if(treeType=="03"){//本人
				zTreeObj = app.zTreeObj_user;
			}else{//全行通用
				zTreeObj = app.zTreeObj;
			}
			$(zTreeObj.getNodes()).each(function(i, n) {
				zTreeObj.removeNode(n, false);
			});
			BIONE.ajax({
				type : 'post',
				data : { text : text = $('#treesearchtext').val(),defSrc: treeType},
				url : app.ctx + '/frs/integratedquery/rptquery/searchRptForTree.json'
			}, function(result) {
				$(result).each(function(i, n) {
					if (n.nodeType == '1') {
						n.icon = app.iconFolder;
					} else if (n.nodeType == '2') {
						n.icon = app.iconRpt;
					} else if (n.nodeType == '3') {
						n.icon = app.iconLine;
					}
					n.open = true;
				});
				result.unshift({ id : '0', text : '报表树', upId : '0', isParent : true, open : true });
				zTreeObj.addNodes(null, result);
			});
		},
		// 收藏名称 Form
		initFavNmForm : function() {
			var width = $(window).width() - 178;
			$('#search').ligerForm({
				fields : [ {
					display : "查询名称", name : "queryNm", newline : true, type : "text", cssClass : "field", width: width,
					validate : { required : true },
					attr : {
						op : "like", field : "ins.queryNm"
					}
				}, {
					display : '文件夹ID', name : 'folderId', newline: false, type : 'hidden', cssClass : 'field',
					attr : {
						op : "like", field : "module.moduleName"
					}
				}, {
					display : '上级文件夹ID', name : 'upFolderId', newline: false, type : 'hidden', cssClass : 'field',
					attr : {
						op : "like", field : "module.moduleName"
					}
				} ]
			});
			if (app.folderInfo && typeof app.folderInfo == 'object') {
				$('#folderId').val(app.folderInfo.folderId);
				$('#queryNm').val(app.folderInfo.folderNm);
				$('#upFolderId').val(app.folderInfo.upFolderId);
			} else {
				$('#upFolderId').val(parent.app.folderId);
			}
			jQuery.metadata.setType("attr", "validate");
			BIONE.validate($('#formsearch'));
		},
		// 添加收藏查询按钮
		addFavQueryButton : function () {
			BIONE.createButton({
				appendTo : "#searchbtn",
				text : '收藏查询',
				icon : 'import',
				width : '75px',
				click : function() {
					var checkType = liger.get('checkTypeCombox').getValue();
					if (BIONE.validator.element('#orgTypeBox') & BIONE.validator.element('#orgNm_sel') & ((checkType == '01' & BIONE.validator.element('#dataDate'))
							|| (checkType == '02' & BIONE.validator.element('#startDate') & BIONE.validator.element('#endDate')))) {
						if (grid && grid.getData().length == 0) {
							top.BIONE.tip('请选择指标');
							return false;
						}
						var zTreeObj = app.zTreeObj;
						if (app.nodeType == '2') {
							var nodes = $(zTreeObj.getCheckedNodes(true)).map(function(i, n) {
								if (n.nodeType == '2') return n;	// nodeType: 1.目录; 2.报表; 3.条线
							});
						} else {
							var nodes = $(zTreeObj.getSelectedNodes()).map(function(i, n) {
								if (n.nodeType == '2') return n;	// nodeType: 1.目录; 2.报表; 3.条线
							});
						}
						if (nodes && nodes.length > 0) {
							var height = $(window).height() - 100;
							BIONE.commonOpenDialog('添加收藏', 'favDialog', 480, height,  app.ctx + '/frs/integratedquery/myrpt/favFolder');
						} else {
							top.BIONE.tip('请选择报表!');
						}
					}
				}
			});
		},
		// 添加全屏查看按钮
		addFullScreenButton : function (nodes) {
			BIONE.createButton({
				appendTo : "#searchbtn",
				text : '全屏查看',
				icon : 'guide_2',
				width : '85px',
				click : function() {
					var checkType = liger.get('checkTypeCombox').getValue();
					if (BIONE.validator.element('#orgTypeBox') && BIONE.validator.element('#orgNm_sel') &&
							((checkType == '01' && BIONE.validator.element('#dataDate')) || 
							 (checkType == '02' & BIONE.validator.element('#startDate') && BIONE.validator.element('#endDate')))) {
						var showType = liger.get('showTypeBox').getValue();
						var nodes = new Array();
						if(datajson){
							datajson.text = datajson.rptNm;
							datajson.id = datajson.rptId;
							datajson.nodeType = '2';
							nodes.push(datajson); 
						}else{
							nodes = app.zTreeObj.getCheckedNodes(true);
						}
						if (showType == '01') {
							// 封装查询参数
							var argsArr = app.rptQuery_readExportParms(nodes);
							if (argsArr.length < 31) {
								// 封装查询参数
								var parms = app.rptQuery_readQueryParms(nodes);
								var url = app.ctx + '/frs/integratedquery/rptquery/fullScreen?parms=' + JSON2.stringify(JSON2.stringify(parms));
								//var url = app.ctx + '/frs/integratedquery/rptquery/fullScreen';
								window.open(url, '_blank', 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, copyhistory=yes, channelmode=yes, fullscreen=no');
							}else{
								BIONE.tip('最终文档将超过 30个标签页(超过了Excel的限制), 请适当减少条件');
							}	
						} else {
							parent.BIONE.tip('仅限于[网页展示]');
						}
					}
				}
			});
		},
		// 添加全屏查看按钮
		addUploadButton : function () {
			BIONE.createButton({
				appendTo : "#searchbtn",
				text : '数据导入',
				icon : 'import',
				width : '85px',
				click : function() {
					BIONE.commonOpenDialog('报表数据导入','uploadWin', 600, 378,app.ctx + '/frs/integratedquery/rptquery/rptUploadData');
				}
			});
		},
		// 添加保存按钮
		addSaveFavButton : function() {
			BIONE.addFormButtons([{
				text : '保存', onclick : function () {
					var zTreeObj = app.zTreeObj, nodes = zTreeObj.getSelectedNodes();
					if ($('#formsearch').valid()) {
						if (nodes.length > 0) {
							parent.app.saveMyQuery($('#queryNm').val(), nodes[0].id);
							var main = parent || window;
							var dialog = main.jQuery.ligerui.get('favDialog');
							dialog.close();
						} else {
							top.BIONE.tip('请选择文件夹');
						}
					}
				}
			}]);
		},
		// 添加文件夹保存按钮
		addSaveFolderButton : function() {
			BIONE.addFormButtons([
				{ text : "取消", onclick : function() { BIONE.closeDialog("folderDialog"); } },	
				{ text : "保存", onclick : function() {
						if ($('#formsearch').valid()) {
							BIONE.ajax({
								data : {
									folderId : $('#folderId').val() || app.uuid2(),
									upFolderId : $('#upFolderId').val(),
									folderNm : $('#queryNm').val()
								},
								type : 'post',
								url : app.ctx + '/frs/integratedquery/myrpt/saveFolderInfo'
							}, function(data) {
								if(!data.folderId){
									BIONE.tip("文件夹名称重复，请重新命名");
								}
								var zTreeObj = parent.app.zTreeObj;
								var node = zTreeObj.getNodeByParam('id', data.folderId);
								if (node) {
									node.text = data.folderNm;
									zTreeObj.updateNode(node, false);
								} else {
									var node = zTreeObj.getNodeByParam('id', data.upFolderId);
									zTreeObj.addNodes(node, { id: data.folderId, text: data.folderNm, upId: data.upFolderId, isParent : true});
								}
								BIONE.closeDialog("folderDialog");
							});
						}
					}
				}
			]);
		},
		// 新建文件夹时 热键支持
		folder_addHotKey : function() {
			$('#queryNm').keydown(function(ev) {
				if (ev.keyCode && (ev.keyCode == 13)) {
					$('.form-bar .l-dialog-btn-inner').each(function(i, n) {
						if ($(this).text() == '保存') {
							$(this).closest('.l-dialog-btn').click();
						}					
					});
				}
			});
		},
		// 新建收藏文件夹
		newFolder : function() {
			var nodes = app.zTreeObj.getSelectedNodes();
			if (nodes.length > 0) {
				app.folderId = nodes[0].id;
				BIONE.commonOpenDialog('新建文件夹', 'folderDialog', 420, 160,  app.ctx + '/frs/integratedquery/myrpt/newFolder');
			} else {
				top.BIONE.tip('请先选择父目录');
			}
		},
		// 生成不含 '-' 分隔符的 uuid
		uuid2 : function() {
			return uuid.v1().replace(/-/g, '');
		},
		// 保存我的查询
		saveMyQuery : function(queryNm, folderId) {
			var uuid2 = app.uuid2;
			if (app.nodeType == '2') {	// nodeType: 1.目录; 2.报表; 3.条线
				var zTreeObj = app.zTreeObj, nodes = zTreeObj.getCheckedNodes(true);
				var instId = uuid2(), queryRelArr = [], querysItemInfo = [], queryInstInfo = {}, folderInstRelInfo = {};
				$(nodes).each(function(i, n) {
					if (n.nodeType == '2') {	// nodeType: 1.目录; 2.报表; 3.条线
						// 报表批量查询与报表关系信息
						queryRelArr.push({
							id : {
								rptId : n.id, instanceId : instId
							}
						});
					}
				});
				// 报表批量查询项信息
				$(":input", $('#search')).not(":submit, :reset, :image, :button, [disabled]").each(function() {
					if (!this.name || !$(this).val() || !$(this).attr('field')) return;
					var ltype = $(this).attr("ltype"), optionsJSON = $(this).attr("ligerui"), options = "";
					if (optionsJSON) {
						options = JSON2.parse(optionsJSON);
					}
					var name = this.name, value = $(this).val();
					// 如果是下拉框，那么读取下拉框关联的隐藏控件的值(ID值,常用与外表关联)
					if (ltype == "select" && options && options.valueFieldID) {
						value = $("#" + options.valueFieldID).val();
					}
					querysItemInfo.push({
						queryId : uuid2(), paramType : name, paramValExpression : value, instanceId : instId
					});
				});
				// 查询实例信息
				$.extend(queryInstInfo, { instanceId : instId, queryNm : queryNm, remark : '' });
				// 我的文件夹与实例关系
				$.extend(folderInstRelInfo, {
					id : {
						instanceId : instId, folderId : folderId
					},
					instanceType : '01'	// 报表批量查询（我的报表）
				});
				var data = { querysRel : queryRelArr, querysItem : querysItemInfo, queryins : queryInstInfo, folderInstRel : folderInstRelInfo }
				$.ajax({
					cache : false,
					async : true,
					type : 'post',
					data : { 
						"data":JSON2.stringify(data) 
					},//JSON2.stringify(data),
					dataType : 'json',
					url : app.ctx + '/frs/integratedquery/myrpt/saveFavQuerys',
					success : function(result) {
						top.BIONE.tip('收藏成功');
					}
				});
			} else if (app.nodeType == '3') {	// nodeType: 1.目录; 2.报表; 3.条线
				var instId = uuid2(), rptidxItem = [], rptidxRel = [], folderInstRelInfo = {}, queryInstInfo = {}; 
				// 报表指标查询项信息
				$(":input", $('#search')).not(":submit, :reset, :image, :button, [disabled]").each(function() {
					if (!this.name || !$(this).val() || !$(this).attr('field')) return;
					var ltype = $(this).attr("ltype"), optionsJSON = $(this).attr("ligerui"), options = "";
					if (optionsJSON) {
						options = JSON2.parse(optionsJSON);
					}
					var name = this.name, value = $(this).val();
					// 如果是下拉框，那么读取下拉框关联的隐藏控件的值(ID值,常用与外表关联)
					if (ltype == "select" && options && options.valueFieldID) {
						value = $("#" + options.valueFieldID).val();
					}
					rptidxItem.push({
						queryId : uuid2(), paramType : name, paramValExpression : value, instanceId : instId
					});
				});
				// 报表指标查询与指标关系
				$(grid.getData()).each(function(i, n) {
					rptidxRel.push({
						queryId : uuid2(), rptId : n.rptId, indexNo : n.realIndexNo, rptNm : n.rptNm, indexNm : n.cellNo, instanceId : instId
					});
				});
				// 查询实例信息
				$.extend(queryInstInfo, { instanceId : instId, queryNm : queryNm, remark : '' });
				// 我的文件夹与实例关系
				$.extend(folderInstRelInfo, {
					id : {
						instanceId : instId, folderId : folderId
					},
					instanceType : '02'	// 报表批量查询（我的指标）
				});
				var data = { rptidxRel : rptidxRel, rptidxItem : rptidxItem, queryins : queryInstInfo, folderInstRel : folderInstRelInfo }
				$.ajax({
					type : 'post',
					data : { 
						"data":JSON2.stringify(data) 
					},
					/*data : JSON2.stringify(data),*/
					/*contentType : 'application/json',*/
					dataType : 'json',
					url : app.ctx + '/frs/integratedquery/myrpt/saveFavQuerys',
					success : function(result) {
						top.BIONE.tip('收藏成功');
					}
				});
			}
		},
		// 覆盖报表树当前的 callback
		rptIdx_cover_rptAsyncTree_callback : function(treeType) {
			var treeObj = null;
			if (treeType == "02") {// 本机构
				treeObj = app.zTreeObj_org;
			} else if (treeType == "03") {// 本人
				treeObj = app.zTreeObj_user;
			} else {// 全行通用
				treeObj = app.zTreeObj;
			}
			$.extend(treeObj.setting.callback, {
				beforeClick : function(treeId, treeNode, clickFlag) {
					treeObj.expandNode(treeNode);	// 点击节点名称也展开节点
					return treeNode.nodeType == '2';	// nodeType: 1.目录; 2.报表; 3.条线
				},
				onClick : function(event, treeId, treeNode) {
					app.rptIdx_onClickRptNode(treeNode, treeId, event);
				}
			});
		},
		// 报表指标树, 点击报表节点事件
		rptIdx_onClickRptNode : function(treeNode, treeId, event) {
			if (treeNode.nodeType == '2') {		// nodeType: 1.目录; 2.报表; 3.条线
				liger.get('orgTypeBox').setValue(treeNode.busiType);
				$('#orgTypeBox').blur();
				grid.currentData && (grid.currentData.Rows = []);	// 先清除备份
				grid._clearGrid();			// 如果是报表指标查询则清空指标展示区的 Grid
				app.rptId = treeNode.id;	// 报表ID
				app.rptNm = treeNode.text;	// 报表名称
				app.rptNum = treeNode.rptNum;	// 报表编码
				app.busiType = treeNode.busiType;	// 业务类型
				$('#spread').empty().show();
				require.config({
					baseUrl : app.ctx + '/plugin/js/',
					paths: {
						design : 'cfg/views/rptdesign'
					}
				});
				require(["design"] , function(design) {
					var settings = {
						targetHeight : ($("#content").height() - 2),
						ctx : app.ctx,
						readOnly : true,
						cellDetail: false,
						showType: 'cellnm',
						onEnterCell: function(sender, args, rptIdxTmp) {
						//	if (rptIdxTmp.cellType && (rptIdxTmp.cellType == '03'/* || rptIdxTmp.cellType == '05'*/)) {
							if (rptIdxTmp.realIndexNo) {
								var has = $.map(grid.getData(), function(n) {
									if (rptIdxTmp.realIndexNo == n.realIndexNo) {
										return '1';
									}
								});
								(has.length == 0) && grid.addRow($.extend({}, rptIdxTmp, { rptId : app.rptId, rptNm: app.rptNm }));
							}
						},
						initFromAjax : true,
						ajaxData : {
							templateId : treeNode.cfgId
						}
					};
					initSlcanvas(app.ctx);
					try {
						spread = design.init($("#spread") , settings);
					} catch (e) {
						
					}
				});
			}
		},
		// 报表指标查询表单
		rptIdx_addSearchForm : function() {
			$("#search").ligerForm({
				fields : [ {
					display : "统计类型", name : "orgType", newline : true, type : "select", cssClass : "field", comboboxName : 'orgTypeBox',
					options : {
						valueFieldID : 'orgType',
						/*data : [{ id : '02', text : '1104' }, { id : '03', text : '人行大集中'}],*/
						data : [{ id : '02', text : '非现场监管统计' }, { id : '03', text : '金融统计'},{ id : '01', text : '其他类型'}],
						onSelected : function(value, text) {
							
						}
					},
					attr : {
						op : "like", field : "module.moduleName"
					},
					validate : { required : true }
				}, {
					display : "选择机构", name : "orgNo", newline : true, type : "select", cssClass : "field", comboboxName : 'orgNm_sel', 
					options : {
						onBeforeOpen : function() {
							if (BIONE.validator.element('#orgTypeBox')) {
								BIONE.commonOpenDialog("机构树", "taskOrgWin", "750", "500", app.ctx + '/frs/integratedquery/rptquery/orgTree', null);
							}
							return false;
						},
						onSelected : function(value, text) {
							$('#orgNm_sel').blur();
						}
					},
					attr : {
						op : "=", field : "module.moduleNo"
					},
					validate : { required : true }
				}, {
					display : "查询方式", name : "checkType", newline : true, type : "select", cssClass : "field", comboboxName : 'checkTypeCombox',
					options : {
						valueFieldID : 'checkType',
						data : [ { text : '时间点值', id : '01' }, { text : '时间段值', id : '02' } ],
						onSelected : function(value, text) {
							if (value == '01') {
								$('#dataDate').closest('.l-fieldcontainer').show();
								$('#cycle').closest('.l-fieldcontainer').hide();
								$('#startDate').closest('.l-fieldcontainer').hide();
								$('#endDate').closest('.l-fieldcontainer').hide();
								$('#dataDate').removeAttr('disabled');
								liger.get('cycleBox').setDisabled();
								liger.get('startDate').setDisabled();
								liger.get('endDate').setDisabled();
							} else if (value == '02') {
								$('#dataDate').closest('.l-fieldcontainer').hide();
								$('#cycle').closest('.l-fieldcontainer').show();
								$('#startDate').closest('.l-fieldcontainer').show();
								$('#endDate').closest('.l-fieldcontainer').show();
								$('#dataDate').attr('disabled', 'disabled');
								liger.get('cycleBox').setEnabled();
								liger.get('startDate').setEnabled();
								liger.get('endDate').setEnabled();
							}
							$(':input').blur();
						}
					},
					attr : {
						op : "like", field : "module.moduleName"
					}
				}, {
					display : "频度", name : "cycle", newline : false, type : "select", cssClass : "field", comboboxName : 'cycleBox', width : '48',
					options : {
						valueFieldID : 'cycle', initValue : '05',
						data : [ { text : '日', id : '01' }, { text : '旬', id : '02' }, { text : '月', id : '03' }, { text : '季度', id : '04' }, 
						         { text : '半年', id : '05' }, { text : '年', id : '06' }, { text : '周', id : '07' } ]
					},
					attr : {
						op : "=", field : "module.moduleNo"
					}
				}, {
					display : "开始日期", name : "startDate", newline : true, type : "date", cssClass : "field",
					options : { 
						format: 'yyyyMMdd',
						onChangeDate : function(value, text) {
							BIONE.validator.element('#startDate');
						}
					},
					attr : {
						vt : 'date', op : "=", field : "module.moduleNo"
					},
					validate : { required : true }
				}, {
					display : "结束日期", name : "endDate", newline : true, type : "date", cssClass : "field",
					options : { 
						format: 'yyyyMMdd',
						onChangeDate : function(value, text) {
							BIONE.validator.element('#endDate');
						}
					},
					attr : {
						vt : 'date', op : "like", field : "module.moduleName"
					},
					validate : { required : true }
				}, {
					display : "时间点值", name : "dataDate", newline : false, type : "date", cssClass : "field",
					options : { 
						format: 'yyyyMMdd',
						onChangeDate : function(value, text) {
							var dataDateHideVal = $('#dataDateHide').val(), dateArr = [];
							if (dataDateHideVal) {
								dateArr = dataDateHideVal.split(',');
							}
							dateArr.push(value);
							var dateObj = {};
							$(dateArr).each(function(i, n) {
								dateObj[n] = '';
							});
							dateArr = [];
							for (var p in dateObj) {
								dateArr.push(p);
							}
							dataDateHideVal = dateArr.join(',');
							liger.get('dataDate').setValue(dataDateHideVal);
							$('#dataDateHide').val(dataDateHideVal);
							BIONE.validator.element('#dataDate');
						}
					},
					attr : {
						op : "like", field : "module.moduleName"
					},
					validate : { required : true }
				}, {
					display : '数据日期', name : 'dataDateHide', newline: false, type : 'hidden', cssClass : 'field',
					attr : {
						op : "like", field : "module.moduleName", disabled : 'disabled'
					}
				} ],
				space : '22',
				labelWidth : '75',
				inputWidth : '160'
			});
			app.addSearchButtons("#search", "#searchbtn");
			 liger.get('checkTypeCombox').setValue('01');
			// 由于 IE7 在页面初始化时无法触发上面这种写法的联动, 因此改为下面这种写法 begin
			$('#cycle').parent().parent().prev().css('width', '45px'); 
			$('#dataDate').closest('.l-fieldcontainer').show();
			$('#cycle').closest('.l-fieldcontainer').hide();
			$('#startDate').closest('.l-fieldcontainer').hide();
			$('#endDate').closest('.l-fieldcontainer').hide();
			$('#dataDate').removeAttr('disabled');
			liger.get('cycleBox').setDisabled();
			liger.get('startDate').setDisabled();
			liger.get('endDate').setDisabled();
			// end
			liger.get('checkTypeCombox').setValue('01');
			$('div.searchtitle span').text('报表指标查询');
			jQuery.metadata.setType("attr", "validate");
			BIONE.validate('#formsearch');
		},
		// 报表指标查询, 指标维度
		rptIdx_addGrid : function() {
			var data = {
				Rows : [
					{
						rptNm : '我的查询', indexNm : '许广源', indexNo : '1407845587504', remark : '我的查询测试数据, 勿删, 谢谢合作！'
					}
				],
				Total : 1
			};
			grid = $("#maingrid").ligerGrid({
				columns : [
					{
						display : '报表名称', name : 'rptNm', align : 'center', minWidth : '60', width : '27%'
					}, {
						display : '单元格信息', name : 'cellNo', align : 'center', minWidth : '60', width : '7%'
					}, {
						display : '指标信息', name : 'cellNm', align : 'center', minWidth : '60', width : '7%',
						render:function(a,b,c){ 
							if(c==null||c=="") 
								return a.cellNo 
							else 
								return c
						}
					}, {
						display : '操作', name : 'oper', align : 'center', minWidth : '60', width : '7%', 
						render : function(rowdata, index, value, column) {
							return '<a name="' + column.name + '" class="oper" rowid="' + rowdata.__id + '" onclick="var $this = $(this),' + 
								' name = $this.attr(\'name\'), rowid = $this.attr(\'rowid\');grid.remove(grid.getRow(rowid));">删除</a>';
						}
					} ], 
				checkbox : false,
				usePager : false,
				isScroll : false,
				rownumbers : false,
				alternatingRow : true, /* 附加奇偶行效果行 */
				colDraggable : true,	/* 是否允许表头拖拽 */
				dataAction : 'local',	/* 从后台获取数据 */
				// method : 'post',
				// data : data,
				// url : app.ctx + '/rpt/frs/MyRpt/list.json',
				sortName : 'indexNo', //第一次默认排序的字段
				sortOrder : 'asc',
				// delayLoad : true,
				height : '100%',
				width : '100%'
			});
		},
		// 报表指标查询_读取查询参数
		rptIdx_readQueryParms : function() {
			var rows = grid.getData(), indexNo = [];
			if (app.rptId) {
				app.indexInfo = {};
				$(rows).each(function(i, n) {
					indexNo.push(n.realIndexNo || n.indexNo);
					app.indexInfo[n.realIndexNo || n.indexNo] = n.cellNo || n.indexNm;
					// app.indexInfo[n.realIndexNo + '_' + n.indexVerId] = n.indexNm;	// indexVerId 版本号
				});
				var dataDate, checkType = liger.get('checkTypeCombox').getValue();
				if ('01' == checkType) {
					dataDate = { dataDate : liger.get('dataDate').getValue()};
				} else if ('02' == checkType) {
					dataDate = {
						startDate: liger.get('startDate').getValue(),
						endDate: liger.get('endDate').getValue(),
						dataDate : app.rptQuery_readQueryDataDate()
					};
				}
				app.orgInfo = {};
				var orgNms = liger.get('orgNm_sel').getText().split(',');
				var orgNos = liger.get('orgNm_sel').getValue().split(',');
				$(orgNms).each(function(i, n) {
					app.orgInfo[orgNos[i]] = orgNms[i];
				});
				
				app.queryParms = $.extend({
					indexNo : indexNo.join(), orgNo : liger.get('orgNm_sel').getValue()
				}, dataDate);
			}
		},
		// 初始化报表查询的 tab
		rptQuery_initTab : function(properties) {
			app.isInit = true;
			app.tab = $("#navtab1").ligerTab({
				moveToNew : false,
			    showSwitch : true,
			    onAfterSelectTabItem : function (tabId) {
			    	var jQdom = $('#' + tabId);
			    	if (!app.isInit) {
			    		if (jQdom.attr('isOK') == 'false') {
			    			require.config({
			    				baseUrl : app.ctx + "/plugin/js/",
			    				paths: {
			    					"view" : "show/views/rptview"
			    				}
			    			});
			    			require(["view"] , function(view){
			    				var settings = {
		    						targetHeight : ($("#" + tabId).height() - 51),
		    						ctx : app.ctx,
		    						readOnly : true ,
		    						cellDetail: false,
		    						initFromAjax : true,
		    						searchArgs : jQdom.attr('searchArgs'),
		    						ajaxData : $.parseJSON(jQdom.attr('ajaxData'))
			    				};
			    				if(properties
			    						&& typeof properties == "object"){
			    					for(var p in properties){
			    						settings[p] = properties[p];
			    					}
			    				}
			    				spread = view.init($("#" + tabId) , settings);
			    			});
			    			jQdom.attr('isOK', 'true');
			    		}
			    	}
			    	var params = $.parseJSON(jQdom.attr('ajaxData'));
			    	if(params && (params.templateType == "01" || params.templateType == "03")){
			    		if(params.isPaging == "Y"){
			    			$("#pagination").show();
			    			$('#navtab1').height(app.height - 40);
			    		}else{
			    			$("#pagination").hide();
			    			$('#navtab1').height(app.height);
			    		}
			    	}else{
			    		$("#pagination").hide();
			    		$('#navtab1').height(app.height);
			    	}
			    }
			});
		},
		// 执行报表查询
		rptQuery_execQuery : function(argsArr, rptInfo, lineInfo) {
			app.isInit = true;
			app.tab.removeAll();
			var height = $(window).height() - $("#mainsearch").height()-5;
			$(argsArr).each(function(i, n) {
				var searchArgs = n.searchArgs;
				var rptOrgNm=argsArr[i].orgNm;
				var subsOrgNm=rptOrgNm.substring(0,rptOrgNm.lastIndexOf('('));
				n.searchArgs = null;
				app.tab.addTabItem({
					tabid : 'tab_' + i,
					text : rptInfo[n.rptId] + (n.busiLineId ? (' - ' + lineInfo[n.busiLineId]) : '')+'【'+subsOrgNm+'】',
					showClose : true,
					height : height,
					content : '<div tabid="tab_' + i + '" id="tab_' + i + '" title="' + rptInfo[n.rptId] + '" style="height: ' + height + 'px;"'
							+ ' searchArgs=' + searchArgs + ' ajaxData=' + JSON2.stringify(n) + ' isOK="false"></div>'
				});
			});
			app.isInit = false;
			app.tab.selectTabItem('tab_0');
		},
		// 报表查询_读取查询参数
		rptQuery_readQueryParms : function(nodes) {
			var rptInfo = {}, lineInfo = { '' : '' };
			// 报表与数据日期关系
			var orgNms = liger.get('orgNm_sel').getText().split(',');
			var orgNos = liger.get('orgNm_sel').getValue().split(',');
			var dataDate = app.rptQuery_readQueryDataDate();
			var archiveType = liger.get('archiveTypeBox').getValue();
			var argsArr = $(orgNos).map(function(i, orgNo) {
				return $(dataDate).map(function(ii, date) {
					return $(nodes).map(function(iii, rpt) {
						if (rpt.nodeType == '2') {		// nodeType: 1.目录; 2.报表; 3.条线
							rptInfo[rpt.id] = rpt.text;
							var lineIds = [];
							if (rpt.children) {
								lineIds = $(rpt.children).map(function(i, n) {
									if (n.checked) {
										lineInfo[n.id] = n.text;
										return n.id;
									}
								}).toArray();
							}
							if (lineIds.length == 0) lineIds.push('');
							return $(lineIds).map(function(iiii, lineId) {
								return { 
									rptId: rpt.id, dataDate: date, busiLineId: lineId, orgNm: orgNms[i], templateType : rpt.templateType, isPaging : rpt.isPaging, archiveType: archiveType,
									searchArgs: JSON2.stringify([{DimNo:'ORG', Op:'=', Value: orgNo}])
								};
							}).toArray();
						}
					}).toArray();
				}).toArray();
			}).toArray();
			return { "argsArr": argsArr, "rptInfo": rptInfo, "lineInfo" : lineInfo };
		},
		// 报表查询_读取数据日期
		rptQuery_readQueryDataDate : function() {
			var dataDate = [], dt = liger.get('checkTypeCombox').getValue();
			var settlementType = liger.get('settlementTypeBox').getValue();//年终结算
			var date = liger.get('dataDate').getValue().split(',');
			var orgType = liger.get('orgTypeBox').getValue();
			if ('01' == dt) {
				//如果是年节后数据，查询次年1月1号的数据
				if("02" == settlementType && (orgType == "02" || orgType == "03")){
					for(var i=0;i<date.length;i++){
						if(date[i].indexOf("1231") != -1){
							dataDate.push(Number(date[i].substr(0,4))+1 + "0101");
						}else{
							dataDate.push(date[i]);
						}
					}
				}else{
					dataDate = date;
				}
			} else if ('02' == dt) {
				dateCal.init(liger.get('startDate').getValue(),liger.get('endDate').getValue());
				dataDate=dateCal.getDuringDate($('#cycle').val());
			}
			return dataDate;
		},
		// 报表查询_读取导出参数
		rptQuery_readExportParms : function(nodes) {
			var orgNms = liger.get('orgNm_sel').getText().split(',');
			var orgNos = liger.get('orgNm_sel').getValue().split(',');
			var dataDate = app.rptQuery_readQueryDataDate();
			var archiveType = liger.get('archiveTypeBox').getValue();
			var argsArr = $(orgNos).map(function(i, orgNo) {
				return $(dataDate).map(function(ii, date) {
					return $(nodes).map(function(iii, rpt) {
						if (rpt.nodeType == '2') {		// nodeType: 1.目录; 2.报表; 3.条线
							var lineIds = [];
							if (rpt.children) {
								lineIds = $(rpt.children).map(function(i, n) {
									if (n.checked) {
										return n.id;
									}
								}).toArray();
							}
							if (lineIds.length == 0) lineIds.push('');
							return $(lineIds).map(function(iiii, lineId) {
								return { 
									rptId: rpt.id, dataDate: date, busiLineId: lineId, orgNm: orgNms[i], archiveType: archiveType, 
									searchArgs: JSON2.stringify([{DimNo:'ORG', Op:'=', Value: orgNo}])
								};
							}).toArray();
						}
					}).toArray();
				}).toArray();
			}).toArray();
			return argsArr;
		},
		// 调整 content 区域高度
		initContentHeight : function() {
			$('#content').height($(window).height() - $('#mainsearch').height() - $('#bottom').height() - 24);
			$('#treeContainer').height($('#content').height() - $('#bottom').height() + 1);
		},
		equals: function (objA, objB) {
			if (typeof arguments[0] != typeof arguments[1])
				return false;
			// 数组
			if (typeof arguments[0] instanceof Array) {
				if (arguments[0].length != arguments[1].length)
					return false;
				var allElementsEqual = true;
				for (var i = 0; i < arguments[0].length; i++) {
					if (typeof arguments[0][i] != typeof arguments[1][i])
						return false;
					if (typeof arguments[0][i] == 'number' && typeof arguments[1][i] == 'number')
						allElementsEqual = (arguments[0][i] == arguments[1][i]);
					else
						allElementsEqual = arguments.callee(arguments[0][i], arguments[1][i]);	// 递归判断对象是否相等
				}
				return allElementsEqual;
			}
			// 对象
			if (arguments[0] instanceof Object && arguments[1] instanceof Object) {
				var result = true;
				var attributeLengthA = 0, attributeLengthB = 0;
				for (var o in arguments[0]) {
					// 判断两个对象的同名属性是否相同(数字或字符串)
					if (typeof arguments[0][o] == 'number' || typeof arguments[0][o] == 'string')
						result = arguments[0][o] == arguments[1][o];
					else {
						// 如果对象的属性也是对象, 则递归判断两个对象的同名属性
						if (!arguments.callee(arguments[0][o], arguments[1][o])) {
							return result = false;
						}
					}
					++attributeLengthA;
				}
				for (var o in arguments[1]) {
					++attributeLengthB;
				}
				// 如果两个对象的属性数目不等, 则两个对象也不等
				if (attributeLengthA != attributeLengthB)
					result = false;
				return result;
			}
			return arguments[0] == arguments[1];
		}
	});
})(jQuery);