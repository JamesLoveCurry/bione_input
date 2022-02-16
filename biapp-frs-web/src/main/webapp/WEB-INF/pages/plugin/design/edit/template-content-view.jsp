<!-- 报表设计器 - 单元格明细内容模板 -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" %>
<script type="text/javascript"
	src="${ctx}/js/backbone/jquery.tmpl.min.js"></script>
	
<script type="text/javascript">
	// 使用$(selector).tpl(data).appentTo(selector); 的方式使用模板，避免el表达式和tmpl参数的冲突
	(function($){
	    $.fn.tpl = function(data){
	        $.template('template', $(this).html().replace(/@/g,"$"));
	        return $.tmpl('template', data);
	    }
	})($)
	function detail(cellNm,orgNo,dataDate,checkId,validDesc) {
        parent.BIONE.commonOpenDialog("指标:"+cellNm, "validDescWin", "700", "300", "${ctx}/rpt/frs/rptfill/addWarnValidDesc?checkId=" + checkId +"&dataDate=" + dataDate +"&orgNo=" + orgNo +"&validDesc=" + validDesc, null);
    }
    function childDetail(cellNm,orgNo,dataDate,checkId,validDesc,indexNo) {
        parent.BIONE.commonOpenDialog("下级机构异动说明", "validDescWin", "700", "350", "${ctx}/rpt/frs/rptfill/getChildOrgWarnValidDesc?checkId=" + checkId +"&dataDate=" + dataDate +"&orgNo=" + orgNo +"&validDesc=" + validDesc +"&indexNo=" + indexNo, null);
    }
</script>

<!-- 基于ligerui 样式的普通版本 -->
<script id="template-content" type="text/x-jquery-tmpl" charset="utf-8">
		<table cellpadding="0" cellspacing="0" class="l-table-edit" >
            		<tr>
                		<td align="right" class="l-table-edit-td ">单元格类型:</td>
                		<td align="left" class="l-table-edit-td "><input class="own-formitem-style" name="cellTypeNm" data-bind="value: cellTypeNm" type="text" id="cellTypeNm" ltype="text"  readonly=true  disabled="disabled"/></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='01'  || cellType() == '-1'? 'none' : ''}">
                		<td align="right" class="l-table-edit-td ">单元格编号:</td>
                			<td align="left" class="l-table-edit-td "><input class="own-formitem-style" name="cellNo"  type="text" id="cellNo" ltype="text"  readonly=true  disabled="disabled"/></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='01' || cellType() == '-1'? 'none' : ''}">
                		<td align="right" class="l-table-edit-td">单元格名称:</td>
                			<td align="left" class="l-table-edit-td"><input onchange="cellNmChangeHandler()" class="own-formitem-style {"required":true}" name="cellNm" data-bind="value:cellNm,valueUpdate:'afterkeydown'" type="text" id="cellNm" ltype="text"  disabled="disabled"/></td>
               			<td align="left">
							<span><font color="red">*</font></span>
						</td>
            		</tr>
					<tr data-bind="style:{ display : cellType()=='03' || cellType()=='04' || cellType()=='05'? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">报表指标号:</td>
                			<td align="left" class="l-table-edit-td "><input class="own-formitem-style" name="realIndexNo"  data-bind="value:realIndexNo,valueUpdate:'afterkeydown'"  type="text" id="realIndexNo" ltype="text"  readonly=true  disabled="disabled"/></td>
               			 <td align="left"></td>
            		</tr>
            		<!--<tr data-bind="style:{ display : cellType()=='01' ? 'none' : ''}">-->
					<tr data-bind="style:{ display : cellType()=='03' || cellType()=='04' || cellType()=='05'? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">人行编码:</td>
                			<td align="left" class="l-table-edit-td"><input onchange="busiNoChangeHandler()" class="own-formitem-style" name="busiNo" data-bind="value:busiNo,valueUpdate:'afterkeydown'" type="text" id="busiNo" ltype="text"  disabled="disabled"/></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='02' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">数据模型:</td>
                			<td align="left" class="l-table-edit-td "><input class="own-formitem-style" name="dsName"  data-bind="value:dsName,valueUpdate:'afterkeydown'"  type="text" id="dsName" ltype="text"  readonly=true  disabled="disabled"/></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='02' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">字段名称:</td>
                			<td align="left" class="l-table-edit-td "><input class="own-formitem-style" name="columnName"  data-bind="value:columnName,valueUpdate:'afterkeydown'"  type="text" id="columnName" ltype="text"  readonly=true  disabled="disabled"/></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='03' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">来源指标:</td>
                			<td align="left" class="l-table-edit-td "><input class="own-formitem-style" name="indexNm"  data-bind="value:indexNm,valueUpdate:'afterkeydown'"  type="text" id="indexNm" ltype="text"  readonly=true  disabled="disabled"/></td>
               			 <td align="left"></td>
            		</tr>
            		<!-- <tr data-bind="style:{ display : cellType()=='03' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">是否减维:</td>
                			<td align="left" class="l-table-edit-td "><input class="own-formitem-style" name="isSubDim"  data-bind="value:allDims() == factDims()? '否' : '是'"  type="text" id="isSubDim" ltype="text"  readonly=true  disabled="disabled"/></td>
               			 <td align="left"></td>
            		</tr> -->
            		<tr data-bind="style:{ display : cellType()=='03'  && (indexNo() != '' && indexNo() != null) ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">是否过滤:</td>
                			<td align="left" class="l-table-edit-td "><input class="own-formitem-style" name="isFiltDim"  data-bind="value:filtInfos() == null  || (typeof filtInfos() == 'string' && filtInfos().length <= 2) || (typeof filtInfos() == 'object' && filtInfos().length <= 0)?  '否' : '是'"  type="text" id="isFiltDim" ltype="text"  readonly=true  disabled="disabled"/></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='04' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">公式内容:</td>
                		<td align="left" class="l-table-edit-td"> 
                			<textarea  data-bind="value:excelFormula,valueUpdate:'afterkeydown'"class="l-textarea own-formitem-style" style="height:60px;resize:none;" readonly=true disabled="disabled"></textarea>
                		</td>
                		<td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='04'  && templateType != '01')? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否报表指标:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isRptIndex" id="isRptIndex" ltype="select" data-bind="value:isRptIndex" disabled="disabled">
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : (templateType!='02' && cellType()=='04') ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否扩展:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isAnalyseExt" id="isAnalyseExt" ltype="select" data-bind="value:isAnalyseExt" disabled="disabled">
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
					<tr data-bind="style:{ display : (templateType!='02' && cellType()=='04' && isAnalyseExt()=='Y') ? '' : 'none'}">
						<td align="right" class="l-table-edit-td" valign="top">扩展方式:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="analyseExtType" id="analyseExtType" ltype="select" data-bind="value:analyseExtType" disabled="disabled">
								<option value="01">范围扩展(纵)</option>
								<option value="02">自增扩展(纵)</option>
								<option value="03">范围扩展(横)</option>
								<option value="04">自增扩展(横)</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='05' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">表间计算:</td>
                		<td align="left" class="l-table-edit-td"> 
                			<textarea  data-bind="value:formulaDesc,valueUpdate:'afterkeydown'"class="l-textarea own-formitem-style" style="height:60px;resize:none;" readonly=true disabled="disabled"></textarea>
                		</td>
                		<td align="left"></td>
            		</tr>
            		<!-- <tr data-bind="style:{ display : cellType()=='01' || cellType()=='06' ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td" valign="top">数据类型:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="dataType" id="dataType" ltype="select"  data-bind="value:dataType" disabled="disabled">  
								<option value="01">数值</option>
								<option value="02">字符</option>
							</select>
                		</td>
            		</tr> -->
            		<tr data-bind="style:{ display : (cellType()=='01' || cellType()=='06' ) ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td" valign="top">显示格式:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="displayFormat" id="displayFormat" ltype="select" data-bind="value:displayFormat" disabled="disabled">
								<option value="01">金额</option>
								<option value="02">百分比</option>
								<option value="03">数值</option>
								<option value="04">文本</option>
								<!-- <option value="08">百分比(无%)</option>-->
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='01' || cellType()=='06'|| displayFormat()=='02' || displayFormat()=='08'|| displayFormat()=='04'|| displayFormat()=='05') ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td" valign="top">数据单位:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="dataUnit" id="dataUnit" ltype="select" data-bind="value:dataUnit" disabled="disabled">
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
                			<td align="left" class="l-table-edit-td"><input class="own-formitem-style" name="dataLen" data-bind="value:dataLen,valueUpdate:'afterkeydown'" type="text" id="dataLen" ltype="text" disabled="disabled"/></td>
               			 <td align="left"></td>
            		</tr>
            		 -->
            		<tr data-bind="style:{ display : (cellType()=='01' || cellType()=='06' || displayFormat()=='03' || displayFormat()=='04'|| displayFormat()=='05') ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td">数据精度:</td>
                			<td align="left" class="l-table-edit-td"><input class="own-formitem-style" name="dataPrecision"  data-bind="value:dataPrecision,valueUpdate:'afterkeydown'"  type="text" id="dataPrecision" ltype="text" disabled="disabled"/></td>
               			 <td align="left"></td>
            		</tr>
					<!-- 单元格属性 -->
            		<tr data-bind="style:{display : (cellType()=='01') || (cellType() == '-1' && _typeIdBatch())  ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">单元格属性:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="typeId" id="typeId" ltype="select" data-bind="value:typeId" disabled="disabled">
								{{each(i , comcellType) template.comcellTypes}}
									<!-- 显示缺省类型的计算规则；当前来源指标未定义统计类型，显示全部计算规则；定义有统计类型的指标，显示对应的计算规则 -->
									<option value="@{comcellType.typeId}">@{comcellType.typeNm}</option>
								{{/each}}
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
					<tr data-bind="style:{ display : ( cellType()=='01' && typeId()!='00')  ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">内容:</td>
                			<td align="left" class="l-table-edit-td">
								<textarea data-bind="value:content,valueUpdate:'afterkeydown'"class="l-textarea own-formitem-style" style="height:60px;resize:none;" disabled="disabled"></textarea>
							</td>
               			 <td align="left"></td>
            		</tr>
            		<!-- 计算规则 -->
            		<tr data-bind="style:{display : (cellType()=='07' || cellType()=='03') || (cellType()=='-1' && typeof _ruleIdBatch == 'function' && _ruleIdBatch() != false) ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">计算规则:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="ruleId" id="ruleId" ltype="select" data-bind="value:ruleId" disabled="disabled">
								{{each(i , calcRule) template.calcRules}}
									<!-- 显示缺省类型的计算规则；当前来源指标未定义统计类型，显示全部计算规则；定义有统计类型的指标，显示对应的计算规则 -->
									<option data-bind="style:{display : (statType()=='' || statType()==@{calcRule.ruleType} || @{calcRule.ruleType} == '01') ? '' : 'none'}" value="@{calcRule.ruleId}">@{calcRule.ruleNm}</option>
								{{/each}}
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
					<!-- 时间度量 -->
            		<tr data-bind="style:{display :(cellType()=='07' || cellType()=='03') || (cellType()=='-1' && typeof _timeMeasureIdBatch == 'function' && _timeMeasureIdBatch() != false) ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">时间度量:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="timeMeasureId" id="timeMeasureId" ltype="select" data-bind="value:timeMeasureId" disabled="disabled">
								{{each(i , timeMeasure) template.timeMeasures}}
									<option value="@{timeMeasure.timeMeasureId}">@{timeMeasure.measureNm}</option>
								{{/each}}
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
					<!-- 取值方式 -->
            		<tr data-bind="style:{display : (cellType()=='07' || cellType()=='03') || (cellType()=='-1' && typeof _modeIdBatch == 'function' && _modeIdBatch() != false)? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">取值方式:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="modeId" id="modeId" ltype="select" data-bind="value:modeId" disabled="disabled">
								{{each(i , valType) template.valTypes}}
									<option value="@{valType.modeId}">@{valType.modeNm}</option>
								{{/each}}
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='02' ) || (cellType() == '03') || (cellType() == '05') ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否可修改:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isUpt" id="isUpt" ltype="select" data-bind="value:isUpt"  disabled="disabled">
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
					<tr data-bind="style:{ display : (cellType() == '03' && templateType != '01') ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否跑数汇总:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isSum" id="isSum" ltype="select" data-bind="value:isSum" disabled="disabled">
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
					<tr data-bind="style:{ display : (cellType() == '03' && templateType != '01') ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否填报汇总:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isFillSum" id="isFillSum" ltype="select" data-bind="value:isFillSum" disabled="disabled">
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='02' ) || (cellType() == '03' && templateType != '03') || (cellType()=='05' )? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否可空:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isNull" id="isNull" ltype="select" data-bind="value:isNull" disabled="disabled">
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
					<tr data-bind="style:{ display : (cellType() == '03' && templateType != '01') ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否锁定:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isLock" id="isLock" ltype="select" data-bind="value:isLock" disabled="disabled">
								<option value="N">否</option>
								<option value="Y">是</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
					<tr data-bind="style:{ display : cellType()=='02' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否合并:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isMerge" id="isMerge" ltype="select" data-bind="value:isMerge" disabled="disabled">
								<option value="N">否</option>
								<option value="Y">是</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
					<tr data-bind="style:{ display : cellType()=='02' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否合并参照列:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isMergeCol" id="isMergeCol" ltype="select" data-bind="value:isMergeCol" disabled="disabled">
								<option value="N">否</option>
								<option value="Y">是</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            	    <tr data-bind="style:{ display : cellType()=='02' || cellType()=='03' || cellType()=='04' || cellType()=='05' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">业务口径:</td>
                		<td align="left" class="l-table-edit-td"> 
                			<textarea data-bind="value:caliberExplain,valueUpdate:'afterkeydown'"class="l-textarea own-formitem-style" style="height:60px;resize:none;" disabled="disabled"></textarea>
                		</td>
                		<td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='02' || cellType()=='03' || cellType()=='04' || cellType()=='05' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">技术口径:</td>
                		<td align="left" class="l-table-edit-td"> 
                			<textarea data-bind="value:caliberTechnology,valueUpdate:'afterkeydown'"class="l-textarea own-formitem-style" style="height:60px;resize:none;" disabled="disabled"></textarea>
                		</td>
                		<td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='02' || cellType()=='03' || cellType()=='04' || cellType()=='05' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">备注说明:</td>
                		<td align="left" class="l-table-edit-td"> 
                			<textarea data-bind="value:remark,valueUpdate:'afterkeydown'"class="l-textarea own-formitem-style" style="height:60px;resize:none;" disabled="disabled"></textarea>
                		</td>
                		<td align="left"></td>
            		</tr>
            	</table>
</script>