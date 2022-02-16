/*******************************************************/
/****     报表设计器 - 报表行/列信息   ****/
/*******************************************************/
define(["constants"] , function(Constants){

	var RowCol = {};
	
	RowCol._defaults = {
			seq : "" , // 行列信息缓存中唯一标识
			objType : Constants.POS_TYPE_ROW , // 对象类型 : 01-行；02-列；
			rowId : "" , // 行号 （不由前端维护）
			colId : "" , // 列号 (不由前端维护)
			filtInfos : "[]" , // 维度过滤明细 JSON{dimTypeNo , checkedVal , filterModelVal , filterText}
			// 方法
			filtMapping : function(){
				// 生成过滤信息map方法，key：dimTypeNo , value:过滤信息对象
				var mapping = [];
				var filtInfoObj = JSON2.parse(this.filtInfos);
				if(filtInfoObj instanceof Array 
						&& filtInfoObj.length > 0){
					for(var i = 0 , l = filtInfoObj.length ; i < l ; i++){
						var filtTmp = filtInfoObj[i];
						if(jQuery.inArray(filtTmp.dimTypeNo , mapping) == -1){
							mapping[filtTmp.dimTypeNo] = filtTmp;
						}
					}
				}
				return mapping;
			} 
	};
	
	RowCol._attrTemplates = [];
	RowCol._attrTemplates[Constants.POS_TYPE_ROW] = {
		// 行特殊信息
		objType : Constants.POS_TYPE_ROW
	};
	
	RowCol._attrTemplates[Constants.POS_TYPE_COL] = {
		// 列特殊信息
		objType : Constants.POS_TYPE_COL
	};
	
	/**
	 * 创建新的行对象
	 */
	RowCol.newRow = function(){
		return RowCol._newInstance(Constants.POS_TYPE_ROW);
	}
	
	/**
	 * 创建新的列对象
	 */
	RowCol.newColumn = function(){
		return RowCol._newInstance(Constants.POS_TYPE_COL);
	}
	
	RowCol._newInstance = function(objType){
		var settings = {};
		jQuery.extend(settings , RowCol._defaults);
		// 加工模板特有属性
		if(objType != null
				&& typeof objType != "undefined"){		
			var templateSettings = RowCol._attrTemplates[objType];
			if(templateSettings){
				for(var i in templateSettings){
					settings[i] = templateSettings[i];
				}
			}
		}
		return settings;
	}

	return RowCol;
	
});