<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<%@ include file="/common/zTree_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/template/template14.css" />
<style type="text/css">
.searchtitle {
 	margin: auto; 
 	display: inline-block; 
 	*display: inline; 
 	zoom: 1; 
 	width: 100%;
}

.searchtitle .togglebtn {
	position: absolute;
	background: url("../../../images/classics/ligerui/toggle.gif") no-repeat 0px 0px;
	top: 5px;
	width: 9px;
	height: 10px;
	right: 50px;
	cursor: pointer;
	font-size: 0;
	margin: 0;
	padding: 0;
	border: 0;
}

.searchtitle .togglebtn-down {
	background-position: 0px -10px;
}

.searchbox {
	width: 100%;
	text-align: center;
	margin: auto;
	border-style: solid;
	border-width: 1px;
	border-color: #D6D6D6;
}

.searchbutten{
	border: 1px solid transparent; 
	border-radius: 10px;
	border-color: #ddd;
	float: left;
	position: relative;
	padding-top: 1px;
    padding-bottom: 1px;
    padding-right: 10px;
    padding-left: 10px;
    margin-right: 7px;
}

.buttentitle{
	font-family: '微软雅黑',Arial;
	color: #23c6c8;
	font-size: 16px;
}
.oper-div{
	float: left;
    border-right: 2px solid #c5c5c5;
    height: 80px;
    padding: 10px;
    margin: 5px 0px;
}
.oper-font{
	padding: 5px 0px;
    text-align: center;
    color: #8a8a8a;
}
img{
    padding: 0 8px;
}
.l-tab-content-item {
    position: absolute;
    height: 160px;
}
.search-all, .search-all > input[type="text"]:focus{
	height: 24px;
    border: 2px solid #cdcdcd;
    border-radius: 10px;
    outline: 0;
    max-width: 200px;
}
.div-desc{
	clear: both; text-align: center; color: #8a8a8a; padding-top: 10px;
}
.rpt-config{
	float: left;
    margin: 10px 5px 0 5px;
    color: #3c8dbc;
    cursor: pointer;
}
.rpt-config2{
	float: left;
    margin: 10px 5px 0 5px;
    color: #8a8a8a;
    cursor: pointer;
}
 .l-table-edit-td{ 
 	padding:4px;
 }
 .l-button-submit,.l-button-reset{
 	width:80px; 
 	float:left; 
 	margin-left:10px; 
 	padding-bottom:2px;
 }
 .l-verify-tip{ 
 	left:230px; 
 	top:120px;
 }
 .own-formitem-style{
 	width:108px;
 }
 .span-title{
    margin: -20px 10px 0 0;
    color: #3c8dbc;
    font-weight: bold;
    cursor: pointer;
    display: inline-block;
    position: absolute;
    z-index: 999;
    right: 0;
 }
 .span-title > i{
 	background-image: none;
 }
</style>
<script type="text/javascript">
	
	$(function() {
		templateshow();
	});
	
	function templateshow(){
		var $content = $(window);
		var height = $content.height();
		$("#center").height(height);
		$("#designLayout").height($(window).height() - $("#maintab").height() - 10);
	}
	
	//true 一次  false 二次
 	function templateinit(taskType, flag) {}
</script>
<sitemesh:write property='head' />
</head>
<body>
	<div id="center">
		<div id="maintab" style="height:140px;">
			<span class="span-title" id="showOrHide">
	           	<i class="icon icon-up"></i><!-- icon-arrow-down -->
			</span>
		</div>
		<div id="spread" style="width:99.4%;border:1px solid #D0D0D0;margin: 1.5px;display: block;"></div>
		<div id="designLayout" style="width : 99%;display: none;">
			<div id="centerDiv" position="center">
            	<div id="spreadConfig" style="border:1px solid #D0D0D0;"></div>
            </div>
            <div id="rightDiv" position="right" title="单元格信息">
            	<form id="rightCellForm" action=""  class="l-form">
            	<table cellpadding="0" cellspacing="0" class="l-table-edit" >
            		<tr>
                		<td align="right" class="l-table-edit-td ">单元格类型:</td>
                		<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="cellTypeNm" data-bind="value: cellTypeNm" type="text" id="cellTypeNm" ltype="text"  readonly=true /></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='01'  || cellType() == '-1'? 'none' : ''}">
                		<td align="right" class="l-table-edit-td ">单元格编号:</td>
                			<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="cellNo"  type="text" id="cellNo" ltype="text"  readonly=true /></td>
               			 <td align="left"></td>
            		</tr>
            		<tr>
                		<td align="right" class="l-table-edit-td">单元格名称:</td>
                			<td align="left" class="l-table-edit-td"><input onchange="cellNmChangeHandler()" class="own-formitem-style" name="cellNm" data-bind="value:cellNm,valueUpdate:'afterkeydown'" type="text" id="cellNm" ltype="text" /></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='01' ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td">业务编号:</td>
                			<td align="left" class="l-table-edit-td"><input onchange="busiNoChangeHandler()" class="own-formitem-style" name="busiNo" data-bind="value:busiNo,valueUpdate:'afterkeydown'" type="text" id="busiNo" ltype="text" /></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='02' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">数据模型:</td>
                			<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="dsName"  data-bind="value:dsName,valueUpdate:'afterkeydown'"  type="text" id="dsName" ltype="text"  readonly=true/></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='02' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">字段名称:</td>
                			<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="columnName"  data-bind="value:columnName,valueUpdate:'afterkeydown'"  type="text" id="columnName" ltype="text"  readonly=true/></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='03'  && (indexNo() != '' && indexNo() != null) ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">来源指标:</td>
                			<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="indexNm"  data-bind="value:indexNm,valueUpdate:'afterkeydown'"  type="text" id="indexNm" ltype="text"  readonly=true/></td>
               			 <td align="left"></td>
            		</tr>
            		<!-- <tr data-bind="style:{ display : cellType()=='03' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">是否减维:</td>
                			<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="isSubDim"  data-bind="value:allDims() == factDims()? '否' : '是'"  type="text" id="isSubDim" ltype="text"  readonly=true/></td>
               			 <td align="left"></td>
            		</tr> -->
            		<tr data-bind="style:{ display : cellType()=='03'  && (indexNo() != '' && indexNo() != null) ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">是否过滤:</td>
                			<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="isFiltDim"  data-bind="value:filtInfos() == null  || (typeof filtInfos() == 'string' && filtInfos().length <= 2) || (typeof filtInfos() == 'object' && filtInfos().length <= 0)?  '否' : '是'"  type="text" id="isFiltDim" ltype="text"  readonly=true/></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='04' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">公式内容:</td>
                		<td align="left" class="l-table-edit-td"> 
                			<textarea  data-bind="value:excelFormula,valueUpdate:'afterkeydown'"class="l-textarea own-formitem-style" style="height:60px;resize:none;" readonly=true></textarea>
                		</td>
                		<td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='04'  && templateType != '01')? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否报表指标:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isRptIndex" id="isRptIndex" ltype="select" data-bind="value:isRptIndex" >
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='04' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">分析扩展:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isAnalyseExt" id="isAnalyseExt" ltype="select" data-bind="value:isAnalyseExt" >
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='05' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">表间计算:</td>
                		<td align="left" class="l-table-edit-td"> 
                			<textarea  data-bind="value:formulaDesc,valueUpdate:'afterkeydown'"class="l-textarea own-formitem-style" style="height:60px;resize:none;" readonly=true></textarea>
                		</td>
                		<td align="left"></td>
            		</tr>
            		<!-- <tr data-bind="style:{ display : cellType()=='01' || cellType()=='06' ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td" valign="top">数据类型:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="dataType" id="dataType" ltype="select"  data-bind="value:dataType">
								<option value="01">数值</option>
								<option value="02">字符</option>
							</select>
                		</td>
            		</tr> -->
            		<tr data-bind="style:{ display : (cellType()=='01' || cellType()=='06' ) ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td" valign="top">显示格式:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="displayFormat" id="displayFormat" ltype="select" data-bind="value:displayFormat" >
								<option value="01">原格式</option>
								<option value="02">百分比</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='01' || cellType()=='06' || displayFormat()=='02') ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td" valign="top">数据单位:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="dataUnit" id="dataUnit" ltype="select" data-bind="value:dataUnit" >
                				<option value="">使用模板单位</option>
								<option value="01">元</option>
								<option value="02">百</option>
								<option value="03">千</option>
								<option value="04">万</option>
								<option value="05">亿</option>
								<option value="00">无单位</option>
								<option value="-1" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<!-- 
            		<tr data-bind="style:{ display : (cellType()=='01' || cellType()=='06' )? 'none' : ''}">
                		<td align="right" class="l-table-edit-td">数据长度:</td>
                			<td align="left" class="l-table-edit-td"><input class="own-formitem-style" name="dataLen" data-bind="value:dataLen,valueUpdate:'afterkeydown'" type="text" id="dataLen" ltype="text" /></td>
               			 <td align="left"></td>
            		</tr>
            		 -->
            		<tr data-bind="style:{ display : (cellType()=='01' || cellType()=='06' || displayFormat()=='02') ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td">显示精度:</td>
                			<td align="left" class="l-table-edit-td"><input class="own-formitem-style" name="dataPrecision"  data-bind="value:dataPrecision,valueUpdate:'afterkeydown'"  type="text" id="dataPrecision" ltype="text" /></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='02' ) || (cellType() == '03' && templateType != '03') || (cellType()=='05' )? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否可修改:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isUpt" id="isUpt" ltype="select" data-bind="value:isUpt" >
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='02' ) || (cellType() == '03' && templateType != '03') || (cellType()=='05' )? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否可空:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isNull" id="isNull" ltype="select" data-bind="value:isNull" >
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='01' || cellType()=='-1'? 'none' : ''}">
                		<td align="right" class="l-table-edit-td">口径说明:</td>
                		<td align="left" class="l-table-edit-td"> 
                			<textarea data-bind="value:caliberExplain,valueUpdate:'afterkeydown'"class="l-textarea own-formitem-style" style="height:60px;resize:none;"></textarea>
                		</td>
                		<td align="left"></td>
            		</tr>
            	</table>
            	</form>
            </div>
		</div>
	</div>
	<div id="pagination"></div>
</body>
</html>
