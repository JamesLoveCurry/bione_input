/*******************************************************/
/****     报表设计器 - 报表指标(单元格)信息   ****/
/*******************************************************/
define(["utils" , "constants"] , function(utils , Constants){

	var RptIdxInfo = {};
	
	RptIdxInfo._templateType;
	
	RptIdxInfo._Utils = utils;
	
	// 不允许被copy的属性
	RptIdxInfo.noCopyAttrs = ["cellNo"];
	
	// 允许被copy的属性(指标、excel、表间取数)
	RptIdxInfo.copyAttrs = {
			"cellNm" : "cellNm", //单元格名称
			"busiNo" : "busiNo",  //人行机构编码
			"dataPrecision" : "dataPrecision", //数据精度
			"dataUnit" : "dataUnit", //数据单位
			"displayFormat" : "displayFormat", //显示格式
			"isNull" : "isNull",//是否可空
			"remark" : "remark",//备注说明
			"caliberExplain" : "caliberExplain",//业务口径
			"caliberTechnology" : "caliberTechnology"//技术口径
	};
	
	// 指标类属性映射
	RptIdxInfo.defaultIdxAttrsMap = {
			"dataType" : "displayFormat",
			"dataUnit" : "dataUnit",
			"statType" : "statType"
	};
	
	// 入口方法
	RptIdxInfo.initContext = function(templateType){
		// 初始化环境变量
		RptIdxInfo._templateType = templateType ? templateType : Constants.TEMPLATE_TYPE_MODULE;
		// 初始化属性
		RptIdxInfo._defaults = {
				cellType : Constants.CELL_TYPE_COMMON , // 单元格类型 : 01-一般单元格；02-数据模型；03-指标；04-excel公式 ...
				cellTypeNm : "一般单元格" , // 单元格类型名称
				cellNo : "" ,   // 单元格编号
				busiNo : "" , // 业务编号
				cellNm : "" ,  // 单元格名称
//				dataType : Constants.DATA_TYPE_NUM , // 单元格类型  01-数值 ； 02-字符
				isUpt : "Y" , // 是否修改  Y-可以修改 ； N-不可以修改
				isNull : "Y" , // 是否可空  Y-可以为空； N-不可以为空
				isSum : "N",//是否汇总指标
				isFillSum : "N",//是否填报汇总
				isLock : "N",//是否锁定
//				dataUnit : "" , // 数据单位 ，显示用， 缺省是【使用模板单位】
				dataLen : "" , // 数据长度
				dataPrecision : "2" , // 数据精度
//				displayFormat : "" , // 显示格式  01-金额 ； 02-百分比 ； 03-数值
				caliberExplain : "" , // 口径说明
				caliberTechnology : "" , // 技术口径
				remark : "" , // 备注
//				sortMode : "" , // 排序方式 
				rowId : "" , // 行号 （不由前端维护）
				colId : "" , // 列号 (不由前端维护)
				seq : "" , // 报表指标类信息缓存中唯一标识
				// 方法
				cellLabel : function(showType){}  // 单元格label , showType-展示类型，详见constants.js中SHOW_TYPE_**
		};
		
		RptIdxInfo._defaultVals = {
				// 缺省的属性值，若不在此处定义，缺省值统一置为""（PS:所以此处记录的都是缺省值为非""的，不然太多  = 。 =）
				dataUnit : "-1" 
		}
		
		// 批量操作特殊属性
		RptIdxInfo._batchSpeVals = ["cellType" , "cellTypeNm" , "seq"];
		
		RptIdxInfo.templateSettings = [];
		RptIdxInfo.templateSettings[Constants.CELL_TYPE_COMMON] = {
				cellType : Constants.CELL_TYPE_COMMON , // 单元格类型 : 01-一般单元格；02-数据模型；03-指标；04-excel公式 ...
				cellTypeNm : "一般单元格" , // 单元格类型名称
				typeId : "00",
				content : ""
		}
		RptIdxInfo.templateSettings[Constants.CELL_TYPE_MODULE] = {
				// 数据集明细属性
				cellType : Constants.CELL_TYPE_MODULE ,
				cellTypeNm : "数据模型单元格" ,
				displayFormat : Constants.DISPLAY_FORMAT_TEXT , // 文本
				dataUnit : "00" ,
				dsId : "" , // 数据集ID
				dsName : "" , // 数据集名称
				columnId : "" , // 字段ID
				columnName : "" , // 字段名称
				isExt : "Y" , // 是否扩展(是/否)
				extDirection : Constants.EXT_DIRECTION_ROW , //扩展方向(行/列)
				extMode : Constants.EXT_MODE_INSERT , // 扩展方式(插入/覆盖)
				isSort : "N" , // 是否排序
				sortMode : "01" , // 排序方式
				sortOrder : "" , // 排序顺序
				sortDbType : "VARCHAR2(2000)", //排序类型
				filter : "",
				isConver : "N" ,  // 是否转码显示
				dbClkUrl : "/report/frame/design/cfg/dbclk/module",
				dialogWidth : "60%",
				dialogHeight : "60%",
				isMerge : "N",//是否合并
				isMergeCol : "N",//是否合并参照列
				cellLabel : function(showType){
					var label = "";
					if(!showType){
						showType = Constants.SHOW_TYPE_NORMAL;
					}
					if(showType == Constants.SHOW_TYPE_CELLNM){
						label += ("{" + this.cellNm ? this.cellNm : this.cellNo + "}");
					}else if(showType == Constants.SHOW_TYPE_BUSINO){
						label += ("{" + this.busiNo ? this.busiNo : this.cellNo + "}");
					}else{					
						label += ("{" + this.dsName + "}.{" + this.columnName + "}");
						if("Y" == this.isExt){
							// 进行扩展
							if(Constants.EXT_DIRECTION_ROW == this.extDirection){
								// 行扩展
								label += "↓";
							} else {
								label += "→";
							}
						}
					}
					return label;
				}
		}
		RptIdxInfo.templateSettings[Constants.CELL_TYPE_IDX] = {
				// 指标单元格属性
				cellType : Constants.CELL_TYPE_IDX ,
				cellTypeNm : "指标单元格" ,
				displayFormat : Constants.DISPLAY_FORMAT_BASIC ,
				dataUnit : "" ,
				realIndexNo : "" , // 报表指标对应的指标编号
				indexNo : "" , // 引用指标标识 , 为null或为''时，代表该报表指标为空指标
				indexNm : "" , // 单元格显示名称
				indexVerId : "",//引用指标版本号
				measureNo : "",//度量标志
				//measureNm : "",//度量名称
				isSum : "Y",//是否汇总指标
				isFillSum : "Y",//是否填报汇总
				statType : "", // 来源指标统计类型
				isSubDim : "N" , // 是否减维
				isFiltDim : "N" , // 是否过滤维度
				isLock : "N", //是否锁定
				allDims : null , // 全部维度
				factDims : null , // 实际维度
				filtInfos : [] , // 维度过滤明细 JSON
				ruleId : "1" , // 规则ID
				timeMeasureId : "1" , // 时间度量ID
				modeId : "1" , // 计算方式
				dbClkUrl : "/report/frame/design/cfg/dbclk/module/idx",
				dialogWidth : "70%",
				dialogHeight : "65%",
				cellLabel : function(showType){
					var label = "[指标]";
					if(!showType){
						showType = Constants.SHOW_TYPE_NORMAL;
					}
					if(showType == Constants.SHOW_TYPE_CELLNM){
						var tmp = this.cellNm ? this.cellNm : this.cellNo;
						label += ("{" + tmp + "}");
					}else if(showType == Constants.SHOW_TYPE_BUSINO){
						var tmp = this.busiNo ? this.busiNo : this.cellNo;
						label += ("{" + tmp + "}");
					}else{
						if(this.indexNo == ""
							|| this.indexNo == null){
							label += ("{空指标}");
							return label;
						}else{				
							label += ("{"+this.indexNm+"}");
						}
						//if(this.isSubDim == "Y"){
						//	label += (".[减维]");
						//}
						if(this.filtInfos != null
								&&( 
										(typeof this.filtInfos == "string" 
											&& this.filtInfos.length > 2)
											|| (typeof this.filtInfos == "object"
												&& this.filtInfos.length > 0)
								)){
							label += (".[过滤]");
						}
					}
					return label;
				}
		}
		RptIdxInfo.templateSettings[Constants.CELL_TYPE_FORMULA] = {
				// excel公式单元格属性
				cellType : Constants.CELL_TYPE_FORMULA , 
				cellTypeNm : "excel公式" ,
				displayFormat : Constants.DISPLAY_FORMAT_BASIC ,
				dataUnit : "" ,
				sortMode : Constants.SORT_MODE_NOSORT , 
				excelFormula : "" , // excel公式内容
				isAnalyseExt : "N" , // 是否分析扩展  Y-是；N-否
				analyseExtType : "02", // 分析扩展类型 01-范围扩展，02-纵向自增扩展(目前，指标列表初始化为02)
				isRptIndex : "Y" ,    // 是否报表指标  Y-是；N-否
				realIndexNo : "",    // 报表指标对应的指标编号
				isSum : "N",//是否汇总指标
				isFillSum : "N",//是否填报汇总指标
				busiNo : ""// 业务编号
		}
		RptIdxInfo.templateSettings[Constants.CELL_TYPE_BJJS] = {
				// 表间计算公式单元格属性
				cellType : Constants.CELL_TYPE_BJJS , 
				cellTypeNm : "表间计算" ,
				displayFormat : Constants.DISPLAY_FORMAT_BASIC ,
				dataUnit : "" ,
				realIndexNo : "" , // 报表指标对应的指标编号
				indexNo : "" , // 引用指标编号
				formulaDims : null , // 公式维度
				formulaContent : "",  // 引擎用表达式
				formulaDesc : "",  // 显示用表达式
				dbClkUrl : "/report/frame/valid/logic/getData",
				dialogWidth : '99%',
				dialogHeight : '99%',
				busiNo : "",// 业务编号
				isUpt : "N",//是否可修改
				cellLabel : function(showType){
					var label = "";
					if(!showType){
						showType = Constants.SHOW_TYPE_NORMAL;
					}
					if(showType == Constants.SHOW_TYPE_CELLNM){
						var tmp = this.cellNm ? this.cellNm : this.cellNo;
						label += ("{" + tmp + "}");
					}else if(showType == Constants.SHOW_TYPE_BUSINO){
						var tmp = this.busiNo ? this.busiNo : this.cellNo;
						label += ("{" + tmp + "}");
					}else{
						label = "{表间计算}";
					}
					return label;
				}
		}
		RptIdxInfo.templateSettings[Constants.CELL_TYPE_EXPRESSION] = {
				// 表达式单元格属性
				cellType : Constants.CELL_TYPE_EXPRESSION , 
				cellTypeNm : "表达式" ,
				expression : "",
				dbClkUrl : "/report/frame/design/cfg/sysVarSet",
				dialogWidth : '80%',
				dialogHeight : '82%',
				cellLabel : function(showType){
					var label = "";
					if(!showType){
						showType = Constants.SHOW_TYPE_NORMAL;
					}
					if(showType == Constants.SHOW_TYPE_CELLNM){
						var tmp = this.cellNm ? this.cellNm : this.cellNo;
						label += ("{" + tmp + "}");
					}else if(showType == Constants.SHOW_TYPE_BUSINO){
						var tmp = this.busiNo ? this.busiNo : this.cellNo;
						label += ("{" + tmp + "}");
					}else{
						label = this.expression;
					}
					return label;
				}
		}
		RptIdxInfo.templateSettings[Constants.CELL_TYPE_IDXCOL] = {
				// 列表指标单元格属性
				cellType : Constants.CELL_TYPE_IDXCOL ,
				cellTypeNm : "[列表]指标单元格" ,
				displayFormat : Constants.DISPLAY_FORMAT_BASIC ,
				dataUnit : "" ,
				sortMode : Constants.SORT_MODE_NOSORT , 
				realIndexNo : "" , // 报表指标对应的指标编号
				indexNo : "" , // 引用指标标识 , 为null或为''时，代表该报表指标为空指标
				indexNm : "" , // 单元格显示名称
				indexVerId : "",//引用指标版本号
				measureNo : "",//度量标志
				//measureNm : "",//度量名称
				isSum : "Y",//是否汇总指标
				isFillSum : "Y",//是否填报汇总指标
				statType : "", // 来源指标统计类型
				isSubDim : "N" , // 是否减维
				isFiltDim : "N" , // 是否过滤维度
				allDims : null , // 全部维度
				factDims : null , // 实际维度
				filtInfos : [] , // 维度过滤明细 JSON
				ruleId : "1" , // 规则ID
				timeMeasureId : "1" , // 时间度量ID
				modeId : "1" , // 计算方式
				dbClkUrl : "/report/frame/design/cfg/dbclk/module/idx",
				dialogWidth : "70%",
				dialogHeight : "65%",
				cellLabel : function(showType){
					var label = "";
					if(!showType){
						showType = Constants.SHOW_TYPE_NORMAL;
					}
					if(showType == Constants.SHOW_TYPE_CELLNM){
						var tmp = this.cellNm ? this.cellNm : this.cellNo;
						label += ("{" + tmp + "}");
					}else if(showType == Constants.SHOW_TYPE_BUSINO){
						var tmp = this.busiNo ? this.busiNo : this.cellNo;
						label += ("{" + tmp + "}");
					}else{
						if(this.indexNo == ""
							|| this.indexNo == null){
							label += ("{空指标}");
							return label;
						}else{				
							label += ("{"+this.indexNm+"}");
						}
						//if(this.isSubDim == "Y"){
						//	label += (".[减维]");
						//}
						if(this.filtInfos != null
								&&( 
										(typeof this.filtInfos == "string" 
											&& this.filtInfos.length > 2)
											|| (typeof this.filtInfos == "object"
												&& this.filtInfos.length > 0)
								)){
							label += (".[过滤]");
						}
					}
					if(templateType=="04")
						label += "↓";
					if(templateType=="05")
						label += "→";
					if(templateType=="06")
						label += "↓→";
					return label;
				}
		}
		
		RptIdxInfo.templateSettings[Constants.CELL_TYPE_DIMCOL] = {
				// 列表维度单元格属性
				cellType : Constants.CELL_TYPE_DIMCOL ,
				cellTypeNm : "[列表]维度单元格" ,
				dimTypeNo : "" , // 维度类型标识
				dimTypeNm : "" , // 维度类型名称
				dimType : "" , // 维度类型
				isTotal : "N" , // 是否合计
				dateFormat : "01" , // 日期显示格式   -- 只有当dimTypeNo是DATE时初始化该属性（这个逻辑有点点死，暂时没有太好的办法）
				isConver : "Y" ,  // 是否转码显示
				displayLevel : "" , // 显示级别
				dbClkUrl : "/report/frame/design/cfg/platform/dimLvlChoose",
				dataUnit : "00", // 统一不使用单位
				dialogWidth : "50%",
				dialogHeight : "250",
				extDirection : "" ,
				cellLabel : function(showType){
					var label = "";
					if(!showType){
						showType = Constants.SHOW_TYPE_NORMAL;
					}
					if(showType == Constants.SHOW_TYPE_CELLNM){
						var tmp = this.cellNm ? this.cellNm : this.cellNo;
						label += ("{" + tmp + "}");
					}else if(showType == Constants.SHOW_TYPE_BUSINO){
						var tmp = this.busiNo ? this.busiNo : this.cellNo;
						label += ("{" + tmp + "}");
					}else{
						label += ("{"+this.dimTypeNm+"}");
					}
					if(this.extDirection=="01")
						label += "↓";
					if(this.extDirection=="02")
						label += "→";
					return label;
				}
		}
	}
	
	/**
	 * 创建报表指标新对象
	 * @param idxType 报表指标类型
	 * @param sourceIdx 来源指标
	 */
	RptIdxInfo.newInstance = function(idxType , sourceIdx){
		var settings = {};
		jQuery.extend(settings , RptIdxInfo._defaults);
		// 加工模板特有属性
		if(idxType != null
				&& typeof idxType != "undefined"){		
			var templateSettings = RptIdxInfo.templateSettings[idxType];
			if(templateSettings){
				for(var i in templateSettings){
					settings[i] = templateSettings[i];
				}
			}
		}
		if(sourceIdx
				&& typeof sourceIdx == "object"
				&& jQuery.inArray(idxType , Constants.idxCellTypes) != -1){
			// 根据来源指标分析基础属性默认值
			$.each(RptIdxInfo.defaultIdxAttrsMap , function(i , attr){
				if(typeof sourceIdx[i] != "undefined"
					&& sourceIdx[i] != ""){
					settings[attr] = sourceIdx[i];
				}
			})
		}
		return settings;
	}
	
	/**
	 * 基于knockout的对象初始化，初始化属性
	 */
	RptIdxInfo.newInstanceKO = function(idxTypeArg){
		if(ko == null
				|| typeof ko != "object"){
			return {};
		}
		var settings = {};
		if(idxTypeArg != null
				&& typeof idxTypeArg != "undefined"){
			jQuery.extend(settings , RptIdxInfo._defaults);
			jQuery.extend(settings , RptIdxInfo.templateSettings[idxTypeArg]);
		}else{
			//获取所有扩展类型单元格属性
			for(var idxType in RptIdxInfo.templateSettings){
				jQuery.extend(settings , RptIdxInfo.templateSettings[idxType]);
			}
			jQuery.extend(settings , RptIdxInfo._defaults);
			var settingBatch = [];
			for(var i in settings){
				settingBatch["_"+i+"Batch"] = false;
			}
			jQuery.extend(settings , settingBatch);
		}
		// 基于knockout加工
		for(var j in settings){
			if(settings[j] != null
					&& typeof settings[j] == "function"){
				continue;
			}
			var oldVal = settings[j]
			settings[j] = ko.observable(oldVal);
		}
		return settings;
	}
	
	/**
	 * ko对象数据加载
	 */
	RptIdxInfo.initIdxKO = function(koObj , idxObj){
		if( ko != null
				&& typeof ko == "object"
				&& koObj != null
				&& typeof koObj == "object"){		
			if(idxObj == null
					|| typeof idxObj != "object"){
				// 当做一般单元格处理，缺省就是一般单元格处理
				idxObj = {};
				jQuery.extend(idxObj , this.newInstance());
			}
			for(var i in idxObj){
				if(typeof idxObj[i] == "function"){
					continue;
				}
				var argName = i , 
					   argValue = idxObj[i];
				if(argName in koObj
						&& typeof koObj[argName] == "function"){
					koObj[argName](argValue == null ? "" : argValue);
				}
			}
		}
	}
	
	return RptIdxInfo;
	
});