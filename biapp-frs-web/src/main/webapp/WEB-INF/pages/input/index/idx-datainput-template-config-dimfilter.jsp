<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template9.jsp">
<head>
<title>Insert title here</title>
<script type="text/javascript">
	var tabObj = null;
	var tab2Object = null;
	var canSelect = false;
	var dimFilterInfo=[];
	var flag;
	var type="${type}";
	//初始化
	function clone(obj){
		if(typeof(obj) !='object')return obj;
		 if(obj==null ) return obj;
		 var newobj={};
		 for(var i in obj){
			 newobj[i]=clone(obj[i]);
		 }
		 return newobj;
	}
	$(function() {
		window.parent.dimFilterManager = window;
		initTab();
		if(parent.dimFilterInfo!=null){
			for(var i in parent.dimFilterInfo){
				dimFilterInfo.push(clone(parent.dimFilterInfo[i]));
			}
			flag=true;
		}
		else{
			dimFilterInfo=[];
			flag=false;
		}
		filterFormula=parent.filterFormula;
		function initTab() {
			$("#tab").append('<div tabid="tab1" title="过滤信息" />');
			$("#tab").append('<div tabid="tab2" title="信息确认" />');
			var centerHeight = $("#center").height;
			$("#tab").height(centerHeight-10);
			tabObj = $("#tab").ligerTab({
				contextmenu : false,
				onAfterSelectTabItem : function(tabId) {
					if ($("#tab2frame").attr('src') == ""  || $("#tab2frame").attr('src') == null) {
						loadFrame("tab2", "${ctx}/rpt/input/idxdatainput/dimGrid", "tab2frame");
					}else{
						window.frames["tab2frame"].refreshData();
					}
				}
				/* ,
				onBeforeSelectTabItem : function() {
					return canSelect;
				} */
			});
			loadFrame("tab1", "${ctx}/rpt/input/idxdatainput/idxdim?indexNo=${indexNo}&indexVerId=${indexVerId}", "tab1frame");
		}
	});
	function getfilterFormula(dimNo){
		if(filterFormula!=null){
			var str=filterFormula.match("\\(NOTLIKE\\(\\\\$"+dimNo+"[^ ]+\\)");
			if(str!=null&&str.length>0)
				return str[0];
			else{
				str=filterFormula.match("\\(LIKE\\(\\\\$"+dimNo+"[^ ]+\\)");
				if(str!=null&&str.length>0)
					return str[0];
				else{
					str=filterFormula.match("\\(\\\\$"+dimNo+"[^ ]+\\)");
					if(str!=null&&str.length>0)
						return str[0];
				}
			}
		}
		return "";
	}
	function getFilterInfo(dimTypeNo){
		for(var i in dimFilterInfo){
			if(dimFilterInfo[i].dimNo==dimTypeNo){
				return dimFilterInfo[i];
			}
		}
		return null;
	}
	//上一步
	function last() {
		canSelect = true;
		tabObj.selectTabItem("tab1");
		canSelect = false;
	}

	//下一步
	function next() {
		loadFrame("tab2", "${ctx}/rpt/input/idxdatainput/dimGrid", "tab2frame");
		canSelect = true;
		tabObj.selectTabItem("tab2");
		canSelect = false;
	}
	
	function getDimFilterInfo(){
		var filterFormula="";
		var filterinfos=[];
		for(var i in dimFilterInfo){
			filterinfos.push({
				dimNo: dimFilterInfo[i].dimNo,
				setId: dimFilterInfo[i].setId,
				indexNo: dimFilterInfo[i].indexNo,
				colId: dimFilterInfo[i].colId,
				rptId: dimFilterInfo[i].rptId,
				dimNm: dimFilterInfo[i].dimNm,
				filterMode: dimFilterInfo[i].filterMode,
				filterVal: dimFilterInfo[i].filterVal,
				filterText: dimFilterInfo[i].filterText,
				eVal: dimFilterInfo[i].eVal,
				lVal: dimFilterInfo[i].lVal
			});
			if(dimFilterInfo[i].viewformula!=""){
				if(type==1){
					filterFormula+=dimFilterInfo[i].viewformula;
					filterFormula+=" and ";
				}
				else{
					filterFormula+=dimFilterInfo[i].filterformula;
					filterFormula+=" && ";
				}
				
			}
		}
		if(type==1)
			filterFormula=filterFormula.substring(0,filterFormula.length-5);
		else
			filterFormula=filterFormula.substring(0,filterFormula.length-4);
		var data={
			dimFilterInfo: filterinfos,
			filterFormula: filterFormula
		};
		return data;
	}

	function loadFrame(tabId, src, id) {
		var height = $(document).height() - 33;
		if ($('#' + id).attr('src') && $("#" + id).attr('src') == src) {
			gridInfo.refreshData();
			return;
		}
		var frame = $('<iframe/>');
		frame.attr({
			id : id,
			name: id,
			frameborder : 0,
			src : src
		}).css("height", height);
		$('div[tabId=' + tabId + ']').html(frame);
	}
</script>
</head>
<body>
</body>
</html>