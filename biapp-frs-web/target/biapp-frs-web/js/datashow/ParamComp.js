'use strict';

(function($) {
			var Uuid = uuid;
 			var ParamTmp = window.ParamTmp = {};

			var _lineSize = 2; // 一行两个组件

			ParamTmp.component = [];

			// 01
			ParamTmp.component["01"] = function(key, text,
					orderNo, dimTypeNo, dimTypeStruct, paramid, required,
					daterange,checkbox,defValue) {
				// 文本框
				var uuid = paramid ? paramid : Uuid.v1();
				var attrObj = {
					datasource : '{"options":{"data":[{"id":"=","text":"精确查询"},{"id":"like","text":"模糊查询"}]}}', // 同下拉框
					display : text ? text : "文本框", // 同上
					id : uuid, // 同上
					inputName : Uuid.v1(), // 组合控件中输入框的id，在所有控件中必须唯一
					isMultiSelect : "false", // 同下拉框的属性
					key : key, // 同上
					name : uuid, // 同上
					newline : orderNo % _lineSize == 0 ? "true" : "false", // 同上
					type : "dblin", // 类型为组合控件
					value : defValue ? defValue : "",
					validate : '{"required":' + (required ? required : "false")
							+ ',"number":false}'// 输入框的验证，同文本框
				}
				return attrObj;
			};

			// 02
			ParamTmp.component["02"] = function(key, text,
					orderNo, dimTypeNo, dimTypeStruct, paramid, required,
					daterange,checkbox,defValue) {
				// 数字
				var uuid = paramid ? paramid : Uuid.v1();
				var attrObj = {
					datasource : '{"options":{"data":[{"id":"=","text":"="},{"id":"<","text":"<"},{"id":">","text":">"},{"id":"<>","text":"<>"},{"id":"<=","text":"<="},{"id":">=","text":">="}]}}', // 同下拉框
					display : text ? text : "数字框", // 同上
					id : uuid, // 同上
					inputName : Uuid.v1(), // 组合控件中输入框的id，在所有控件中必须唯一
					isMultiSelect : "false", // 同下拉框的属性
					key : key, // 同上
					name : uuid, // 同上
					newline : orderNo % _lineSize == 0 ? "true" : "false", // 同上
					type : "dblin", // 类型为组合控件
					value : defValue ? defValue : "",
					validate : '{"required":' + (required ? required : "false")
							+ ',"number":true}'// 输入框的验证，同文本框
				}
				return attrObj;
			};

			// 03
			ParamTmp.component["03"] = function(key,
					text, orderNo, dimTypeNo, dimTypeStruct, paramid, required,
					daterange,checkbox,defValue,isConver) {
				// 单选下拉框
				var uuid = paramid ? paramid : Uuid.v1();
				var dsUrl = '{"options":{"url":"/report/frame/design/paramtmp/getTreeDimItems?dimTypeNo='
						+ (dimTypeNo ? dimTypeNo : key)+'&isConver='+isConver
						+ '","ajaxType":"post"}}';
				if (dimTypeStruct === "02") {
					var attrObj = {
						checkbox : checkbox ? checkbox : "false", // 是否多选
						datasource : dsUrl, // 同下拉框
						display : text ? text : "弹出窗", // 同上
						id : uuid, // 同上
						key : key, // 同上
						name : uuid, // 同上
						newline : orderNo % _lineSize == 0 ? "true" : "false", // 同上
						required : required ? required : "false", // 同上
						type : "popup", // 类型为弹出树
						value : defValue ? defValue : "",
						dialogWidth : "300",
						dialogHeight : "400"
					}
				} else {
					var dataFilter = "true";
					if(checkbox){
						if(checkbox == "true")
							dataFilter = "false";
						else
							dataFilter = "true";
					}
					var attrObj = {
						datasource : dsUrl,// 数据源配置，ajax的url和类型
						display : text ? text : "下拉框", // 同上
						id : uuid, // 同上
						isMultiSelect : checkbox ? checkbox : "false", // 是否多选，"true", "false"
						tree_checkbox : "false",
						key : key, // 同上
						name : uuid, // 同上
						dataFilter : dataFilter,
						newline : orderNo % _lineSize == 0 ? "true" : "false", // 同上
						required : (required ? required : "false"), // 同上
						value : defValue ? defValue : "",
						type : "select" // 类型为下拉框 select/combobox
					}

				}
				return attrObj;

			};

			// 04
			ParamTmp.component["04"] = function(key,
					text, orderNo, dimTypeNo, dimTypeStruct, paramid, required,
					daterange,checkbox,defValue,isConver,dataJson,busiType) {
				// 弹出框
				var uuid = paramid ? paramid : Uuid.v1();
				var attrObj = {
					checkbox : checkbox? checkbox:"true", // 是否多选
							datasource : '{"options":{"url":"/report/frame/datashow/idx/orgTree","ajaxType":"post","busiType":"'+busiType+'" }}', // 同下拉框
					display : text ? text : "弹出窗", // 同上
					id : uuid, // 同上
					key : key, // 同上
					name : uuid, // 同上
					newline : orderNo % _lineSize == 0 ? "true" : "false", // 同上
					required : required ? required : "false", // 同上
					type : "popup", // 类型为弹出树
					value : defValue ? defValue : "",
					dialogWidth : "300",
					dialogHeight : "400"
				}
				return attrObj;
			};

			// 05
			ParamTmp.component["05"] = function(
					key, text, orderNo, dimTypeNo, dimTypeStruct, paramid,
					required, daterange,checkbox,defValue) {
				// 单日历组件
				var uuid = paramid ? paramid : Uuid.v1();
				var attrObj = {
					display : text ? text : '日期',// 同上
					id : uuid,// 同上
					key : key,// 同上
					name : uuid,// 同上
					newline : orderNo % _lineSize == 0 ? "true" : "false",// 同上
					required : required ? required : "false",// 同上，是否必填
																// "null","true","false"
																// 其中 "null" =
																// "false"
					showTime : "false",// 是否显示时间
					value : defValue ? defValue : "",
					type : "date" // 类型为日期框
						
				}
				return attrObj;
			};

			// 06
			ParamTmp.component["06"] = function(
					key, text, orderNo, dimTypeNo, dimTypeStruct, paramid,
					required, daterange,checkbox,defValue) {
				// 双日历组件
				var uuid = paramid ? paramid : Uuid.v1();
				var attrObj = {
					display : text ? text : "日期区间", // 同上
					startDateId : Uuid.v1(), // 时间段开始时间存放的隐藏域的ID，在所有控件中必须唯一
					endDateId : Uuid.v1(), // 时间段结束时间存放的隐藏域的ID，在所有控件中必须唯一
					id : uuid, // 同上
					key : key, // 同上
					name : uuid, // 同上
					newline : "false", // 同上
					value : defValue ? defValue : "",
					required : (required ? required : "false"), // ? required :
																// "false",
																// //是否必填
					dayRangeMax : daterange ? daterange : "0",
					type : "daterange" // 类型为日期区间
				}
				return attrObj;
			};

			// 07
			ParamTmp.component["07"] = function(
					key, text, orderNo, dimTypeNo, dimTypeStruct, paramid,
					required, daterange,checkbox,defValue) {
				// 复选下拉框
				var uuid = paramid ? paramid : Uuid.v1();
				var dsUrl = '{"options":{"url":"/report/frame/design/paramtmp/getTreeDimItems?dimTypeNo='
						+ (dimTypeNo ? dimTypeNo : key)
						+ '","ajaxType":"post"}}';
				if (dimTypeStruct === Constants.DIM_TYPE_STRUCT_TREE) {
					var attrObj = {
						checkbox : "true", // 是否多选
						datasource : dsUrl, // 同下拉框
						display : text ? text : "弹出窗", // 同上
						id : uuid, // 同上
						key : key, // 同上
						name : uuid, // 同上
						newline : orderNo % _lineSize == 0 ? "true" : "false", // 同上
						required : required ? required : "false", // 同上
						type : "popup", // 类型为弹出树
						value : defValue ? defValue : "",
						dialogWidth : "300",
						dialogHeight : "400"
					}
				} else {
					var attrObj = {
						datasource : dsUrl,// 数据源配置，ajax的url和类型
						display : text ? text : "下拉框", // 同上
						id : uuid, // 同上
						isMultiSelect : "true", // 是否多选，"true", "false"
						tree_checkbox : "true",
						dataFilter : "true",
						key : key, // 同上
						name : uuid, // 同上
						value : defValue ? defValue : "",
						newline : orderNo % _lineSize == 0 ? "true" : "false", // 同上
						required : (required ? required : "false"), // 同上
						type : "select" // 类型为下拉框 select/combobox
					}
				}
				return attrObj;
			};

			/**
			 * edit by fangjuan 20151022 数值区间
			 */
			// 08
			ParamTmp.component["08"] = function(key,
					text, orderNo, dimTypeNo, dimTypeStruct, paramid, required,
					daterange,checkbox,defValue) {
				var uuid = paramid ? paramid : Uuid.v1();
				var attrObj = {
					display : text ? text : "数值区间", // 同上
					id : paramid, // 同上
					inputName : Uuid.v1(), // 组合控件中输入框的id，在所有控件中必须唯一
					key : key, // 同上
					name : paramid, // 同上
					newline : orderNo % _lineSize == 0 ? "true" : "false", // 同上
					type : "textrange", // 类型为组合控件
					value : defValue ? defValue : "",
					validate : '{"required":' + (required ? required : "false")
							+ ',"number":true}'// 输入框的验证，同文本框
				}
				return attrObj;
			};
			
			ParamTmp.component["09"] = function(key,
					text, orderNo, dimTypeNo, dimTypeStruct, paramid, required,
					daterange,checkbox,defValue,isConver,data) {
				var source = {
					options : {
						data : JSON2.parse(data)
					}
				};
				var uuid = paramid ? paramid : Uuid.v1();
				var attrObj = {
						datasource : JSON2.stringify(source),// 数据源配置，ajax的url和类型
						display : text ? text : "下拉框", // 同上
						id : uuid, // 同上
						isMultiSelect : "false", // 是否多选，"true", "false"
						tree_checkbox : "false",
						key : key, // 同上
						name : uuid, // 同上
						dataFilter : "false",
						newline : orderNo % _lineSize == 0 ? "true" : "false", // 同上
						required : (required ? required : "false"), // 同上
						value : defValue ? defValue : "",
						type : "select" // 类型为下拉框 select/combobox
				}
				return attrObj;
			};

			/**
			 * 生成查询维度对应的模板对象
			 * 
			 * @param dimObj
			 *            {dimTypeNo , dimTypeNm , dimTypeStruct ,orderNo ...}
			 */
			ParamTmp.generateDimTmp = function(dimObj) {
				var objTmp = {};
				if (!dimObj && typeof dimObj != "object") {
					return objTmp;
				}
				switch (dimObj.dimTypeNo) {
				case Constants.DIM_TYPE_DATE_RANGE:
					// 双日历
					objTmp = ParamTmp.component[Constants.PARAM_COMP_DATE_DOUBLE]
							("DATE", "数据日期", dimObj.orderNo, null, null, null,
									"true",null,null,dimObj.defValue);
					break;
				case Constants.DIM_TYPE_DATE:
					// 单日历
					objTmp = ParamTmp.component[Constants.PARAM_COMP_DATE_SIMPLE]
							("DATE", "数据日期", dimObj.orderNo, null, null, null,
									"true",null,null,dimObj.defValue);
					break;
				case Constants.DIM_TYPE_ORG:
					// 弹出框
					objTmp = ParamTmp.component[Constants.PARAM_COMP_POPUP](
							"ORG", "机构", dimObj.orderNo,null,null,null,dimObj.required,null,dimObj.checkbox,dimObj.defValue);
					break;
				default:
					// 其他 - 下拉框
					objTmp = ParamTmp.component[Constants.PARAM_COMP_COMBO](
							dimObj.dimTypeNo, dimObj.dimTypeNm, dimObj.orderNo,
							dimObj.dimTypeNo, dimObj.dimTypeStruct,null,dimObj.required,null,dimObj.checkbox,dimObj.defValue);
					break;
				}
				return objTmp;
			}

			/**
			 * 生成明细字段对应的模板对象
			 * 
			 * @param colObj
			 *            {enNm , cnNm , dbType , dimTypeNo , elementType ,
			 *            orderNo ...}
			 */
			ParamTmp.generateModuleTmp = function(colObj) {
				var objTmp = {};
				if (!colObj && typeof colObj != "object"
						&& typeof colObj.elementType != "string") {
					return objTmp;
				}
				objTmp = ParamTmp.component[colObj.elementType](colObj.enNm,
						colObj.cnNm, colObj.orderNo, colObj.dimTypeNo,
						colObj.dimTypeStruct, colObj.paramId, colObj.required,
						colObj.daterange,colObj.checkbox,colObj.defValue,colObj.isConver,colObj.dataJson,colObj.busiType);
				objTmp.uuid = colObj.uuid;
				return objTmp;
			}

			return ParamTmp;
})(jQuery);
