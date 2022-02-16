<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<script type="text/javascript">
	//全局变量
	var grid;
	var gridLoadOver = false;
	var orgDimTypeNo  =  '${orgDimTypeNo}';
	var orgDimTypeName  =  '${orgDimTypeName}';
	var dateDimTypeNo  =  '${dateDimTypeNo}';
	var dateDimTypeName  =  '${dateDimTypeName}';
	var currencyDimTypeNo  =  '${currencyDimTypeNo}';
	var currencyDimTypeName  =  '${currencyDimTypeName}';
	var indexDimTypeNo  =  '${indexDimTypeNo}';
	var indexDimTypeName =  '${indexDimTypeName}';
	var SET_TYPE_MUTI_DIM  =  '${SET_TYPE_MUTI_DIM}';
	var SET_TYPE_SUM  =  '${SET_TYPE_SUM}';
	var SET_TYPE_GENERIC  =  '${SET_TYPE_GENERIC}';
	var SET_TYPE_REPORT  =  '${SET_TYPE_REPORT}';
	var SET_TYPE_DETAIL  =  '${SET_TYPE_DETAIL}';
	var setType ;
	var allMeasures  =  eval('(${allMeasures})');
	
	//////////////////////////////////////////////////////////////////////////////
	// 根据维度No，取得维度名称，eval函数当参数的字符串过长时，会报错，所以修改
	//var allDims  =  eval('(${allDims})');
	var strDimTypeNos = '${dimTypeNos}';
	var strDimTypeNms = '${dimTypeNms}';
	strDimTypeNos = strDimTypeNos.replace("[","");
	strDimTypeNos = strDimTypeNos.replace("]","");
	dimTypeNos = strDimTypeNos.split(",");
	strDimTypeNms = strDimTypeNms.replace("[","");
	strDimTypeNms = strDimTypeNms.replace("]","");
	dimTypeNms = strDimTypeNms.split(",");
	/////////////////////////////////////////////////////////////////////////////
	var measureNoArr;
	var dimTypeNoArr;
	function  updateMeasureNoArr(measureNo){
		for(var i  =0;i<measureNoArr.length;i++){
			  var  obj  =  measureNoArr[i];
			  if(obj.measureNo==measureNo){
			    	return  false;
			  }
		}
		measureNoArr.push({
			"measureNo":measureNo
		});
		return  true;
	}
	function  updateDimTypeNoArr(dimTypeNo){
		for(var i  =0;i<dimTypeNoArr.length;i++){
			  var  obj  =  dimTypeNoArr[i];
			  if(obj.dimTypeNo==dimTypeNo){
			    	return  false;
			  }
		}
		dimTypeNoArr.push({
			"dimTypeNo":dimTypeNo
		});
		return  true;
	}
	function    getMeasureName(measureNo){
		for(var i=0;i<allMeasures.length;i++){
			var  obj   =allMeasures[i];
			if(obj.measureNo==measureNo){
				return  obj.measureNm;
			}
		}
		return   measureNo;
	}
	function    getDimTypeName(dimTypeNo){
		/* for(var i=0;i<allDims.length;i++){
			var  obj   =allDims[i];
			if(obj.dimTypeNo==dimTypeNo){
				return  obj.dimTypeNm;
			} 
		} */
		for(var i=0;i<dimTypeNos.length;i++){
			var  objDimTypeNo = dimTypeNos[i];
			if(objDimTypeNo==dimTypeNo){
				return  dimTypeNms[i];
			} 
		}
		return   dimTypeNo;
	}
	$(function() {
		var parent = window.parent;
		var datasetObj = parent.datasetObj;
		setType =  datasetObj.setType;
		//渲染grid
		ligerGrid();

		//渲染GRID
		function ligerGrid() {
			//初始化tip
			var gridHeight = $("#center").height() - 21;
			
			var leftCols  =[{
				display : "字段名",
				name : "enNm",
				width : '16%',
				isSort : false,
				editor : {
					type : 'text'
				}
			},
			{
				display : "中文名称",
				width : '13%',
				name : "cnNm",
				editor : {
					type : 'text'
				},
				isSort : false
			},
			{
				display : "数据库类型",
				name : "dbType",
				width : '9%',
				isSort : false,
				editor : {
					type : 'select',
					data : [ {
						'id' : '01',
						'text' : '文本'
					}, {
						'id' : '02',
						'text' : '数字'
					}, {
						'id' : '03',
						'text' : '日期'
					}, {
						'id' : '05',
						'text' : '对象'
					} ]
				},
				render : function(row) {
					switch (row.dbType) {
					case "01":
						return "文本";
					case "02":
						return "数字";
					case "03":
						return "日期";
					case "05":
						return "对象";
					default:
						return "未知";
					}
				}
			},{
				display : "字段类型",
				name : "colType",
				width : '8%',
				isSort : false,
				editor : {
					type : 'select',
					data : [ {
						'id' : '00',
						'text' : '属性字段'
					}, {
						'id' : '01',
						'text' : '度量字段'
					}, {
						'id' : '02',
						'text' : '维度字段'
					} ]
				},
				render : function(row) {
					switch (row.colType) {
						case "00":{
							return  "属性字段";
						}
						case "01":{
							return   "度量字段";
						}	
						case "02":{
							return   "维度字段";
						}
						default:{
							return   "";
						}
					}
				} 
			}];  
			var  dynamicW = '8%';
			if(setType==SET_TYPE_SUM||setType==SET_TYPE_REPORT){
				dynamicW = '5%';
				leftCols.push({
					display : "度量类型",
					name : "oper_measure",
					width : '13%',
					isSort : false,
					editor : {
						type : 'text'
					},
					render : function(row,item,value) {
						if(row.colType=="01"){
							if(row.measureName){//初始化和后续操作
								return  row.measureName;
							}
						}else if(row.colType=="02"){
							row.measureNo =  "";
						}else if(row.colType=="00"){
							row.dimTypeNo =  "";
							row.measureNo =  "";  
						}
						if(value)
							return value;
						else
							return ""; 
					}

				});
			}
		    var rightCols =[{
				display : "维度类型",
				name : "oper_dim",
				width : '13%',
				isSort : false,
				editor : {
					type : 'text'
				},
				render : function(row,item,value) {
					if(row.colType=="01"){
						row.dimTypeNo =  "";
					}else if(row.colType=="02"){
						if(row.dimTypeName){//初始化和后续操作
							return  row.dimTypeName;
						}
					}else if(row.colType=="00"){
						row.dimTypeNo =  "";
						row.measureNo =  "";
					}
					if(value)
						return value;
					else
						return ""; 
				}

			},
			{
				display : "长度",
				name : "len",
				width : dynamicW,
				isSort : false,
				editor : {
					type : 'int'
				}
			},
			{
				display : "精度",
				name : "precision2",
				width :dynamicW,
				isSort : false,
				editor : {
					type : 'int'
				}
			},
			{
				display : "可空",
				name : "isNull",
				width : dynamicW,
				isSort : false,
				render : function(row) {
					return "<input name='isNullCheck' style='margin-top:5px;' disabled='true' onclick='checkFunc(\"nullable\",\""
							+ row.__id
							+ "\",this);' type='checkbox' "
							+ ((row.isNull == "Y") ? "checked='checked'"
									: "") + "/>";
				}
			},
			{
				display : "主键",
				name : "isPk",
				width : dynamicW,
				isSort : false,
				render : function(row) {
					return "<input name='isPkCheck' disabled='true' style='margin-top:5px;' onclick='checkFunc(\"pk\",\""
							+ row.__id
							+ "\",this);' type='checkbox' "
							+ ((row.isPk == "Y") ? "checked='checked'"
									: "") + "/>";
				}
			}
	];
			var columns  = leftCols.concat(rightCols);
			parent.colGrid = grid = $("#maingrid")
					.ligerGrid(
							{
								width : "99.8%",
								height : gridHeight,
								columns : columns,
								onSelectRow : function(rowdata, rowindex) {
									$("#txtrowindex").val(rowindex);
								},
								dataAction : 'server',//从后台获取数据
								method : 'post',
								url : "${ctx}/rpt/frame/dataset/colList.json?d="
										+ new Date().getTime(),
								delayLoad : true,//初始化时不加载数据
								checkbox : false,
								rownumbers : true,
								usePager : false
							});

			//colDrag(grid);

			//加载数据
			grid.set('parms', {
				from : datasetObj.from,
				datasetId : datasetObj.datasetId,
				dsId : datasetObj.dsId,
				table : datasetObj.table,
				d : new Date().getTime(),
				setType:setType
			});
			grid.loadData();
		}
        
		//保存
		function col_save() {
			if (!gridLoadOver) {
				BIONE.tip("努力加载中，请稍后。");
				return;
			}
			grid.endEdit(); //结束编辑
			var rows = grid.getData();
			if (rows.length == 0) {
				BIONE.tip("数据集至少配置一个数据项。");
				return;
			}
			var checkCols = [];
			for ( var i = 0, l = rows.length; i < l; i++) {
				if (!rows[i].enNm) {
					BIONE.tip("数据项字段名不能为空。");
					return;
				}
				if (!contains(checkCols, rows[i].enNm.toUpperCase())) {
					checkCols.push(rows[i].enNm.toUpperCase());
				} else {
					BIONE.tip("数据项名称必须唯一。");
					return;
				}
			}

			var sRows = grid.getSelectedRows();
			var sCols = [];
			if (sRows) {
				for ( var i = 0, l = sRows.length; i < l; i++) {
					sCols.push(sRows[i].enNm.toUpperCase());
				}
			}
			var cols = [];
			var orgDimTypeNoCount =  0;
			var dateDimTypeNoCount =  0;
			var currencyDimTypeNoCount =  0;
			var indexDimTypeNoCount =  0;
			measureNoArr=[];
			dimTypeNoArr=[];
			for ( var i = 0, l = rows.length; i < l; i++) {
				/* if(orgDimTypeNoCount >=2){
					BIONE.tip("维度类型["+orgDimTypeName+"]最多只能被一个数据列项选择！");
					return  ;
				}
				if(dateDimTypeNoCount >=2){
					BIONE.tip("维度类型["+dateDimTypeName+"]最多只能被一个数据项选择！");
					return  ;
				} */
				if(currencyDimTypeNoCount >=2){
					BIONE.tip("维度类型["+currencyDimTypeName+"]最多只能被一个数据项选择！");
					return  ;
				}
				if(indexDimTypeNoCount >=2){
					BIONE.tip("维度类型["+indexDimTypeName+"]最多只能被一个数据项选择！");
					return  ;
				}
				if(rows[i].dimTypeNo&&rows[i].dimTypeNo!=""){
					var  no_ =rows[i].dimTypeNo;
					if(no_==orgDimTypeNo){
						orgDimTypeNoCount++;
					}else if(no_==dateDimTypeNo){
						dateDimTypeNoCount++;
					}else  if(no_==currencyDimTypeNo){
						currencyDimTypeNoCount++;
					}else if(no_==indexDimTypeNo){
						indexDimTypeNoCount++;
					}
					 /* if(!updateDimTypeNoArr(rows[i].dimTypeNo)){
	                   	  BIONE.tip("维度["+getDimTypeName(rows[i].dimTypeNo)+"]最多只能被一个数据项选择！");
	                   	  return ;
	                 }   */
					
				}/* else if(rows[i].measureNo&&rows[i].measureNo!=""){
                    if(!updateMeasureNoArr(rows[i].measureNo)){
                    	  BIONE.tip("度量["+getMeasureName(rows[i].measureNo)+"]最多只能被一个数据项选择！");
                    	  return ;
                    }  
				} */
				var ft = rows[i].dbType;
				var isMeasure = rows[i].colType=='01';
				if(rows[i].dbType!='02'&&isMeasure){
                    BIONE.tip("数据项["+rows[i].enNm+"]选为度量字段时对应的数据库类型应该为[数字]!");
					return;
				}
				var col = {
					isUse : (contains(sCols, rows[i].enNm.toUpperCase())) ? "Y"
							: "N",
					orderNum : (i + 1),
					enNm : rows[i].enNm.toUpperCase(),
					cnNm : rows[i].cnNm||"",
					dbType : rows[i].dbType,
					colType : rows[i].colType,
					len : rows[i].len,
					precision2 : rows[i].precision2,
					isNull : rows[i].isNull=="Y"?"Y":"N",
					isPk : rows[i].isPk=="Y"?"Y":"N",
					measureNo :rows[i].colType=="01"?rows[i].measureNo:"",
					dimTypeNo :rows[i].colType=="02"?rows[i].dimTypeNo:"",
				    colId: rows[i].colId,
				    rowDraggable : false
				};
				cols.push(col);
			}
			if(datasetObj.setType!=SET_TYPE_REPORT && datasetObj.setType!=SET_TYPE_DETAIL){
				if(dateDimTypeNoCount==0){
					BIONE.tip("至少有一个数据项字段为["+dateDimTypeName+"]的维度类型字段");
					return  ;
				}
				/* if(orgDimTypeNoCount==0){
					BIONE.tip("至少有一个数据项字段为["+orgDimTypeName+"]的维度类型字段");
					return  ;
				} */
				/* if(currencyDimTypeNoCount==0){
					BIONE.tip("至少有一个数据项字段为["+currencyDimTypeName+"]的维度类型字段");
					return  ;
				} */
				if(indexDimTypeNoCount==0&&datasetObj.setType!=SET_TYPE_MUTI_DIM){
					BIONE.tip("至少有一个数据项字段为["+indexDimTypeName+"]的维度类型字段");
					return  ;
			    }
			}
			var datacolsJsonStr = '{fields:' + JSON2.stringify(cols) + '}';
			$.ajax({
				type : "POST",
				url : "${ctx}/rpt/frame/dataset/save",
				dataType : 'json',
				data : {
					setId : datasetObj.datasetId,
					catalogId : datasetObj.catalogId,
					sourceId : datasetObj.dsId,
					setNm : datasetObj.datasetName,
					tableEnNm : datasetObj.table,
					remark : datasetObj.remark,
					setType : datasetObj.setType,
					datacolsJsonStr : datacolsJsonStr
				},
				success : function() {
					window.parent.parent.BIONE.tip("保存成功！");
					//刷新grid
					window.parent.parent.datasetGrid.loadData();
					window.parent.closeDsetBox();
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					BIONE.tip('保存失败,错误信息:' + textStatus);
				}
			});
		}

		//包含测试
		function contains(array, string) {
			if (array) {
				for ( var i = 0, l = array.length; i < l; i++) {
					if (array[i] == string)
						return true;
				}
			}
			return false;
		}

	});

	//复选框操作
	function checkFunc(type, rowId, e) {
		var row = grid.getRow(rowId);
		if (type == "nullable") {
			row.isNull = e.checked ? "Y" : "N";
		} else if(type=="pk"){
			row.isPk = e.checked ? "Y" : "N";
		}
	}
	 
	 //拖拽支持
		var colDrag = function(grid){
			 var g = grid, p = grid.options;
                g.rowDroptip = $("<div class='l-drag-rowdroptip' style='display:none'></div>").appendTo('body');
                g.gridbody.add(g.f.gridbody).ligerDrag({ revert: true, animate: false,
                    proxyX: 0, proxyY: 0,
                    proxy: function (draggable, e)
                    {
                        var src = g._getSrcElementByEvent(e);
                        if (src.row)
                        {
                            var content = p.draggingMessage.replace(/{count}/, draggable.draggingRows ? draggable.draggingRows.length : 1);
                            if (p.rowDraggingRender)
                            {
                                content = p.rowDraggingRender(draggable.draggingRows, draggable, g);
                            }
                            var proxy = $("<div class='l-drag-proxy' style='display:none'><div class='l-drop-icon l-drop-no'></div>" + content + "</div>").appendTo('body');
                            return proxy;
                        }
                    },
                    onRevert: function () { return false; },
                    onRendered: function ()
                    {
                        this.set('cursor', 'default');
                        g.children[this.id] = this;
                    },
                    onStartDrag: function (current, e)
                    {
                        if (e.button == 2) return false;
                        if (g.colresizing) return false;
                        if (!g.columns.length) return false;
                        this.set('cursor', 'default');
                        var src = g._getSrcElementByEvent(e);
                        if (!src.cell || !src.data || src.checkbox) return false;
                        var ids = src.cell.id.split('|');
                        var column = g._columns[ids[ids.length - 1]];
                        if (src.rownumberscell || src.detailcell || src.checkboxcell || column == g.columns[0]|| column == g.columns[1])
                        {
                                this.draggingRows = [src.data];
                            this.draggingRow = src.data;
                            this.set('cursor', 'move');
                            g.rowdragging = true;
                            this.validRange = {
                                top: g.gridbody.offset().top,
                                bottom: g.gridbody.offset().top + g.gridbody.height(),
                                left: g.grid.offset().left - 10,
                                right: g.grid.offset().left + g.grid.width() + 10
                            };
                        }
                        else
                        {
                            return false;
                        }
                    },
                    onDrag: function (current, e)
                    {
                        var rowdata = this.draggingRow;
                        if (!rowdata) return false;
                        var rows = this.draggingRows ? this.draggingRows : [rowdata];
                        if (g.colresizing) return false;
                        if (g.rowDropIn == null) g.rowDropIn = -1;
                        var pageX = e.pageX;
                        var pageY = e.pageY;
                        var visit = false;
                        var validRange = this.validRange;
                        if (pageX < validRange.left || pageX > validRange.right
                            || pageY > validRange.bottom || pageY < validRange.top)
                        {
                            g.rowDropIn = -1;
                            g.rowDroptip.hide();
                            this.proxy.find(".l-drop-icon:first").removeClass("l-drop-yes l-drop-add").addClass("l-drop-no");
                            return;
                        }
                        for (var i in g.rows)
                        {
                            var rd = g.rows[i];
                            var rowid = rd['__id'];
                            if (rowdata == rd) visit = true;
                            if ($.inArray(rd, rows) != -1) continue;
                            var isAfter = visit ? true : false;
                            if (g.rowDropIn != -1 && g.rowDropIn != rowid) continue;
                            var rowobj = g.getRowObj(rowid);
                            var offset = $(rowobj).offset();
                            var range = {
                                top: offset.top - 4,
                                bottom: offset.top + $(rowobj).height() + 4,
                                left: g.grid.offset().left,
                                right: g.grid.offset().left + g.grid.width()
                            };
                            if (pageX > range.left && pageX < range.right && pageY > range.top && pageY < range.bottom)
                            {
                                var lineTop = offset.top;
                                if (isAfter) lineTop += $(rowobj).height();
                                g.rowDroptip.css({
                                    left: range.left,
                                    top: lineTop,
                                    width: range.right - range.left
                                }).show();
                                g.rowDropIn = rowid;
                                g.rowDropDir = isAfter ? "bottom" : "top";
                                if (p.tree && pageY > range.top + 5 && pageY < range.bottom - 5)
                                {
                                    this.proxy.find(".l-drop-icon:first").removeClass("l-drop-no l-drop-yes").addClass("l-drop-add");
                                    g.rowDroptip.hide();
                                    g.rowDropInParent = true;
                                }
                                else
                                {
                                    this.proxy.find(".l-drop-icon:first").removeClass("l-drop-no l-drop-add").addClass("l-drop-yes");
                                    g.rowDroptip.show();
                                    g.rowDropInParent = false;
                                }
                                break;
                            }
                            else if (g.rowDropIn != -1)
                            {
                                g.rowDropIn = -1;
                                g.rowDropInParent = false;
                                g.rowDroptip.hide();
                                this.proxy.find(".l-drop-icon:first").removeClass("l-drop-yes  l-drop-add").addClass("l-drop-no");
                            }
                        }
                    },
                    onStopDrag: function (current, e)
                    {
                        var rows = this.draggingRows;
                        g.rowdragging = false;
                        for (var i = 0; i < rows.length; i++)
                        {
                            var children = rows[i].children;
                            if (children)
                            {
                                rows = $.grep(rows, function (node, i)
                                {
                                    var isIn = $.inArray(node, children) == -1;
                                    return isIn;
                                });
                            }
                        }
                        if (g.rowDropIn != -1)
                        {
                            if (p.tree)
                            {
                                var neardata, prow;
                                if (g.rowDropInParent)
                                {
                                    prow = g.getRow(g.rowDropIn);
                                }
                                else
                                {
                                    neardata = g.getRow(g.rowDropIn);
                                    prow = g.getParent(neardata);
                                }
                                g.appendRange(rows, prow, neardata, g.rowDropDir != "bottom");
                                g.trigger('rowDragDrop', {
                                    rows: rows,
                                    parent: prow,
                                    near: neardata,
                                    after: g.rowDropDir == "bottom"
                                });
                            }
                            else
                            {
                                g.moveRange(rows, g.rowDropIn, g.rowDropDir == "bottom");
                                g.trigger('rowDragDrop', {
                                    rows: rows,
                                    parent: prow,
                                    near: g.getRow(g.rowDropIn),
                                    after: g.rowDropDir == "bottom"
                                });
                            }

                            g.rowDropIn = -1;
                        }
                        g.rowDroptip.hide();
                        this.set('cursor', 'default');
                    }
                });
            };
</script>
<title>Insert title here</title>
</head>
<body>
	<div id="template.center">
		<div style="height: 2px;"></div>
		<div id="maingrid"></div>
	</div>
</body>
</html>