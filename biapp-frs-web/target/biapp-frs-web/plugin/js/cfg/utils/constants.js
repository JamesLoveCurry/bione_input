/*******************************************************/
/****             报表设计器 - 静态常量字典                    ****/
/*******************************************************/
define(function(){
	
	var Constants = {
			DIM_TYPE_DATE : "DATE" , // 日期类维度类型
			
			DIM_TYPE_DATE_RANGE : "DATERANGE" , // 日期范围维度类型
			
			DIM_TYPE_UNIT : "tempUnit" , // 数据单位
			 
			/**  模板类型  **/
			TEMPLATE_TYPE_MODULE : "01" , // 明细类
			TEMPLATE_TYPE_IDX : "02" , // 单元格类
			TEMPLATE_TYPE_ALL : "03" ,  // 综合类
			TEMPLATE_TYPE_IDXCOL_V : "04" , // 指标列表（纵）
			TEMPLATE_TYPE_IDXCOL_H : "05" , // 指标列表（横）
			TEMPLATE_TYPE_CROSS : "06",  // 交叉列表
				
			/**  单元格类型  **/
			CELL_TYPE_BATCH : "-1" , //批量处理
			CELL_TYPE_COMMON : "01",  //一般单元格
			CELL_TYPE_MODULE : "02",    // 数据模型
			CELL_TYPE_IDX : "03",				 // 指标
			CELL_TYPE_FORMULA : "04",	 // excel公式
			CELL_TYPE_BJJS : "05" ,			 // 表间计算
			CELL_TYPE_EXPRESSION : "06" ,  // 表达式
			CELL_TYPE_IDXCOL : "07" ,      // 列表-指标
			CELL_TYPE_DIMCOL : "08" ,     // 列表-维度
			
			/**  批量设置 位置类型  **/
			POS_TYPE_ROW : "01" , // 行过滤
			POS_TYPE_COL : "02" ,   // 列过滤
				
			/**  数据类型  （已废弃） **/
//			DATA_TYPE_NUM : "01" , // 数值
//			DATA_TYPE_TEXT : "02" , // 文本
			
			/**  显示格式   **/
			DISPLAY_FORMAT_BASIC : "01" , // 原格式（金额）
			DISPLAY_FORMAT_PER : "02" , // 百分比
			DISPLAY_FORMAT_NUM : "03" , // 数值
			DISPLAY_FORMAT_TEXT : "04" , // 文本
			
			/**  扩展方向  **/
			EXT_DIRECTION_ROW : "01" , // 列扩展
			EXT_DIRECTION_COL : "02" , // 行扩展
			
			/**  扩展方式  **/
			EXT_MODE_INSERT : "01" , // 插入
			EXT_MODE_OVERRITE : "02" , // 覆盖
			
			/** 展现方式 **/
			SHOW_TYPE_NORMAL : "normal" , // 普通展现
			SHOW_TYPE_CELLNM : "cellnm" , // 展现单元格名称
			SHOW_TYPE_BUSINO : "busino" , // 展现业务标识
				
			/** 排序方式 **/
			SORT_MODE_NOSORT : "01" , // 不排序
			SORT_MODE_A2Z : "02" , // 正序排序(A-Z)
			SORT_MODE_Z2A : "03" , // 倒序排序(Z-A)
				
			/** 维度类型 **/
			DIM_TYPE_DATE : "DATE" , // 日期
			DIM_TYPE_ORG : "ORG" , // 机构
			DIM_TYPE_STRUCT_TREE : "02" ,// 维度树形结构
			
			/** 参数模板类型 **/
			PARAM_COMP_TEXT : "01"  ,// 文本
			PARAM_COMP_NUM : "02" , // 数字
			PARAM_COMP_COMBO : "03" , // 单选下拉框
			PARAM_COMP_POPUP : "04" , // 弹出框
			PARAM_COMP_DATE_SIMPLE : "05" , // 单日历
			PARAM_COMP_DATE_DOUBLE : "06" , // 双日历
			PARAM_COMP_DATE_COMBOMULTI : "07", // 复选下拉框
			PARAM_COMP_TEXT_RANGE : "08", //数值区间 edit by fangjuan 20151022
			PARAM_COMP_UNIT : "09", //数值区间 edit by fangjuan 20151022
				
			/** 汇总方式 **/
			DATA_SUM_NULL : "00", //不汇总
			DATA_SUM_SUM : "01" //求和
	};
	
	Constants.idxCellTypes = [Constants.CELL_TYPE_IDX , Constants.CELL_TYPE_IDXCOL]; // 指标类单元格类型集合
	
	return Constants;
	
})