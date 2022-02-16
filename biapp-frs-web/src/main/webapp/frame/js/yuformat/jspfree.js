//autor:xch
(function($) {
	window['JSPFree'] = {};

	// 通用万通的调用!反射!
	// 比如var v_rt =
	// JSPFree.doClassMethodCall("com.yuchengtech.JSPFree.base.utils.YuFORMATBSUtil2","testMethod",v_sql);
	// 这个方法的好处是,不要再折腾Control中的映射了,对应参数名，如果改参数名或加参数,则要改一堆,还要去文本搜索寻找对应的路径,现在从入口可以直接看到是哪个类与方法!
	JSPFree.doClassMethodCall = function(_className, _methodName, _parJSO) {
		return FreeUtil.doClassMethodCall(_className, _methodName, _parJSO);
	};

	// 异步调用!
	JSPFree.doClassMethodCall2 = function(_className, _methodName, _parJSO,
			callBackFunction) {
		FreeUtil.doClassMethodCall2(_className, _methodName, _parJSO,
				callBackFunction);
	};

	// 直接查询数据
	JSPFree.getHashVOs = function(_sql) {
		return FreeUtil.getHashVOs(_sql); // 直接查询数据
	};
	// 直接查询数据(多数据源)
	JSPFree.getHashVOsByDS = function(_sql, _ds) {
		return FreeUtil.getHashVOsByDS(_sql, _ds); // 直接查询数据
	};

	// 重设一个整个页面高度
	JSPFree.resetHeight = function(_divid) {
		var li_height = window.innerHeight;
		document.getElementById(_divid).style.height = (li_height - 2) + "px"; //
	};

	JSPFree.resetHeight2 = function(_divid) {
		var li_height = window.innerHeight;
		document.getElementById(_divid).style.height = (li_height - 1) + "px"; //
	};

	// 创建多页签
	JSPFree.createTabb = function(_divid, _titles, _width) {
		FreeUtil.createTabb(_divid, _titles, _width);
	};

	// 创建多页签..
	JSPFree.createTabbByBtn = function(_divid, _titles, _btns, _isNorth, _width) {
		FreeUtil.createTabbByBtn(_divid, _titles, _btns, _isNorth, _width);
	};

	// 增加多页签监听事件,入参自定义函数有两个参数(_index,_title)
	JSPFree.addTabbSelectChangedListener = function(_tabb, _func) {
		_tabb.tabs({
			onSelect : function(_title) {
				var itemTab = _tabb.tabs('getSelected'); // 选中的页签
				var index = _tabb.tabs('getTabIndex', itemTab); // 选中的页签位置,第一个是0
				_func(index, _title); // 调自己的函数,传入两个参数
			}
		});
	};

	// 人工函数决定选择多页签的哪一个
	JSPFree.setTabbSelected = function(_tabb, _indexOrTitle) {
		_tabb.tabs('select', _indexOrTitle);
	};

	// 创建分割器
	JSPFree.createSplit = function(_divid, _type, _location, _isCanDrag) {
		var isCanDrag = true;
		if (typeof _isCanDrag != "undefined") {
			isCanDrag = _isCanDrag;
		}

		if (_type == "左右") {
			FreeUtil.createSplit_X(_divid, _location, isCanDrag); // 水平分割
		} else if (_type == "上下") {
			FreeUtil.createSplit_Y(_divid, _location, isCanDrag); // 上下分割
		} else {
			console.log("错误的类型【" + _type + "】");
		}
	};

	// 创建分割器
	JSPFree.createSplitByBtn = function(_divid, _type, _location, _btns,
			_isCanDrag) {
		var isCanDrag = true;
		if (typeof _isCanDrag != "undefined") {
			isCanDrag = _isCanDrag;
		}

		if (_type == "左右") {
			FreeUtil.createSplit_X_ByBtn(_divid, _location, _btns, isCanDrag); // 水平分割
		} else if (_type == "上下") {
			FreeUtil.createSplit_Y_ByBtn(_divid, _location, _btns, isCanDrag); // 上下分割
		} else {
			console.log("错误的类型【" + _type + "】");
		}
	};

	// 创建一个空白的Span但下面带按钮
	JSPFree.createSpanByBtn = function(_divid, _btns, _isNorth, _isBtnAlignLeft) {
		FreeUtil.createSpanByBtn(_divid, _btns, _isNorth, _isBtnAlignLeft);
	};

	// 创建文本
	JSPFree.createText = function(_id, _text) {
		var div_root = document.getElementById(_id); // 先取得根div
		div_root.innerHTML = "<span>" + _text + "</span>"; // 直接创建文本
	}

	// IFrame
	JSPFree.createOrReplaceIFrame = function(_divid, _jsfile, _pars) {
		var str_pars = encodeURI(encodeURI(JSON.stringify(_pars))); //

		var str_url = v_context + "/frs/yufreejs?js="; // 路径
		str_url = str_url + _jsfile + "&OpenPars=" + str_pars;

		// 创建iframe,极其关键!
		var dom_iframe = document.createElement("iframe"); //
		dom_iframe.id = _divid + "_iframe"; //
		dom_iframe.src = str_url; //
		dom_iframe.width = "100%";
		dom_iframe.height = "100%";

		var div_root = document.getElementById(_divid); // 先取得根div

		// 先删除所有旧的!
		var v_nodelist = div_root.childNodes; // NodeList
		for (var i = 0; i < v_nodelist.length; i++) {
			div_root.removeChild(v_nodelist.item(i));
		}

		// 再加入新的
		div_root.appendChild(dom_iframe); //
	};

	// 只计算出模板VO
	JSPFree.getTempletVO = function(_templetCode) {
		var v_par = {
			templetcode : _templetCode
		};
		var jso_templetVO = JSPFree.doClassMethodCall(
				FreeUtil.CommDMOClassName, "getTempletVO", v_par); //
		return jso_templetVO;
	};

	// 创建列表(EasyUI)
	JSPFree.createBillList = function(_id, _templetCode, _btns, _listConfig) {
		var v_par = {
			webcontext : v_context,
			divid : _id,
			templetCode : _templetCode,
			buttons : _btns,
			listConfig : _listConfig
		};
		var jso_htmljs = JSPFree.doClassMethodCall(
				FreeUtil.JSPBuilderClassName, "getBillListHtmlAndJsByEasyUI",
				v_par); //

		var str_jstext_1 = jso_htmljs.jstext_1; // 返回JS源代码
		var str_html = jso_htmljs.html; // 返回的html
		var str_jstext = jso_htmljs.jstext; // 返回JS源代码

		// 先搞Html页面
		var div_root = document.getElementById(_id); // 先取得根div
		div_root.innerHTML = str_html; // 先把页面改了

		// 再搞JavaScript代码
		var newJSObj = document.createElement("script"); // 一定要用这个创建,否则浏览器不解析
		newJSObj.id = _id;
		newJSObj.type = "text/javascript"; // 一定设置类型
		newJSObj.text = str_jstext; // 直接设置文本
		document.body.appendChild(newJSObj); // 在</body>前加上
	};

	// 直接从前端送入列名与宽度,生成一个最简单的表格
	JSPFree.createBillListByItems = function(_id, _templetCode, _itemKeys,
			_itemNames, _itemWidths, _listConfig) {
		var v_par = {
			webcontext : v_context,
			divid : _id,
			templetCode : _templetCode,
			itemKeys : _itemKeys,
			itemNames : _itemNames,
			itemWidths : _itemWidths,
			listConfig : _listConfig
		};
		var jso_htmljs = JSPFree.doClassMethodCall(
				FreeUtil.JSPBuilderClassName,
				"getBillListHtmlAndJsByEasyUIByItems", v_par); //

		var str_jstext_1 = jso_htmljs.jstext_1; // 返回JS源代码
		var str_html = jso_htmljs.html; // 返回的html
		var str_jstext = jso_htmljs.jstext; // 返回JS源代码

		// 先搞Html页面
		var div_root = document.getElementById(_id); // 先取得根div
		div_root.innerHTML = str_html; // 先把页面改了

		// 再搞JavaScript代码
		var newJSObj = document.createElement("script"); // 一定要用这个创建,否则浏览器不解析
		newJSObj.id = _id;
		newJSObj.type = "text/javascript"; // 一定设置类型
		newJSObj.text = str_jstext; // 直接设置文本
		document.body.appendChild(newJSObj); // 在</body>前加上
	};

	// 创建卡片,可以带按钮,带参数
	JSPFree.createBillCard = function(_id, _templetCode, _btns, _cardConfig) {
		var v_par = {
			webcontext : v_context,
			divid : _id,
			templetCode : _templetCode,
			buttons : _btns,
			cardConfig : _cardConfig
		};
		var jso_htmljs = JSPFree.doClassMethodCall(
				FreeUtil.JSPBuilderClassName, "getBilCardHtmlAndJsByEasyUI",
				v_par); //
		var str_html = jso_htmljs.html; // 返回的html
		var str_jstext = jso_htmljs.jstext; // 返回JS源代码

		// 先搞Html页面
		var div_root = document.getElementById(_id); // 先取得根div
		div_root.innerHTML = str_html; // 先把页面改了

		// 再搞JavaScript代码
		var newJSObj = document.createElement("script"); // 一定要用这个创建,否则浏览器不解析
		newJSObj.id = _id;
		newJSObj.type = "text/javascript"; // 一定设置类型
		newJSObj.text = str_jstext; // 直接设置文本
		document.body.appendChild(newJSObj); // 在</body>前加上
	};

	// 创建查询框
	JSPFree.createBillQuery = function(_id, _templetCode) {
		var v_par = {
			"webcontext" : v_context,
			"divid" : _id,
			"templetCode" : _templetCode
		};
		var jso_htmljs = JSPFree.doClassMethodCall(
				FreeUtil.JSPBuilderClassName, "getBillQueryHtmlAndJsByEasyUI",
				v_par); //
		var str_html = jso_htmljs.html; // 返回的html
		var str_jstext = jso_htmljs.jstext; // 返回JS源代码

		// 先搞Html页面
		var div_root = document.getElementById(_id); // 先取得根div
		div_root.innerHTML = str_html; // 先把页面改了

		// 再搞JavaScript代码
		var newJSObj = document.createElement("script"); // 一定要用这个创建,否则浏览器不解析
		newJSObj.id = _id;
		newJSObj.type = "text/javascript"; // 一定设置类型
		newJSObj.text = str_jstext; // 直接设置文本
		document.body.appendChild(newJSObj); //
	};

	// 绑定查询事件
	JSPFree.bindQueryEvent = function(_query, _function) {
		_query.onQuery = _function; // 设置对象!
	}

	// 取得查询表单生成的SQL
	JSPFree.getQueryFormSQL = function(_query) {
		return "9=9";
	};

	// 从前端设置强制SQL条件,不是XML中设置的,有时弹出一个子表,必须是强制过滤
	JSPFree.setBillListForceSQLWhere = function(_billList, _forceSQLWhere) {
		_billList.forceSQLWhere = _forceSQLWhere; //
	};

	// 根据类名反射取数,这个方法入参必须是JSObject,返回是HashVO[]
	JSPFree.queryDataByClass = function(_billList, _className, _methodName,
			_jso_par, _isYiBu) {
		FreeUtil.queryDataByClass(_billList, _className, _methodName, _jso_par,
				_isYiBu);
	};

	// 直接送SQL
	JSPFree.queryDataBySQL = function(_grid, _sql) {
		if("2"==_grid.pagerType || "1"==_grid.pagerType){  //如果定义了分页类型,则强行分页
			FreeUtil.queryDataByConditonRealBySQL(_grid, _sql, false, true, 1);
		} else {
			FreeUtil.queryDataByConditonRealBySQL(_grid, _sql, false, false, 1);
		}
	};

	// 同步查询数据,也是默认方式
	JSPFree.queryDataByConditon = function(_grid, _cons) {
		FreeUtil.queryDataByConditonReal(_grid, _cons, false, 1, null, true);
	};

	// 异步查询数据
	JSPFree.queryDataByConditon2 = function(_grid, _cons) {
		FreeUtil.queryDataByConditonReal(_grid, _cons, true, 1, null, true);
	};

	// 双击事件,有两个参数rowIndex,rowData
	JSPFree.addBillListDoubleClick = function(_BillList, _func) {
		_BillList.dblClickRow = _func;
		// JSPFree.billListResetPageListener(_BillList);
	};

	// 绑定选择事件,函数有两个参数(rowIndex, rowData)
	// 以前的机制是立即绑定，但有问题,需要重置分页,后来改成在构造时预先设置onSelect,然后转调,就好了!就不需要重置分页了
	JSPFree.bindSelectEvent = function(_BillList, _func) {
		_BillList.rowSelectChanged = _func; //
		// JSPFree.billListResetPageListener(_BillList);
	};

	// 列表自定义查询
	JSPFree.billListBindCustQueryEvent = function(_BillList, _func) {
		_BillList.custQueryAction = _func;
	};

	// 重新设置分页切换
	JSPFree.billListResetPageListener = function(_BillList) {
		// 后来发现一个EasyUI的Bug,就是一旦绑定选择事件后,切换分页就不起效果了,不得已重新绑定一下!
		var str_list_ispagebar = _BillList.templetVO.templet_option.list_ispagebar;
		// 如果设置了分页,才做绑定,默认是分页的!
		if (typeof str_list_ispagebar == "undefined"
				|| str_list_ispagebar == "" || str_list_ispagebar == "Y") {
			var billList_pager = _BillList.datagrid('getPager');
			billList_pager.pagination({
				showRefresh : false,
				onChangePageSize : function(pageSize) {
					FreeUtil.resetOnePageSize(_BillList, pageSize);
				},
				onSelectPage : function(pageNumber, pageSize) {
					FreeUtil.skipToOnePage(_BillList, pageNumber);
				}
			});
		}
	};

	// 设置表格数据,直接送一个数组进入
	JSPFree.setBillListDatas = function(_billList, _datas) {
		var jso_realData = {
			total : _datas.length,
			rows : _datas
		}; // EasUI的表格数据必须是这个格式
		FreeUtil.appendBillListDataRowNum(jso_realData); // 补上【_rownum】,以前在后台处理的,后来发现还是在前台处理更好
		_billList.datagrid('loadData', jso_realData);
		_billList.datagrid('clearSelections');
	}

	// 往表格中加入一批数据
	JSPFree.billListAppendDatas = function(_billList, _datas) {
		var li_now = Date.now();
		for (var i = 0; i < _datas.length; i++) {
			_datas[i]["_rownum"] = ("" + (li_now + i)); // 一定要处理一下这个唯一标识,因为这是新增数据,所以不能用行号了,需要找到一种唯一机制
			_billList.datagrid('appendRow', _datas[i]);
		}
	};

	// 取得列表选中行的数据
	JSPFree.getBillListSelectData = function(_grid) {
		var json_rowdata = _grid.datagrid('getSelected'); //
		return json_rowdata;
	};

	// 删除当前行.
	JSPFree.billListDeleteCurrRow = function(_billList) {
		li_row = JSPFree.getBillListSelectRow(_billList);
		if (li_row < 0) {
			return;
		}
		JSPFree.billListDeleteRow(_billList, li_row);
	};

	// 删除某一行,转调EasyUI方法,为了以后可以更换其他插件
	JSPFree.billListDeleteRow = function(_billList, _row) {
		_billList.datagrid('deleteRow', _row);
	};

	// 取得列表选中行的数据
	JSPFree.getBillListSelectRow = function(_grid) {
		var jso_rowdata = JSPFree.getBillListSelectData(_grid); // 先取得选中的数据
		if (jso_rowdata == null) {
			return -1;
		}
		var str_rownumValue = jso_rowdata['_rownum']; // 取得行号数据
		var li_row = _grid.datagrid('getRowIndex', str_rownumValue); //
		return li_row;
	};

	// 取得列表所有选中数据
	JSPFree.getBillListSelectDatas = function(_grid) {
		var jsy_datas = _grid.datagrid('getSelections'); //
		return jsy_datas;
	};

	// 取得所有数据，条数上限为当前分页的大小。例如分页为20条，那么这里能获得20行
	JSPFree.getBillListAllDatas = function(_grid) {
		var jsy_datas = _grid.datagrid('getData'); //
		return jsy_datas.rows;
	};

	// 取得按钮对应的表格
	JSPFree.getBtnBindBillList = function(_btn) {
		return FreeUtil.getBtnBindBillList(_btn);
	};

	// 设置按钮是否启用
	JSPFree.setBillListBtnEnable = function(_billList, _btnText, _enable) {
		var str_divid = _billList.divid; // d1_BillList_Btn新增
		var btn_id = str_divid + "_BillList_Btn" + _btnText; // 按钮id
		if (_enable) {
			$("#" + btn_id).linkbutton('enable'); // 按钮有效
		} else {
			$("#" + btn_id).linkbutton('disable'); // 按钮失效
		}
	};

	// 设置按钮提示,可能要在AfterBodyLoad方法中调用
	JSPFree.setBillListBtnTip = function(_billList, _btnText, _tip) {
		var str_divid = _billList.divid; // div的id
		var btn_id = str_divid + "_BillList_Btn" + _btnText; // 按钮id
		$("#" + btn_id).attr("title", _tip); // 设置title属性
	};

	// 取得表单中某一项的值
	JSPFree.getBillCardItemValue = function(_billCard, _itemkey) {
		return FreeUtil.getBillCardItemValue(_billCard, _itemkey);
	};

	// 得到整个Form的所有数据
	JSPFree.getBillCardFormValue = function(_billCard) {
		return FreeUtil.getBillCardFormValue(_billCard);
	};

	// 取得查询面板中的值
	JSPFree.getBillQueryFormValue = function(_billQuery) {
		return FreeUtil.getBillQueryFormValue(_billQuery);
	};

	// 拼接SQLwhere条件根据主键字段与主键值
	JSPFree.getSQLWhereByPK = function(_templetVO, _data) {
		return FreeUtil.getSQLWhereByPK(_templetVO, _data);
	};

	// 从查询面板中点击查询动作的方法
	JSPFree.doQueryFromBillQuery = function(_billQuery, _billList) {
		try {
			var isPager = true; // 默认是分页
			if ("N" == _billList.templetVO.templet_option.list_ispagebar) {
				isPager = false; // 不分页
			}

			// 取得查询条件,并查询数据
			var str_sqlCons = JSPFree.getQueryFormSQLCons(_billQuery); // 先拼出SQL条件
			if (str_sqlCons == null) { // 如果校验不通过(比如查询必输项),则不做查询!
				return;
			}

			// 假如自定义查询逻辑,则走自定义查询函数
			if (typeof _billList.custQueryAction == "function") {
				_billList.custQueryAction(str_sqlCons);
				return;
			}

			// xml模板中定义的查询条件,如果有,则与传入的条件再合并!
			var str_xmlCons = _billList.templetVO.templet_option.querycontion;

			if (typeof str_xmlCons != "undefined" && str_xmlCons != null
					&& str_xmlCons.trim() != "") {
				if (str_sqlCons == "") {
					str_sqlCons = str_sqlCons + str_xmlCons; //
				} else {
					str_sqlCons = str_sqlCons + " and " + str_xmlCons + " "
				}
			}

			// 同步查询,第一页,不加xml条件了..
			FreeUtil.queryDataByConditonReal(_billList, str_sqlCons, false, 1,
					null, false);

			// 如果有分页,则把当前页重新置为1
			if (isPager) {
				var js_pager = _billList.datagrid('getPager'); //
				_billList["NotTriggerPageSelectedEvent"]= "N"; //以前发生查询后,然后跳转到第一页,然后又触发跳转页事件,然后再次查询,即查询两次!所以需要一个开关
				js_pager.pagination('select', 1); //查询时重新设置当前页是第1页,否则如果先跳至第2页,再查询时,会停在第2页,xch
				_billList["NotTriggerPageSelectedEvent"]= "Y";
			}
		} catch (_ex) {
			console.log(_ex); //
			JSPFree.openHtmlMsgBox2("发生异常", 900, 400, _ex);
		}
	};

	// 从查询面板中取得查询条件,需要整个表单HtmlForm对象,还需要整个模板配置对象!
	JSPFree.getQueryFormSQLCons = function(_BillQuery) {
		var str_divid = _BillQuery.divid; //
		var jso_templetVO = _BillQuery.templetVO; // 模板VO
		var dom_form = document.getElementById(str_divid + '_QueryForm'); // 找到form

		var li_queryLevel = _BillQuery.queryLevel; // 查询级别,即【常用条件】【更多条件】
		if (typeof li_queryLevel == "undefined") {
			li_queryLevel = 1; // 如果没定义,则默认是1
		}

		var queryItems = FreeUtil.getQueryItemFromTempletVO(jso_templetVO,li_queryLevel); // 参与查询的哪些列!
		return JSPFree.getQueryFormSQLConsByItems(dom_form, queryItems);
	};

	// 直接根据指定的Items,计算出查询SQL
	JSPFree.getQueryFormSQLConsByItems = function(_form, queryItems) {
		var str_allSQL = ""; // 所有SQL条件
		var isFirst = true; //
		var str_errMsg = "";
		// 遍历循环处理各个查询列
		for (var i = 0; i < queryItems.length; i++) {
			var str_itemkey = queryItems[i].itemkey;
			var str_sqlitem = FreeUtil.getQueryItemSQLCons(_form, queryItems[i]);
			// console.log("查询项[" + str_itemkey + "]条件[" + str_sqlitem + "]");
			if (str_sqlitem.indexOf("◆") == 0) {
				str_errMsg = str_errMsg	+ str_sqlitem.substring(1, str_sqlitem.length) + "<br>";
			}

			if (str_sqlitem != "") {
				if (isFirst) {
					str_allSQL = str_allSQL + " " + str_sqlitem + " "; // 第一个不要and
					isFirst = false; //
				} else {
					str_allSQL = str_allSQL + " and " + str_sqlitem + " "; // 拼接整个SQL
				}
			}
		}

		if (str_errMsg.length > 0) {
			FreeUtil.alert(str_errMsg);
			return null;
		} else {
			return str_allSQL;
		}
	};

	// 刷新当前行..
	JSPFree.refreshBillListCurrRow = function(_billList) {
		FreeUtil.refreshBillListCurrRow(_billList); //
	};

	// 刷新当前所有选中的行,即多行
	JSPFree.refreshBillListCurrRows = function(_billList) {
		FreeUtil.refreshBillListCurrRows(_billList); //
	};

	// 刷新当前页
	JSPFree.refreshBillListCurrPage = function(_billList) {
		FreeUtil.refreshBillListCurrPage(_billList);
	};

	// 设置新增或修改时,表单上参照定义的SQL的动态过滤参数
	JSPFree.setDefaultValues = function(_billList, _jsoValue) {
		_billList.DefaultValues = _jsoValue;
	};

	// 设置新增或修改时,表单上参照定义的SQL的动态过滤参数
	JSPFree.setInsertOrUpdateRefSQLWhere = function(_billList, _jsoValue) {
		_billList.InsertOrUpdateRefSQLWhere = _jsoValue;
	};

	// 列表中新增一条记录,默认值很特别.
	JSPFree.doBillListInsert = function(_grid) {
		var str_divid = _grid.divid; //
		var str_beforeInsertFn = "beforeInsert_" + str_divid + "_BillList"; // 保存前校验
		if (typeof self[str_beforeInsertFn] == "function") { // 如果有这个函数
			var isOK = self[str_beforeInsertFn](_grid); // 执行
			if (!isOK) {
				return; // 如果失败则返回
			}
		}

		var jso_templetVO = _grid.templetVO; // 模板对象
		var str_ds = jso_templetVO.templet_option.ds; // 数据源
		var str_templetcode = jso_templetVO.templet_option.templetcode;
		var str_templetname = jso_templetVO.templet_option.templetname;
		var str_fromtable = jso_templetVO.templet_option.fromtable; // 查询表名
		var str_savetable = jso_templetVO.templet_option.savetable; // 保存表名

		var str_card_innserjs = jso_templetVO.templet_option.card_innerjs; // 内部js文件
		var jso_DefaultValues = _grid.DefaultValues; // 表格是否有默认值对象,有可能为undefined
		var jso_InsertOrUpdateRefSQLWhere = _grid.InsertOrUpdateRefSQLWhere; // 参照SQL引用的变量

		var li_winwid = 720;
		var li_winhei = 480;
		var str_cardsize = jso_templetVO.templet_option["card_size"]; // 窗口大小
		if (typeof str_cardsize != "undefined" && str_cardsize != "") {
			var li_pos = str_cardsize.indexOf("*");
			li_winwid = str_cardsize.substring(0, li_pos);
			li_winhei = str_cardsize.substring(li_pos + 1, str_cardsize.length); // 高度
		}

		// 弹出卡片窗口,如果保存成功,则回调刷新当前行的逻辑!
		var jso_par = {
			templetcode : str_templetcode,
			DefaultValues : jso_DefaultValues,
			InsertOrUpdateRefSQLWhere : jso_InsertOrUpdateRefSQLWhere,
			ds : str_ds,
			fromtable : str_fromtable,
			savetable : str_savetable
		};

		// 如果模板中定义了card_innerjs,则传到后端引入这个js文件,因为新增/修改界面是平台封装死的功能界面,但又要有表单联动逻辑,所以要引入第二个js
		if (typeof str_card_innserjs != "undefined"
				&& str_card_innserjs != null && str_card_innserjs != null) {
			jso_par["innerjs2"] = str_card_innserjs; // 如果模板中定义了js2,则在后端引入js2
		}

		JSPFree.openDialog(str_templetname,
				"/frame/js/yuformat/BillCardInsertDialog.js", li_winwid,
				li_winhei, jso_par, function(_rtdata) {
					if ((typeof _rtdata) != "undefined"
							&& "ok" == _rtdata.result) {
						FreeUtil.refreshBillListOneRowDataByInsert(_grid,
								_rtdata.SQLWhere);
					}
				});
	};

	// 修改列表中的一条记录!
	JSPFree.doBillListUpdate = function(_grid, _isCanSave) {
		var json_rowdata = _grid.datagrid('getSelected'); // 先得到数据
		if (json_rowdata == null) {
			JSPFree.alert('必须选择一条数据!');
			return;
		}

		var jsy_datas = _grid.datagrid('getSelections'); //
		if (jsy_datas.length > 1) {
			JSPFree.alert('只能选择一条数据进行操作!<br>目前共选择了【' + jsy_datas.length
					+ '】条,请注意滚动框下面是否还有其他选择!');
			return;
		}

		var str_divid = _grid.divid; //
		var str_beforeUpdateFn = "beforeUpdate_" + str_divid + "_BillList"; // 保存前校验
		if (typeof self[str_beforeUpdateFn] == "function") { // 如果有这个函数
			var isOK = self[str_beforeUpdateFn](_grid, json_rowdata); // 执行
			if (!isOK) {
				return; // 如果失败则返回
			}
		}

		var str_rownumValue = json_rowdata['_rownum']; // 取得行号数据
		var int_selrow = _grid.datagrid("getRowIndex", str_rownumValue); // 根据_rownum的值,计算出是第几行,后面刷新就是这一行!

		var jso_templetVO = _grid.templetVO; // 模板对象
		var str_ds = jso_templetVO.templet_option.ds; // 数据源
		var str_templetcode = jso_templetVO.templet_option.templetcode;
		var str_templetname = jso_templetVO.templet_option.templetname;
		var str_fromtable = jso_templetVO.templet_option.fromtable; // 查询表名
		var str_savetable = jso_templetVO.templet_option.savetable; // 保存表名

		var str_card_innserjs = jso_templetVO.templet_option.card_innerjs; // 内部js文件
		var jso_InsertOrUpdateRefSQLWhere = _grid.InsertOrUpdateRefSQLWhere; // 参照SQL引用的变量

		var str_sqlwhere = FreeUtil
				.getSQLWhereByPK(jso_templetVO, json_rowdata); // 拼出SQL

		// 拼接主键where条件,带到弹出卡片中去查询数据!
		var bo_isCanSave = true;
		if (typeof _isCanSave != "undefined") {
			bo_isCanSave = _isCanSave;
		}

		var jso_par = {
			templetcode : str_templetcode,
			SQLWhere : str_sqlwhere,
			isCanSave : bo_isCanSave,
			InsertOrUpdateRefSQLWhere : jso_InsertOrUpdateRefSQLWhere,
			ds : str_ds,
			fromtable : str_fromtable,
			savetable : str_savetable
		};

		// 如果模板中定义了card_innerjs,则传到后端引入这个js文件,因为新增/修改界面是平台封装死的功能界面,但又要有表单联动逻辑,所以要引入第二个js
		if (typeof str_card_innserjs != "undefined"
				&& str_card_innserjs != null && str_card_innserjs != null) {
			jso_par["innerjs2"] = str_card_innserjs; // 如果模板中定义了js2,则在后端引入js2
		}

		// 弹出卡片窗口,如果保存成功,则回调刷新当前行的逻辑!
		var li_winwid = 720;
		var li_winhei = 480;
		var str_cardsize = jso_templetVO.templet_option["card_size"]; // 窗口大小
		if (typeof str_cardsize != "undefined" && str_cardsize != "") {
			var li_pos = str_cardsize.indexOf("*");
			li_winwid = str_cardsize.substring(0, li_pos);
			li_winhei = str_cardsize.substring(li_pos + 1, str_cardsize.length); // 高度
		}

		JSPFree.openDialog(str_templetname,
				"/frame/js/yuformat/BillCardUpdateDialog.js", li_winwid,
				li_winhei, jso_par, function(_rtdata) {
					if ((typeof _rtdata) != "undefined"
							&& "ok" == _rtdata.result) {
						FreeUtil.refreshBillListOneRowDataByUpdate(_grid,
								int_selrow, _rtdata.SQLWhere);
					}
				});
	};

	// 查看记录
	JSPFree.doBillListView = function(_billList) {
		JSPFree.doBillListUpdate(_billList, false); // 转调修改逻辑
	};

	// 查看记录2
	JSPFree.doBillListView2 = function(_grid) {
		var json_rowdata = _grid.datagrid('getSelected'); // 先得到数据
		if (json_rowdata == null) {
			JSPFree.alert('必须选择一条数据!');
			return;
		}

		var jsy_datas = _grid.datagrid('getSelections'); //
		if (jsy_datas.length > 1) {
			JSPFree.alert('只能选择一条数据进行操作!<br>目前共选择了【' + jsy_datas.length
					+ '】条,请注意滚动框下面是否还有其他选择!');
			return;
		}

		var str_divid = _grid.divid; //
		var str_rownumValue = json_rowdata['_rownum']; // 取得行号数据
		var int_selrow = _grid.datagrid("getRowIndex", str_rownumValue); // 根据_rownum的值,计算出是第几行,后面刷新就是这一行!

		var jso_templetVO = _grid.templetVO; // 模板对象
		var str_templetcode = jso_templetVO.templet_option.templetcode;
		var str_templetname = jso_templetVO.templet_option.templetname;
		var str_card_innserjs = jso_templetVO.templet_option.card_innerjs; // 内部js文件

		var jso_par = {
			templetcode : str_templetcode
		};

		jso_par["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数

		// 如果模板中定义了card_innerjs,则传到后端引入这个js文件,因为新增/修改界面是平台封装死的功能界面,但又要有表单联动逻辑,所以要引入第二个js
		if (typeof str_card_innserjs != "undefined"
				&& str_card_innserjs != null && str_card_innserjs != null) {
			jso_par["innerjs2"] = str_card_innserjs; // 如果模板中定义了js2,则在后端引入js2
		}

		// 弹出卡片窗口,如果保存成功,则回调刷新当前行的逻辑!
		var li_winwid = 720;
		var li_winhei = 480;
		var str_cardsize = jso_templetVO.templet_option["card_size"]; // 窗口大小
		if (typeof str_cardsize != "undefined" && str_cardsize != "") {
			var li_pos = str_cardsize.indexOf("*");
			li_winwid = str_cardsize.substring(0, li_pos);
			li_winhei = str_cardsize.substring(li_pos + 1, str_cardsize.length); // 高度
		}

		JSPFree.openDialog(str_templetname,
				"/frame/js/yuformat/BillCardViewDialog.js", li_winwid,
				li_winhei, jso_par, function(_rtdata) {
					// 查看2返回时不刷新
				});
	};

	// 列表中删除一条记录!
	JSPFree.doBillListDelete = function(_grid) {
		var json_rowdata = _grid.datagrid('getSelected'); // 先得到数据
		if (json_rowdata == null) {
			$.messager.alert('提示', '必须选择一条数据!', 'info');
			return;
		}

		var str_divid = _grid.divid; //
		var str_beforeDeleteFn = "beforeDelete_" + str_divid + "_BillList"; // 删除前校验
		if (typeof self[str_beforeDeleteFn] == "function") { // 如果有这个函数
			var isOK = self[str_beforeDeleteFn](_grid, json_rowdata); // 执行
			if (!isOK) {
				return; // 如果失败则返回
			}
		}

		// 警告提醒是否真的删除?
		$.messager.confirm('提示', '你真的要删除选中的记录吗?', function(_isConfirm) {
			if (!_isConfirm) {
				return;
			}

			var str_rownumValue = json_rowdata['_rownum']; // 取得行号数据
			var int_selrow = _grid.datagrid("getRowIndex", str_rownumValue); // 根据_rownum的值,计算出是第几行,后面刷新就是这一行!

			var jso_templetVO = _grid.templetVO; // 模板对象
			var str_ds = jso_templetVO.templet_option.ds; // 数据源
			var str_templetcode = jso_templetVO.templet_option.templetcode; // 模板编码
			var str_savetable = jso_templetVO.templet_option.savetable; // 保存的表名,删除时不要重新查模板了,直接送表名,性能高一点!
			var str_sqlwhere = FreeUtil.getSQLWhereByPK(jso_templetVO,
					json_rowdata); // 拼出SQL

			// 远程调用,真正删除数据库
			try {
				FreeUtil.execDeleteBillListdData(str_ds, str_templetcode,
						str_savetable, str_sqlwhere);

				// 从界面上删除行
				_grid.datagrid('deleteRow', int_selrow);
				$.messager.alert('提示', '删除数据成功!', 'info');
			} catch (_ex) {
				console.log(_ex);
				FreeUtil.openHtmlMsgBox("发生异常", 500, 250, _ex)
			}
		});
	};

	// 批量删除
	JSPFree.doBillListBatchDelete = function(_grid) {
		var json_rowdata = _grid.datagrid('getSelections'); // 先得到数据
		if (json_rowdata == null) {
			$.messager.alert('提示', '必须选择一条数据!', 'info');
			return;
		}

		var str_divid = _grid.divid; //
		var str_beforeDeleteFn = "beforeDelete_" + str_divid + "_BillList"; // 删除前校验
		if (typeof self[str_beforeDeleteFn] == "function") { // 如果有这个函数
			var isOK = self[str_beforeDeleteFn](_grid, json_rowdata); // 执行
			if (!isOK) {
				return; // 如果失败则返回
			}
		}

		// 警告提醒是否真的删除?
		$.messager.confirm('提示', '你真的要删除选中的记录吗?', function(_isConfirm) {
			if (!_isConfirm) {
				return;
			}

			var jso_templetVO = _grid.templetVO; // 模板对象
			var str_ds = jso_templetVO.templet_option.ds; // 数据源
			var str_templetcode = jso_templetVO.templet_option.templetcode; // 模板编码
			var str_savetable = jso_templetVO.templet_option.savetable; // 保存的表名,删除时不要重新查模板了,直接送表名,性能高一点!
			var selectRows = [];
			for (var i = 0; i < json_rowdata.length; i++) {
				selectRows.push(json_rowdata[i]);
			}
			// 远程调用,真正删除数据库
			try {
				for (var i = 0; i < selectRows.length; i++) {
					var str_rownumValue = selectRows[i]['_rownum']; // 取得行号数据
					var int_selrow = _grid.datagrid("getRowIndex",
							str_rownumValue); // 根据_rownum的值,计算出是第几行,后面刷新就是这一行!
					var str_sqlwhere = FreeUtil.getSQLWhereByPK(jso_templetVO,
							selectRows[i]); // 拼出SQL
					FreeUtil.execDeleteBillListdData(str_ds, str_templetcode,
							str_savetable, str_sqlwhere);
					// 从界面上删除行
					_grid.datagrid('deleteRow', int_selrow);
				}
				$.messager.alert('提示', '删除数据成功!', 'info');
			} catch (_ex) {
				console.log(_ex);
				FreeUtil.openHtmlMsgBox("发生异常", 500, 250, _ex)
			}
		});
	};

	// 新增卡片数据
	JSPFree.doBillCardInsert = function(_billcard, _custValidate) {
		var dom_form = _billcard.form; // form对象
		var jso_templetVO = _billcard.templetVO; // 模板配置数据
		var str_ds = jso_templetVO.templet_option.ds; // 数据源
		var str_templetcode = jso_templetVO.templet_option.templetcode; // 模板编码
		// 取得表单数据
		var jso_formData = JSPFree.getBillCardFormValue(_billcard);

		// 空值校验
		var isValidateNullSucess = FreeUtil.validateNullBilldData(_billcard,
				jso_templetVO, jso_formData);
		if (!isValidateNullSucess) {
			return false;
		}

		// 宽度校验
		var isValidateLengthSucess = FreeUtil.validateLengthBilldData(
				_billcard, jso_templetVO, jso_formData);
		if (!isValidateLengthSucess) {
			return false;
		}

		// 格式校验
		var isValidateFormatSucess = FreeUtil.validateFormatBilldData(
				jso_templetVO, jso_formData);
		if (!isValidateFormatSucess) {
			return false;
		}

		// 唯一性校验
		var isValidateOnlyOneSucess = FreeUtil.validateOnlyOneBilldData(
				jso_templetVO, jso_formData, false);
		if (!isValidateOnlyOneSucess) {
			return false;
		}

		// 如果定义了自定义校验
		if (typeof _custValidate == "function") {
			return _custValidate(_billcard); //
		}

		// 远程调用,插入数据库
		FreeUtil.execInsertBillCardData(str_ds, str_templetcode, jso_formData);
		_billcard.OldData = jso_formData;
		_billcard.AlreadyInsert = true; // 记录是已新增
		_billcard.saveResult = "ok"; //

		return true;
	};

	// 保存表单数据
	JSPFree.doBillCardUpdate = function(_billcard, _custValidate) {
		var dom_form = _billcard.form; // form对象
		var jso_templetVO = _billcard.templetVO; // 模板配置数据
		var str_ds = jso_templetVO.templet_option.ds; // 数据源
		var str_templetcode = jso_templetVO.templet_option.templetcode; // 模板编码
		var jso_oldData = _billcard.OldData; // 旧数据
		var str_sqlWhere = JSPFree.getSQLWhereByPK(jso_templetVO, jso_oldData); // 一定要根据旧数据拼出where条件,因为主键本身也可能修改!
		var jso_formData = JSPFree.getBillCardFormValue(_billcard); // 取得新数据

		// 空值校验
		var isValidateNullSucess = FreeUtil.validateNullBilldData(_billcard,
				jso_templetVO, jso_formData);
		if (!isValidateNullSucess) {
			return false;
		}
		
		// 宽度校验
		var isValidateLengthSucess = FreeUtil.validateLengthBilldData(
				_billcard, jso_templetVO, jso_formData);
		if (!isValidateLengthSucess) {
			return false;
		}

		// 格式校验
		var isValidateFormatSucess = FreeUtil.validateFormatBilldData(
				jso_templetVO, jso_formData);
		if (!isValidateFormatSucess) {
			return false;
		}

		// 唯一性校验
		var isValidateOnlyOneSucess = FreeUtil.validateOnlyOneBilldData(
				jso_templetVO, jso_formData, true);
		if (!isValidateOnlyOneSucess) {
			return false;
		}

		// 如果定义了自定义校验
		if (typeof _custValidate == "function") {
			return _custValidate(_billcard); //
		}

		
		// 对于guass数据，不允许对主键进行update，则需要去除update中的主键
		// jso_formData中去除主键
        // update by wangxy31 2021-10-11
		var jso_formdata_new = {};  // 表单数据对象
		var str_pk = _billcard.templetVO.templet_option.pkname.toLocaleLowerCase();  //主键字段名
		var pkItems = str_pk.split(",");  //有可能有多个联合主键
		for (let key in jso_formData) {
			for (let pkItem in pkItems) {
				if (pkItems[pkItem].indexOf(key) < 0) {
					jso_formdata_new[key] = jso_formData[key];  //设置数据
				}
			}
		}
		// 远程调用,保存数据库
		FreeUtil.execUpdateBillCardData(str_ds, str_templetcode, jso_formdata_new,
				str_sqlWhere);

		// 如果保存成功,则把新数据更新到旧数据上面去!这样再次修改时才会成功!因为DB中的值已经变化了,所以旧的where条件是没用了
		_billcard.OldData = jso_formData;
		_billcard.saveResult = "ok";
		return true;
	};
	
	// 简易版本的保存表单数据，用于单条校验之后的数据保存。原因是调用单条检验之前已经进行了空值，格式，宽度，唯一性和自定义的校验。
	// 而且调用单条检验之后的数据是符合规则的，所以这里就不需要进行二次校验了，以提高性能
	JSPFree.doBillCardUpdateForEasy = function(_billcard, _custValidate) {
		var dom_form = _billcard.form; // form对象
		var jso_templetVO = _billcard.templetVO; // 模板配置数据
		var str_ds = jso_templetVO.templet_option.ds; // 数据源
		var str_templetcode = jso_templetVO.templet_option.templetcode; // 模板编码
		var jso_oldData = _billcard.OldData; // 旧数据
		var str_sqlWhere = JSPFree.getSQLWhereByPK(jso_templetVO, jso_oldData); // 一定要根据旧数据拼出where条件,因为主键本身也可能修改!
		var jso_formData = JSPFree.getBillCardFormValue(_billcard); // 取得新数据

		// 远程调用,保存数据库
		FreeUtil.execUpdateBillCardData(str_ds, str_templetcode, jso_formData,
				str_sqlWhere);

		// 如果保存成功,则把新数据更新到旧数据上面去!这样再次修改时才会成功!因为DB中的值已经变化了,所以旧的where条件是没用了
		_billcard.OldData = jso_formData;
		_billcard.saveResult = "ok";
		return true;
	};
	
	// 简易版本的新增卡片数据，用于单条校验之后的数据保存。原因是调用单条检验之前已经进行了空值，格式，宽度，唯一性和自定义的校验。
	// 而且调用单条检验之后的数据是符合规则的，所以这里就不需要进行二次校验了，以提高性能
	JSPFree.doBillCardInsertForEasy = function(_billcard, _custValidate) {
		var dom_form = _billcard.form; // form对象
		var jso_templetVO = _billcard.templetVO; // 模板配置数据
		var str_ds = jso_templetVO.templet_option.ds; // 数据源
		var str_templetcode = jso_templetVO.templet_option.templetcode; // 模板编码
		// 取得表单数据
		var jso_formData = JSPFree.getBillCardFormValue(_billcard);

		// 远程调用,插入数据库
		FreeUtil.execInsertBillCardData(str_ds, str_templetcode, jso_formData);
		_billcard.OldData = jso_formData;
		_billcard.AlreadyInsert = true; // 记录是已新增
		_billcard.saveResult = "ok"; //

		return true;
	};

	// 执行卡片默认值公式
	JSPFree.execBillCardDefaultValueFormula = function(_billcard, _isUpdate) {
		var str_divid = _billcard.divid; //
		var dom_form = _billcard.form; // form对象
		var jso_templetVO = _billcard.templetVO; // 模板配置数据
		var array_items = jso_templetVO.templet_option_b; // 模板子表

		var jso_dfValue = {};
		for (var i = 0; i < array_items.length; i++) {
			var str_itemkey = array_items[i].itemkey;
			var str_default = array_items[i].defaultformula;

			// 修改时有专门的默认值公式,比如修改时,修改时是当前人员!
			if (typeof _isUpdate != "undefined" && _isUpdate) {
				str_default = array_items[i].defaultformula2;
			}

			if (typeof str_default != "undefined" && str_default != null
					&& str_default != "") {
				if (str_default == "UUID") {
					var str_uuid = FreeUtil.getUUIDFromServer(); // 远程调用取得UUID
					jso_dfValue[str_itemkey] = str_uuid; //
				} else if (str_default == "$CURRDATE") {
					var str_date = FreeUtil
							.dateFormat("yyyy-MM-dd", new Date());
					jso_dfValue[str_itemkey] = str_date; //
				} else if (str_default == "$CURRDATE8") {
					var str_date = FreeUtil.dateFormat("yyyyMMdd", new Date());
					jso_dfValue[str_itemkey] = str_date; //
				} else if (str_default == "$CURRTIME") {
					var str_time = FreeUtil.dateFormat("yyyy-MM-dd hh:mm:ss",
							new Date()); // 默认值是当前时间,到秒.
					jso_dfValue[str_itemkey] = str_time; //
				} else if (str_default == "$LoginUserCode") {
					jso_dfValue[str_itemkey] = self.str_LoginUserCode; // 登录人员编号
				} else if (str_default == "$LoginUserName") {
					jso_dfValue[str_itemkey] = self.str_LoginUserName; // 登录人员姓名
				} else if (str_default == "$LoginUserOrgNo") { // 登录人员机构号
					jso_dfValue[str_itemkey] = self.str_LoginUserOrgNo;
				} else if (str_default.indexOf("Class:") == 0) { // 直接送一个类!
					var str_classname = str_default.substring(6,
							str_default.length); // 取得类名
					var jso_rt = JSPFree.doClassMethodCall(str_classname,
							"getValue", null); // 远程调用计算出返回数据!
					jso_dfValue[str_itemkey] = jso_rt.value; // 设置返回值
				} else {
					jso_dfValue[str_itemkey] = str_default;
				}
			}
		}

		// 加载数据
		$('#' + str_divid + '_form').form('load', jso_dfValue);
	};

	// 设置卡片中下拉框的数据
	JSPFree.setBillCardComboBoxData = function(_billCard, _itemkey, _jsyData) {
		FreeUtil.setBillCardComboBoxData(_billCard, _itemkey, _jsyData);
	};

	// 设置卡片中下拉框的数据
	JSPFree.setBillCardComboBoxData2 = function(_billCard, _itemkey, _jsyData) {
		FreeUtil.setBillCardComboBoxData2(_billCard, _itemkey, _jsyData);
	};

	// 为表单所有字段设置帮助,必须在AfterBodyLoad()方法中调用才有效果
	JSPFree.setBillCardLabelHelptip = function(_billCard) {
		FreeUtil.setBillCardLabelHelptip(_billCard);
	};

	// 设置所有卡片警告框隐藏或显示
	JSPFree.setBillCardItemWarnMsgVisible = function(_billCard, _isVisible) {
		FreeUtil.setBillCardItemWarnMsgVisible(_billCard, _isVisible);
	};

	// 设置卡片某一项的警告消息,即在输入框下面多出一个警告提示
	JSPFree.setBillCardItemWarnMsg = function(_billCard, _itemkey, _helpMsg) {
		FreeUtil.setBillCardItemWarnMsg(_billCard, _itemkey, _helpMsg);
	};

	// 设置卡片某一项的帮助提示!
	JSPFree.setBillCardItemHelptip = function(_billCard, _itemkey, _helpMsg) {
		FreeUtil.setBillCardItemHelptip(_billCard, _itemkey, _helpMsg);
	};

	// 设置卡片某一项的背景颜色
	JSPFree.setBillCardItemColor = function(_billCard, _itemkey, _color) {
		FreeUtil.setBillCardItemColor(_billCard, _itemkey, _color);
	};

	// 设置卡片中某一项是否显示
	JSPFree.setBillCardItemVisible = function(_billCard, _itemkey, _isVisible) {
		FreeUtil.setBillCardItemVisible(_billCard, _itemkey, _isVisible);
	};

	// 设置卡片中某一项是否可编辑
	JSPFree.setBillCardItemEditable = function(_billCard, _itemkey, _isEditable) {
		FreeUtil.setBillCardItemEditable(_billCard, _itemkey, _isEditable);
	};

	// 设置查询面板中某一项是否可编辑
	JSPFree.setBillQueryItemEditable = function(_itemkey, _itemtype, _isEditable) {
		FreeUtil.setBillQueryItemEditable(_itemkey, _itemtype, _isEditable);
	};

	// 清空表单中某一项的值
	JSPFree.setBillCardItemClearValue = function(_billCard, _itemkey) {
		FreeUtil.setBillCardItemClearValue(_billCard, _itemkey);
	};

	// 设置表单中某一项上必输项
	JSPFree.setBillCardItemIsMust = function(_billCard, _itemkey, _isMust) {
		FreeUtil.setBillCardItemIsMust(_billCard, _itemkey, _isMust);
	};

	// 设置某一项的值
	JSPFree.setBillCardItemValue = function(_billcard, _itemkey, _value) {
		var jso_data = {};
		jso_data[_itemkey] = _value;
		JSPFree.setBillCardValues(_billcard, jso_data);
	};

	// 直接注入数据!
	JSPFree.setBillCardValues = function(_billcard, _jso_values) {
		var str_divid = _billcard.divid; //
		$('#' + str_divid + '_form').form('load', _jso_values);
	};

	// 查询卡片数据:flag区分是否来源于数据处理的数据：Y
	JSPFree.queryBillCardData = function(_billcard, _sqlWhere, _flag) {
		var str_divid = _billcard.divid; //
		var jso_templetVO = _billcard.templetVO; // 模板配置数据
		var str_ds = jso_templetVO.templet_option.ds; // 数据源
		var str_templetcode = jso_templetVO.templet_option.templetcode; // 模板编码
		var str_afterloadclass = jso_templetVO.templet_option.afterloadclass; // 后续加载类

		var jso_par = {
			ds : str_ds,
			templetcode : str_templetcode,
			SQLWhere : _sqlWhere,
			AfterLoadClass : str_afterloadclass
		}; //
		var jso_data = JSPFree.doClassMethodCall(FreeUtil.CommDMOClassName,
				"getBillCardData", jso_par); // 保存数据

		// 把数据加载到表单中,关键代码,以后可能会改成根据模板配置而一个个设置!
		FreeUtil.loadBillCardData(_billcard, jso_data); // 设置数据
		_billcard.OldData = jso_data; // 先把旧的数据存储下来!
		_billcard.CurrSQL = "select * from "
				+ jso_templetVO.templet_option.fromtable + " where "
				+ _sqlWhere; //

		// 处理颜色
		var warnColorMap = {}; //
		for (key in jso_data) {
			var li_pos = key.indexOf("◆warncolor"); //
			if (li_pos > 0) { // 如果有颜色
				var str_item = key.substring(0, li_pos); // 字段
				var str_warncolor = jso_data[key]; //
				warnColorMap[str_item] = str_warncolor; // 加入Map
			}
		}

		// 处理提示语
		var warnMsgMap = {}; //
		for (key in jso_data) {
			var li_pos = key.indexOf("◆warnmsg"); // 提示语
			if (li_pos > 0) { // 如果有颜色
				var str_item = key.substring(0, li_pos); // 字段
				var str_warnmsg = jso_data[key]; //
				warnMsgMap[str_item] = str_warnmsg; // 加入Map
			}
		}

		// 处理置灰
		var visibleMsgMap = {}; //
		for (key in jso_data) {
			var li_pos = key.indexOf("◆warnvisible"); // 置灰
			if (li_pos > 0) { // 如果有显示
				var str_item = key.substring(0, li_pos); // 字段
				var str_visiblemsg = jso_data[key]; //
				visibleMsgMap[str_item] = str_visiblemsg; // 加入Map
			}
		}

		// 设置颜色与提示
		JSPFree.setBillCardItemColorByMap(_billcard, warnColorMap);
		JSPFree.setBillCardItemHelptipByMap(_billcard, warnMsgMap);
		JSPFree.setBillCardItemWarnMsgByMap(_billcard, warnMsgMap);
		JSPFree.setBillCardItemWarnVisibleByMap(_billcard, warnMsgMap, _flag);
		
		return jso_data; // 还返回数据
	};

	// 设置表单的颜色
	JSPFree.setBillCardItemColorByMap = function(_billCard, _colorMap) {
		if (_colorMap == null || Object.keys(_colorMap).length <= 0) {
			return;
		}
		// 遍历设置颜色
		for (_key in _colorMap) {
			FreeUtil.setBillCardItemColor(_billCard, _key, _colorMap[_key]);
		}
	};

	// 设置表单提示
	JSPFree.setBillCardItemHelptipByMap = function(_billCard, _msgMap) {
		if (_msgMap == null || Object.keys(_msgMap).length <= 0) {
			return;
		}

		// 遍历设置提示
		for (_key in _msgMap) {
			FreeUtil.setBillCardItemHelptip(_billCard, _key, _msgMap[_key]);
		}
	};

	// 设置表单提示
	JSPFree.setBillCardItemWarnMsgByMap = function(_billCard, _msgMap) {
		if (_msgMap == null || Object.keys(_msgMap).length <= 0) {
			return;
		}

		// 遍历设置提示
		for (_key in _msgMap) {
			FreeUtil.setBillCardItemWarnMsg(_billCard, _key, _msgMap[_key]);
		}
	};

	// 设置表单置灰
	JSPFree.setBillCardItemWarnVisibleByMap = function(_billCard, _msgMap, _flag) {
		if (_flag != "Y") {
			return;
		}
		if (_msgMap == null || Object.keys(_msgMap).length <= 0) {
			FreeUtil.setBillCardItemEditable(_billCard, "*", false);
			return;
		}
		FreeUtil.setBillCardItemEditable(_billCard, "*", false);
		// 遍历设置提示
		for (_key in _msgMap) {
			FreeUtil.setBillCardItemEditable(_billCard, _key, true);
		}
	};

	// 创建树
	JSPFree.createBillTree = function(_id, _templetCode, _treeConfig) {
		JSPFree.createBillTreeByBtn(_id, _templetCode, null, _treeConfig);
	};

	// 创建树带按钮,_treeConfig是个对象，{isExpandRoot:true,checkbox:true}
	JSPFree.createBillTreeByBtn = function(_id, _templetCode, _btns,
			_treeConfig) {
		var v_par = {
			webcontext : v_context,
			divid : _id,
			templetCode : _templetCode,
			buttons : _btns,
			treeConfig : _treeConfig
		};
		var jso_htmljs = JSPFree.doClassMethodCall(
				FreeUtil.JSPBuilderClassName, "getBillTreeHtmlAndJsByEasyUI",
				v_par); //
		var str_html = jso_htmljs.html; // 返回的html
		var str_jstext = jso_htmljs.jstext; // 返回JS源代码

		// 先搞Html页面
		var div_root = document.getElementById(_id); // 先取得根div
		div_root.innerHTML = str_html; // 先把页面改了

		// 再搞JavaScript代码
		var newJSObj = document.createElement("script"); // 一定要用这个创建,否则浏览器不解析
		newJSObj.id = _id;
		newJSObj.type = "text/javascript"; // 一定设置类型
		newJSObj.text = str_jstext; // 直接设置文本
		document.body.appendChild(newJSObj); // 在</body>前加上
	}

	// 查询树的数据
	JSPFree.queryBillTreeData = function(_billTree) {
	};

	// 绑定树型控件的选择事件
	JSPFree.bindBillTreeOnSelect = function(_billTee, _function) {
		var str_treeid = _billTee.treeid; // 取得树的id
		$('#' + str_treeid).tree({
			onSelect : _function
		}); // 使用EasyUI的语法绑定事件
	};

	// 取得树的根结点
	JSPFree.getBillTreeRoot = function(_billTee) {
		var str_treeid = _billTee.treeid; // 取得树的id
		var rootNode = $('#' + str_treeid).tree('getRoot'); // 使用EasyUI的语法绑定事件
		return rootNode;
	};

	// 展开整个树,就是先找到根结点,然后展开根结点
	JSPFree.expandAllBillTree = function(_billTee) {
		var str_treeid = _billTee.treeid; // 取得树的id
		$('#' + str_treeid).tree('expandAll');
	};

	// 展开某个结点
	JSPFree.expandBillTreeNode = function(_billTee, _node) {
		var str_treeid = _billTee.treeid; // 取得树的id
		$('#' + str_treeid).tree('expandAll', _node.target);
	};

	// 取得树选择的结点
	JSPFree.getBillTreeCheckedNodes = function(_billTee) {
		var str_treeid = _billTee.treeid; // 取得树的id
		var jsy_nodes = $('#' + str_treeid).tree('getChecked'); // 使用EasyUI的语法绑定事件
		return jsy_nodes;
	};

	// 取得树选择的结点
	JSPFree.getBillTreeCheckedDatas = function(_billTee) {
		var str_treeid = _billTee.treeid; // 取得树的id
		var jsy_nodes = $('#' + str_treeid).tree('getChecked'); // 使用EasyUI的语法绑定事件
		var jsy_allData = [];
		if(jsy_nodes==null || jsy_nodes.length<=0){
			return jsy_allData;
		}

		for(var i=0;i<jsy_nodes.length;i++){
			var isRoot = JSPFree.isBillTreeNodeRoot(_billTee,jsy_nodes[i]);  //
			if(isRoot){
				continue;  //
			}
			var jso_rowData = jsy_nodes[i].attributes; // 数据都在这里
			jsy_allData.push(jso_rowData);
		}

		return jsy_allData;
	};

	// 取得树选择的结点
	JSPFree.getBillTreeSelectNode = function(_billTee) {
		var str_treeid = _billTee.treeid; // 取得树的id
		var jso_node = $('#' + str_treeid).tree('getSelected'); // 使用EasyUI的语法绑定事件
		return jso_node;
	};

	// 取得树选择的数据
	JSPFree.getBillTreeSelectData = function(_billTee) {
		var str_treeid = _billTee.treeid; // 取得树的id
		var jso_node = $('#' + str_treeid).tree('getSelected'); // 使用EasyUI的语法绑定事件
		if (jso_node == null) {
			return null;
		}
		var jso_rowData = jso_node.attributes; // 数据其实是绑定在attributes中
		return jso_rowData; //
	};

	// 取得树选择的子孙数据
	JSPFree.getBillTreeChildData = function(_billTee) {
		var str_treeid = _billTee.treeid; // 取得树的id
		var jso_node = $('#' + str_treeid).tree('getSelected'); // 使用EasyUI的语法绑定事件
		if (jso_node == null) {
			return null;
		}
		var arrNode = [];
		arrNode.push(jso_node.attributes);
		JSPFree.getChildNode(jso_node, arrNode);

		return arrNode; //
	};

	JSPFree.getChildNode = function(jso_node, arrNode) {
		for ( var obj in jso_node.children) {
			if (jso_node.children[obj].children) {
				JSPFree.getChildNode(jso_node.children[obj].children, arrNode);
			} else {
				arrNode.push(jso_node.children[obj].attributes);
			}
		}
		return arrNode;
	}

	// 判断是否是叶子结点
	JSPFree.isBillTreeSelectNodeLeaf = function(_billTee) {
		var str_treeid = _billTee.treeid; // 取得树的id
		var jso_node = $('#' + str_treeid).tree('getSelected'); // 使用EasyUI的语法绑定事件
		return JSPFree.isBillTreeNodeLeaf(_billTee, jso_node);
	};

	// 判断一个结点是否是叶子结点
	JSPFree.isBillTreeNodeLeaf = function(_billTee, _node) {
		if (_node == null) {
			return false;
		}
		var str_treeid = _billTee.treeid; // 取得树的id
		var isLeaf = $('#' + str_treeid).tree('isLeaf', _node.target);
		return isLeaf;
	};

	// 判断一个结点是否是叶子结点
	JSPFree.isBillTreeNodeRoot = function(_billTee, _node) {
		if (_node == null) {
			return false;
		}
		if (_node.id == "-99999ROOT") {
			return true;
		} else {
			return false;
		}
	};

	// 下载文件,类名必须继承于AbstractDownloadFile
	JSPFree.downloadFile = function(_className, _fileName, _par) {
		var dom_form = document.getElementById("commform"); //
		dom_form.action = v_context + FreeUtil.DownloadFileUrl; // 重新设置Form的Action
		dom_form.target = "commdownload"; // 提交到iframe中

		var jso_par = {
			classname : _className,
			filename : _fileName,
			par : _par
		}; // 参数
		var str_par = JSON.stringify(jso_par); //

		var dom_hidden = document.getElementById("commFormHiddenValue"); // 取得隐藏域
		dom_hidden.value = str_par; // 设置值

		dom_form.submit(); // 提交表单
	};

	// 直接送一个SQL,导出Excel(支持导出多个sheet)
	JSPFree.downloadExcelBySQL = function(_fileName, _sql, _sheetName, _header) {
		JSPFree.downloadFile(FreeUtil.CommServicePath + "ExportExcelBySQLDMO",
				_fileName, {
					SQL : _sql,
					sheetName : _sheetName,
					header : _header
				}); //
	};

	// 直接送一个SQL,导出Txt
	// 最后一个参数，类型：区分是否是银监上报的表，如果是银监上报的表，需要特殊处理
	JSPFree.downloadTxtBySQL = function(_ds,_fileName, _sql, _split, _type, _tabName) {
		JSPFree.downloadFile(FreeUtil.CommServicePath + "ExportTxtBySQLDMO",
			_fileName, {
				DS : _ds,
				SQL : _sql,
				splitStr : _split,
				typeStr : _type,
				tabName : _tabName
			});
	};

	// 导出一个列表的当前数据
	JSPFree.downloadBillListDataAsExcel = function(_billList) {
		var str_sql = _billList.CurrSQL;
		if (typeof str_sql == "undefined") {
			JSPFree.alert("还没有查询数据,请先查询数据才能导出!");
			return;
		}
		// 取得模板编码与名称
		var str_templetcode = _billList.templetVO.templet_option.templetcode; // 模板编码
		var str_templetname = _billList.templetVO.templet_option.templetname; // 模板名称

		// 构建入参
		var jso_par = {
			SQL : str_sql,
			TempletCode : str_templetcode
		}; // 模板编码与SQL
		JSPFree.downloadFile(FreeUtil.CommServicePath
				+ "ExportBillListDataAsExcelDMO", str_templetname + ".xls",
				jso_par);
	};

	// 导出一个列表的当前数据
	JSPFree.downloadBillListDataAsExcel1 = function(_billList, _filename, _sheetName) {
		var str_sql = _billList.CurrSQL3;
		if (typeof str_sql == "undefined") {
			JSPFree.alert("还没有查询数据,请先查询数据才能导出!");
			return;
		}
		// 取得模板编码与名称
		var str_templetcode = _billList.templetVO.templet_option.templetcode; // 模板编码
		var str_templetname = _billList.templetVO.templet_option.templetname; // 模板名称

		// 构建入参
		var jso_par = {
			SQL : str_sql,
			TempletCode : str_templetcode,
			sheetName : _sheetName
		}; // 模板编码与SQL
		JSPFree.downloadFile(FreeUtil.CommServicePath
				+ "ExportBillListDataAsExcelDMO", _filename + ".xls",
				jso_par);
	};

	//导出一个列表的
	JSPFree.downloadBillListCurrSQL3AsExcel = function(_ds,_billList,_cols) {
		var str_templetcode = _billList.templetVO.templet_option.templetcode; // 模板编码
		var str_templetname = _billList.templetVO.templet_option.templetname; // 模板名称
		var str_sql = _billList.CurrSQL3; //当前SQL

		//如果没有定义定段,则从模板取!
		var jsy_cols = [];
		var jsy_aligns = [];
		if(typeof _cols == "undefined" || _cols==null){
			var itemVOs = _billList.templetVO.templet_option_b;
			for(var i=0;i<itemVOs.length;i++){
				if("Y"==itemVOs[i].list_isshow){  //如果列表显示!
					jsy_cols.push(itemVOs[i].itemkey + "/" + itemVOs[i].itemname);
					jsy_aligns.push(itemVOs[i].itemkey + "/" + itemVOs[i].list_align);
				}
			}
		}else{
			jsy_cols = _cols; //使用传入的!
		}

		var jso_par = {
			TempletCode : str_templetcode,
			TempletName : str_templetname,
			SQL3 : str_sql,
			Cols : jsy_cols,
			Aligns : jsy_aligns,
			Ds : _ds
		}; 
		JSPFree.downloadFile(FreeUtil.CommServicePath + "ExportExcelByCurrSQL3DMO", str_templetname + ".xls",jso_par); //
	};
	
	//通过sql语句查询数据，使用指定excel模板导出excel文件，提供下载
	JSPFree.downloanDataBySQLAsExcel = function(_ds,_billList,_sql,_fileName,_downloadName,_headRow) {
		//如果没有定义定段,则从模板取!
		var jsy_cols = [];  //
		if(typeof _cols == "undefined" || _cols==null){
			var itemVOs = _billList.templetVO.templet_option_b;  //
			for(var i=0;i<itemVOs.length;i++){
				if("Y"==itemVOs[i].list_isshow){  //如果列表显示!
					jsy_cols.push(itemVOs[i].itemkey);  //
				}
			}
		}else{
			jsy_cols = _cols; //使用传入的!
		}
		
		var jso_par = {
			FileName : _fileName, //指定excel模板文件名称
			DownloadName : _downloadName, //下载文件名称
			SQL : _sql,
			Cols : jsy_cols,
			Ds : _ds,
			HeadRow : _headRow //表头占的行数
		};
		JSPFree.downloadFile(FreeUtil.CommServicePath + "ExportDataBySQLAsExcelDMO", _downloadName, jso_par); //
	};

	// 直接弹出一个html窗口,没有远程调用!
	JSPFree.openHtmlMsgBox = function(_title, _width, _height, _html) {
		FreeUtil.openHtmlMsgBox(_title, _width, _height, _html);
	};

	// 第二种直接弹出一个html窗口,即可以突破父亲窗口限制,但会造成远程调用!但数据是从前端传入的
	JSPFree.openHtmlMsgBox2 = function(_title, _width, _height, _html) {
		FreeUtil.openHtmlMsgBox2(_title, _width, _height, _html);
	};

	// 弹出新窗口,并传参与回调函数
	JSPFree.openDialog = function(_title, _js, _width, _height, _pars,
			_callBack, _isHaveCloseBtn) {
		FreeUtil.openDialog(_title, _js, _width, _height, _pars, _callBack,
				_isHaveCloseBtn);
	};

	// 之前有个问题是_pars太大时,url太长,会报错,所以提供第2种方法,即从前端传入变量
	// 实际上就是再用_BillCardData包装一下!
	JSPFree.openDialog2 = function(_title, _js, _width, _height, _pars,
			_callBack, _isHaveCloseBtn) {
		var jso_par = {
			_BillCardData : _pars
		};
		FreeUtil.openDialog(_title, _js, _width, _height, jso_par, _callBack,
				_isHaveCloseBtn);
	};

	// 弹出新窗口,并传参与回调函数
	// ADD BY WANGXY31	
	// DESC：明细数据打开修改，新增按钮时，要全屏铺开
	JSPFree.openDialog3 = function(_title, _js, _width, _height, _pars,
			_callBack, _isHaveCloseBtn) {
		FreeUtil.openDialogAndCloseMe1(_title, _js, _width, _height, _pars, _callBack,
				false,_isHaveCloseBtn);
	};

	// 打开窗口并立即关闭自己
	JSPFree.openDialogAndCloseMe = function(_title, _js, _width, _height,
			_pars, _isHaveCloseBtn) {
		FreeUtil.openDialogAndCloseMe(_title, _js, _width, _height, _pars,
				null, true, _isHaveCloseBtn);
	}

	// 打开窗口并立即关闭自己
	JSPFree.openDialogAndCloseMe2 = function(_title, _js, _width, _height,
			_pars, _isHaveCloseBtn) {
		var jso_par = {
			_BillCardData : _pars
		};
		FreeUtil.openDialogAndCloseMe(_title, _js, _width, _height, jso_par,
				null, true, _isHaveCloseBtn);
	}

	// 关闭弹出窗口,并有返回值..
	JSPFree.closeDialog = function(_returnData) {
		FreeUtil.closeDialog(_returnData);
	};

	// 关闭所有弹出窗口,并回调根窗口的回调函数
	JSPFree.closeDialogAll = function(_returnData) {
		FreeUtil.closeDialogAll(_returnData);
	};

	// 关闭本窗口,但直接回调根窗口的回调方法
	JSPFree.closeDialogToRoot = function(_returnData) {
		FreeUtil.closeDialogToRoot(_returnData);
	};

	// 提示,包一下,以后可以更换其他开源框架!
	JSPFree.alert = function(_str, _callBackFunc) {
		FreeUtil.alert(_str, _callBackFunc);
	};

	// 确认选择!_func有一个参数_isOK
	JSPFree.confirm = function(_title, _msg, _func) {
		$.messager.confirm(_title, _msg, _func);
	};
	
	JSPFree.confirmYesOrNo = function(_title, _msg, _func) {
		$.messager.defaults.ok = "是";
		$.messager.defaults.cancel = "否";
		$.messager.confirm(_title, _msg, _func);
		
		$.messager.defaults.ok = "确认";
		$.messager.defaults.cancel = "取消";
	};

	JSPFree.prompt = function(_title, _msg, _func) {
		$.messager.prompt(_title, _msg, _func);
	};

	// 标准的查看Html连接内容
	JSPFree.onLookHtmlAction = function(_btn) {
		var str_cmd = _btn.dataset.itemvalue;
		FreeUtil.openHtmlMsgBox("查看", 400, 150, str_cmd);
	};

	//是否显示假设校验失败按钮
	JSPFree.showOrHideBtn = function(_billList,_btnName,flag){
		if("Y" == flag){
			$("#"+_billList[0].id + "_Btn" + _btnName).parent().show();
		}else{
			$("#"+_billList[0].id + "_Btn" + _btnName).parent().hide();
		}
	};
	
	// 单条数据校验，调用方法
	JSPFree.editTableCheckData = function(_billCard, _value, _tabname, _tabnameen, _ds, _type) {
		// 判断value是新增还是修改
		var _formData = "";
		if (_value == "Add") {
			_formData = JSPFree.doBillCardInsertByCheck(_billCard, null);
		} else if (_value == "Edit") {
			_formData = JSPFree.doBillCardUpdateByCheck(_billCard, null);
		}
		
		return JSPFree.editTableCheckDataDetail(_billCard, _formData, _tabname, _tabnameen, _ds, _type);
	};

	// 新增表单前的校验：获取表单数据，用于调用引擎的单条校验
	JSPFree.doBillCardInsertByCheck = function(_billcard, _custValidate) {
		var jso_templetVO = _billcard.templetVO; // 模板配置数据
		// 取得表单数据
		var jso_formData = JSPFree.getBillCardFormValue(_billcard);

		// 空值校验
		var isValidateNullSucess = FreeUtil.validateNullBilldData(_billcard,
				jso_templetVO, jso_formData);
		if (!isValidateNullSucess) {
			return false;
		}

		// 宽度校验
		var isValidateLengthSucess = FreeUtil.validateLengthBilldData(
				_billcard, jso_templetVO, jso_formData);
		if (!isValidateLengthSucess) {
			return false;
		}

		// 格式校验
		var isValidateFormatSucess = FreeUtil.validateFormatBilldData(
				jso_templetVO, jso_formData);
		if (!isValidateFormatSucess) {
			return false;
		}

		// 唯一性校验
		var isValidateOnlyOneSucess = FreeUtil.validateOnlyOneBilldData(
				jso_templetVO, jso_formData, false);
		if (!isValidateOnlyOneSucess) {
			return false;
		}

		// 如果定义了自定义校验
		if (typeof _custValidate == "function") {
			return _custValidate(_billcard); //
		}

		return jso_formData;
	};
	
	// 保存表单数据，用于单条校验保存前的校验
	JSPFree.doBillCardUpdateByCheck = function(_billcard, _custValidate) {
		var jso_templetVO = _billcard.templetVO; // 模板配置数据
		var jso_formData = JSPFree.getBillCardFormValue(_billcard); // 取得新数据

		// 空值校验
		var isValidateNullSucess = FreeUtil.validateNullBilldData(_billcard,
				jso_templetVO, jso_formData);
		if (!isValidateNullSucess) {
			return false;
		}
		
		// 宽度校验
		var isValidateLengthSucess = FreeUtil.validateLengthBilldData(
				_billcard, jso_templetVO, jso_formData);
		if (!isValidateLengthSucess) {
			return false;
		}

		// 格式校验
		var isValidateFormatSucess = FreeUtil.validateFormatBilldData(
				jso_templetVO, jso_formData);
		if (!isValidateFormatSucess) {
			return false;
		}

		// 唯一性校验
		var isValidateOnlyOneSucess = FreeUtil.validateOnlyOneBilldData(
				jso_templetVO, jso_formData, true);
		if (!isValidateOnlyOneSucess) {
			return false;
		}

		// 如果定义了自定义校验
		if (typeof _custValidate == "function") {
			return _custValidate(_billcard); //
		}

		return jso_formData;
	};

	JSPFree.editTableCheckDataDetail = function(_billCard, _formData, _tabname, _tabnameen, _ds, _type) {
		// 保存之前，先校验，填的数据是否符合银监校验
		// ADD BY WANGXY31 -- 20200213
		// 1:east
		// 2:客户风险
		// 3:金标
		// 8:外汇局
		// BFD:金融基础
		var jso_rt = "";
		if (_formData != false) {
			var _method = "";
			if ("1" == _type) {
				_method = "com.yusys.east.singlecheck.EastSingleCheckBS";
			} else if ("2" == _type) {
				_method = "com.yusys.crrs.singlecheck.CrrsSingleCheckBS";
			} else if ("3" == _type) {
				_method = "com.yusys.fsrs.singlecheck.FsrsSingleCheckBS";
			} else if ("8" == _type) {
				_method = "com.yusys.safe.singlecheck.SafeSingleCheckBS";
			} else if ("BFD" == _type) {
				_method = "com.yusys.bfd.singlecheck.BfdSingleCheckBS";
			} else if ("IMAS" == _type) {
				_method = "com.yusys.imas.singlecheck.ImasSingleCheckBS";
			}
			jso_rt = JSPFree.doClassMethodCall(_method, "singleCheckData",{data:_formData,tabName:_tabname,tabNameEn:_tabnameen,ds:_ds});
		} else {
			return "";
		}
		
		if (jso_rt.status == "OK") {
			var ruleIds = jso_rt.ruleId;
			// 判断返回的ruleId是否为null，如果为null则表示数据验证通过，如果不为null则表示验证失败
			if (ruleIds == "" || ruleIds == null) {

				// 有可能重复点击保存按钮，要清空页面高亮显示
				JSPFree.setBillCardItemWarnMsgVisible(_billCard, false);
				JSPFree.setBillCardItemColor(_billCard, "*", "");
				
				return "OK";
			}
			
			// 有可能重复点击保存按钮，要清空页面高亮显示
			JSPFree.setBillCardItemWarnMsgVisible(_billCard, false);
			JSPFree.setBillCardItemColor(_billCard, "*", "");
			
			// 验证失败，则页面进行规则展示
			var ruleArr = ruleIds.split(',');
			
			// 1east; 2客户风险; 3金标; 8外汇局;BFD金融基础
			if ("1" == _type) {
				// east传过来的tabName为中文表名
				var jso_data1 = JSPFree.getHashVOs("select distinct c.col_name_en,c.col_name from east_cr_rule r,east_cr_col c " +
						"where r.tab_name = c.tab_name " +
						"and r.col_name = c.col_name " +
						"and c.IS_EXPORT='Y' " +
						"and (rule_sts='Y' or rule_sts is null) " +
						"and r.tab_name='"+_tabname+"'" +
						"and id in ("+ruleArr+")");
				
				var colnames = [];
				if (jso_data1 != null && jso_data1 != "" && jso_data1.length>0) {
					for (var j=0;j<jso_data1.length;j++) {
						var colname_en = jso_data1[j].col_name_en;
						var colname = jso_data1[j].col_name;
						var jso_data2 = JSPFree.getHashVOs("select rule_name problemmsg from east_cr_rule " +
								"where (rule_sts='Y' or rule_sts is null) and tab_name ='"+_tabname+"' " +
								"and col_name = '"+colname+"' and id in ("+ruleArr+")");
						
						var problemmsg = "";
						if (jso_data2 != null && jso_data2 != "" && jso_data2.length>0) {
							for (var k=0;k<jso_data2.length;k++) {
								problemmsg = problemmsg + (k+1) + "." + jso_data2[k].problemmsg + "<br>";
							}
						}
						JSPFree.setBillCardItemWarnMsg(_billCard, colname_en.toLowerCase(), problemmsg);
						JSPFree.setBillCardItemColor(_billCard, colname_en.toLowerCase(), "yellow");
					}
				}
				
				return "Fail";
			} else if ("2" == _type) {
				var rules = [];
				for (var i=0;i<ruleArr.length;i++) {
					var value = ruleArr[i];
					rules.push("'"+value+"'");
				}
				
				var jso_data1 = JSPFree.getHashVOs("select DISTINCT colname, colname_en from crrs_rule where (isopen='Y' or isopen is null) and tablename_en = " +
						"'"+_tabnameen.toUpperCase()+"' and id in ("+rules+")");
				
				var colnames = [];
				if (jso_data1 != null && jso_data1 != "" && jso_data1.length>0) {
					for (var j=0;j<jso_data1.length;j++) {
						var colname = jso_data1[j].colname;
						var colname_en = jso_data1[j].colname_en;
						var jso_data2 = JSPFree.getHashVOs("select problemmsg from crrs_rule where (isopen='Y' or isopen is null) and tablename_en = " +
								"'"+_tabnameen.toUpperCase()+"' and colname = '"+colname+"' and id in ("+rules+")");
						
						var problemmsg = "";
						if (jso_data2 != null && jso_data2 != "" && jso_data2.length>0) {
							for (var k=0;k<jso_data2.length;k++) {
								problemmsg = problemmsg + (k+1) + "." + jso_data2[k].problemmsg + "<br>";
							}
						}
						JSPFree.setBillCardItemWarnMsg(_billCard, colname_en.toLowerCase(), problemmsg);
						JSPFree.setBillCardItemColor(_billCard, colname_en.toLowerCase(), "yellow");
					}
				}
				
				return "Fail";
			} else if ("3" == _type) {
				var rules = [];
				for (var i=0;i<ruleArr.length;i++) {
					var value = ruleArr[i];
					rules.push("'"+value+"'");
				}
				
				var jso_data1 = JSPFree.getHashVOs("select DISTINCT colname_en from fsrs_rule where (isopen='Y' or isopen is null) and tablename_en = " +
						"'"+_tabnameen.toUpperCase()+"' and id in ("+rules+")");

				var colnames = [];
				if (jso_data1 != null && jso_data1 != "" && jso_data1.length>0) {
					for (var j=0;j<jso_data1.length;j++) {
						var colname = jso_data1[j].colname_en;
						var jso_data2 = JSPFree.getHashVOs("select problemmsg from fsrs_rule where (isopen='Y' or isopen is null) and tablename_en = " +
								"'"+_tabnameen.toUpperCase()+"' and colname_en = '"+colname+"' and id in ("+rules+")");
						
						var problemmsg = "";
						if (jso_data2 != null && jso_data2 != "" && jso_data2.length>0) {
							for (var k=0;k<jso_data2.length;k++) {
								problemmsg = problemmsg + (k+1) + "." + jso_data2[k].problemmsg + "<br>";
							}
						}
						JSPFree.setBillCardItemWarnMsg(_billCard, colname.toLowerCase(), problemmsg);
						JSPFree.setBillCardItemColor(_billCard, colname.toLowerCase(), "yellow");
					}
				}
				
				return "Fail";
			} else if ("8" == _type) {
				var rules = [];
				for (var i=0;i<ruleArr.length;i++) {
					var value = ruleArr[i];
					rules.push(""+value+"");
				}
				
				var jso_data1 = JSPFree.getHashVOs("select col_name_en, col_name from safe_cr_col where col_name in " +
						"(select col_name from safe_cr_rule where tab_name = '"+_tabname+"' AND (rule_sts ='Y' or rule_sts is null) " +
						"AND ID IN ("+rules+")) and tab_name = '"+_tabname+"'");

				var colnames = [];
				if (jso_data1 != null && jso_data1 != "" && jso_data1.length > 0) {
					for (var j=0;j<jso_data1.length;j++) {
						var colname = jso_data1[j].col_name;
						var colname_en = jso_data1[j].col_name_en;
						var jso_data2 = JSPFree.getHashVOs("select rule_name from safe_cr_rule where (rule_sts='Y' or rule_sts is null) and tab_name = " +
								"'"+_tabname+"' and col_name = '"+colname+"' and id in ("+rules+")");
						
						var problemmsg = "";
						if (jso_data2 != null && jso_data2 != "" && jso_data2.length>0) {
							for (var k=0;k<jso_data2.length;k++) {
								problemmsg = problemmsg + (k+1) + "." + jso_data2[k].rule_name + "<br>";
							}
						}
						JSPFree.setBillCardItemWarnMsg(_billCard, colname_en.toLowerCase(), problemmsg);
						JSPFree.setBillCardItemColor(_billCard, colname_en.toLowerCase(), "yellow");
					}
				}
				
				return "Fail";
			} else if ("BFD" == _type) {
				var rules = [];
				for (var i=0;i<ruleArr.length;i++) {
					var value = ruleArr[i];
					rules.push("'"+value+"'");
				}

				var jso_data1 = JSPFree.getHashVOs("select col_name_en, col_name from bfd_cr_col where col_name in " +
						"(select col_name from bfd_cr_rule where tab_name = '"+_tabname+"' AND (rule_sts ='Y' or rule_sts is null) " +
						"AND id IN ("+rules+")) and tab_name = '"+_tabname+"'");

				var colnames = [];
				if (jso_data1 != null && jso_data1 != "" && jso_data1.length > 0) {
					for (var j=0;j<jso_data1.length;j++) {
						var colname = jso_data1[j].col_name;
						var colname_en = jso_data1[j].col_name_en;
						var jso_data2 = JSPFree.getHashVOs("select rule_name from bfd_cr_rule where (rule_sts='Y' or rule_sts is null) and tab_name = " +
								"'"+_tabname+"' and col_name = '"+colname+"' and id in ("+rules+")");

						var problemmsg = "";
						if (jso_data2 != null && jso_data2 != "" && jso_data2.length>0) {
							for (var k=0;k<jso_data2.length;k++) {
								problemmsg = problemmsg + (k+1) + "." + jso_data2[k].rule_name + "<br>";
							}
						}
						JSPFree.setBillCardItemWarnMsg(_billCard, colname_en.toLowerCase(), problemmsg);
						JSPFree.setBillCardItemColor(_billCard, colname_en.toLowerCase(), "yellow");
					}
				}

				return "Fail";
			} else if ("IMAS" == _type) {
				var rules = [];
				for (var i=0;i<ruleArr.length;i++) {
					var value = ruleArr[i];
					rules.push("'"+value+"'");
				}

				var jso_data1 = JSPFree.getHashVOs("select col_name_en, col_name from imas_cr_col where col_name in " +
					"(select col_name from imas_cr_rule where tab_name = '"+_tabname+"' AND (rule_sts ='Y' or rule_sts is null) " +
					"AND id IN ("+rules+")) and tab_name = '"+_tabname+"'");

				var colnames = [];
				if (jso_data1 != null && jso_data1 != "" && jso_data1.length > 0) {
					for (var j=0;j<jso_data1.length;j++) {
						var colname = jso_data1[j].col_name;
						var colname_en = jso_data1[j].col_name_en;
						var jso_data2 = JSPFree.getHashVOs("select rule_name from imas_cr_rule where (rule_sts='Y' or rule_sts is null) and tab_name = " +
							"'"+_tabname+"' and col_name = '"+colname+"' and id in ("+rules+")");

						var problemmsg = "";
						if (jso_data2 != null && jso_data2 != "" && jso_data2.length>0) {
							for (var k=0;k<jso_data2.length;k++) {
								problemmsg = problemmsg + (k+1) + "." + jso_data2[k].rule_name + "<br>";
							}
						}
						JSPFree.setBillCardItemWarnMsg(_billCard, colname_en.toLowerCase(), problemmsg);
						JSPFree.setBillCardItemColor(_billCard, colname_en.toLowerCase(), "yellow");
					}
				}

				return "Fail";
			}
		} else {
			JSPFree.alert("请求异常!");
		}
	};

    // 根据父表查询所有子表，返回子表按钮 -- create by wm 2021-4-9 10:29:11 ，外汇局有报表展示的页面都用到了此方法，所以写成公共函数
    JSPFree.getChildTabBtn = function(tab_name_en, report_type) {
        return FreeUtil.getChildTabBtn(tab_name_en, report_type)
    };
})(jQuery);
