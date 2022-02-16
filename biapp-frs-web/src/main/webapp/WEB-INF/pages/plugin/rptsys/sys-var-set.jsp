<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var customgrid = null;
	var innergrid = null;
	var mainform=null;
	$(function() {
		initForm();
		initTab();
		initBtn();
	});
	function initBtn(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("sysVarSetWin");
			}
		});
		buttons.push({
			text : '保存',
			onclick : f_save
		});

		BIONE.addFormButtons(buttons);
	}
	
	function f_save(){
		window.parent.sysVarInfo=$("#info");
		BIONE.closeDialog("sysVarSetWin");
		/*  $.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frame/rptsys/set/test.json",
			dataType : 'json',
			data : {
				str:$("#info").val()
			},
			type : "POST",
			success : function(result) {
				alert(result.str);
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});  */
		
	}
	function initCustom(){
		var width=$("#info").width();
		customgrid = $("#customgrid").ligerGrid({
			width : width,
			height: '99%',
			columns : [ {
				display : '参数名称',
				name : 'varNm',
				width : "20%"
			}, {
				display : '参数代码',
				name : 'varNo',
				width : "70%"
			}],
			checkbox : false,
			usePager : true,
			isScroll : false,
			rownumbers : true,
			alternatingRow : true, /* 附加奇偶行效果行 */
			colDraggable : true,	/* 是否允许表头拖拽 */
			dataAction : 'server',	/* 从后台获取数据 */
			method : 'post',
			url : '${ctx}/rpt/frame/rptsys/set/list.json?type=custom',
			sortName : 'varNo' /* 第一次默认排序的字段 */
		});
		customgrid.setHeight($("#center").height()-$("#top").height()-40);
		
	}
	function initInner(){
		var width=$("#info").width();
		innergrid = $("#innergrid").ligerGrid({
				width : width,
				height: '99%',
				columns : [ {
					display : '参数名称',
					name : 'varNm',
					width : "20%"
				}, {
					display : '参数代码',
					name : 'varNo',
					width : "70%"
				}],
				checkbox : false,
				usePager : true,
				isScroll : false,
				rownumbers : true,
				alternatingRow : true, /* 附加奇偶行效果行 */
				colDraggable : true,	/* 是否允许表头拖拽 */
				dataAction : 'server',	/* 从后台获取数据 */
				method : 'post',
				url : '${ctx}/rpt/frame/rptsys/set/list.json?type=inner',
				sortName : 'varNo' /* 第一次默认排序的字段 */
			});
		innergrid.setHeight($("#center").height()-$("#top").height()-40);
	}
	function initForm() {
		var width=$("#top").width();
		mainform = $("#mainform").ligerForm({
			fields : [ {
				display : "",
				labelWidth : '0',
				name : "info",
				type : "textarea",
				width : width-100,
				attr : {
					style : "resize:none"
				},
				validate : {
					required : true
				}
			} ]
		});
	}
	function initTab() {
		maintab = $("#tab").ligerTab({
			contextmenu : false,
			onAfterSelectTabItem : function(tabId) {
				if(tabId=="inner"){
					if (innergrid == null) {
						initInner();
						grid2TextAreaDrag(innergrid,$("#info"),2);//
					}
				}
				if (tabId == "custom") {
					if (customgrid == null) {
						initCustom();
						grid2TextAreaDrag(customgrid,$("#info"),1);//
						
					}
				}
			}
		});
		maintab.selectTabItem("inner") ;
	}
	var grid2TextAreaDrag = function(grid,textarea,flag){
		 var g = grid, p = grid.options;
	        g.rowDroptip = $("<div class='l-drag-rowdroptip' style='display:none'></div>").appendTo('body');
	        g.gridbody.add(g.f.gridbody).ligerDrag({ revert: true, animate: false,
	            proxyX: 0, proxyY: 0,
	            proxy: function (draggable, e)
	            {
	                var src = g._getSrcElementByEvent(e);
	                if (src.row)
	                {
	                    var content = draggable.draggingRow ? draggable.draggingRow.varNm : "";
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
	                if (src.rownumberscell || src.detailcell || src.checkboxcell || column )
	                {
	                    if (g.enabledCheckbox())
	                    {
	                        this.draggingRows = g.getSelecteds();
	                        if (!this.draggingRows || !this.draggingRows.length) return false;
	                    }
	                    else
	                    {
	                        this.draggingRows = [src.data];
	                    } 
	                    this.draggingRow = src.data;
	                    g.rowDropIn = -1;
	                    this.set('cursor', 'move');
	                    g.rowdragging = true;
	                    var areaTop = textarea.position().top;
	                    var areaBottom = areaTop + parseInt(textarea.height());
	                    var areaLeft = textarea.position().left;
	                    var areaRight = areaLeft + parseInt(textarea.width());
	                    this.validRange1 = {
	                    	top: areaTop,
	                    	bottom: areaBottom,
	                    	left: areaLeft,
	                    	right: areaRight
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
	                if (g.colresizing) return false;
	                if (g.rowDropIn == null) g.rowDropIn = -1;
	                var pageX = e.pageX;
	                var pageY = e.pageY;
	                var validRange1 = this.validRange1;
	                if ((pageX > validRange1.left) && (pageX < validRange1.right) && (pageY < validRange1.bottom) && (pageY > validRange1.top))
	                   {
	                	//若鼠标滑动的目标区域是tempTitle,允许drop
	                    g.rowDroptip.hide();
	                    this.proxy.find(".l-drop-icon:first").removeClass("l-drop-no l-drop-add").addClass("l-drop-yes");;
	                    return;
	                   }
	                
	                g.rowDropIn = null;
	                g.rowDropInParent = true;
	                g.rowDroptip.hide();
	                this.proxy.find(".l-drop-icon:first").removeClass("l-drop-yes l-drop-add").addClass("l-drop-no");
	                
	            },
	            onStopDrag: function (current, e)
	            {
	            	
	                var rows = this.draggingRows;
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
	                function formatRecord(rows)
	                {
	                    $.each(rows, function ()
	                    {
	                        g.formatRecord(this);
	                        if (this.children)
	                            formatRecord(this.children);
	                    });
	                }
	                g.rowDroptip.hide();
	                g.rowdragging = false;
	                if (flag == 1)  
	                {
	               		//考虑到浏览器兼容问题，目前暂只支持在末尾追加
	               		var curValue = $("#info").attr("value");
	               		var appendValue = "";
	               		for(var j = 0 ; j < rows.length ; j++){
	               			appendValue = appendValue + "$"+rows[j].varNo+"$";
	               		}
	               		$("#info").attr("value",curValue+appendValue);
	                }
	                if (flag == 2 ) 
	                {
	               		//考虑到浏览器兼容问题，目前暂只支持在末尾追加
	               		var curValue = $("#info").attr("value");
	               		var appendValue = "";
	               		for(var j = 0 ; j < rows.length ; j++){
	               			appendValue = appendValue + "#"+rows[j].varNo+"#";
	               		}
	               		$("#info").attr("value",curValue+appendValue);
	                }
	               /*  if (g.rowDropIn == 2)  //给模板文本赋值
	                {
	               		//考虑到浏览器兼容问题，目前暂只支持在末尾追加
	               		var curValue = $("#templateText").attr("value");
	               		var appendValue = "";
	               		for(var j = 0 ; j < rows.length ; j++){
	               			appendValue = appendValue + "{%"+rows[j].id.paramCode+"%}";
	               		}
	               		$("#templateText").attr("value",curValue+appendValue);
	                } */
	                g.rowDroptip.hide();
	                this.set('cursor', 'default');
	            }
	        });
	};
	
</script>
</head>
<body>
	<div id="template.center">
		<div id="top" style="overflow: hidden;">
			<form id="mainform"></form>
		</div>
		<div id="middle">
			<div id="tab" style="width: 99%; overflow: hidden;margin-left:10px;">
				<div tabid="inner" title="内置变量" lselected="true">
					<div id="innergrid"></div>
				</div>
				<div tabid="custom" title="自定义变量" lselected="true">
					<div id="customgrid"></div>
				</div>
			</div>
		</div>

	</div>
</body>
</html>