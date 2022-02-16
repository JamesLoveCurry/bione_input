/**
 * 
 */
package com.yusys.bione.plugin.spreadjs.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.plugin.spreadjs.entity.Data;
import com.yusys.bione.plugin.spreadjs.entity.DataTable;
import com.yusys.bione.plugin.spreadjs.entity.DataTableProperty;
import com.yusys.bione.plugin.spreadjs.entity.DataTablePropertyProperty;
import com.yusys.bione.plugin.spreadjs.entity.Defaults;
import com.yusys.bione.plugin.spreadjs.entity.Gridline;
import com.yusys.bione.plugin.spreadjs.entity.SheetSize;
import com.yusys.bione.plugin.spreadjs.entity.SheetsProperty;
import com.yusys.bione.plugin.spreadjs.entity.Span;
import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;
import com.yusys.bione.plugin.spreadjs.entity.Style;
import com.yusys.bione.plugin.spreadjs.entity.Validator;

/**
 * excel 读取工具类
 * @author tanxu
 * 
 */
public abstract class AbstractExcelReader implements IExcelReader {

	protected int sheetCount = 0;

	private Map<String, String> styleMap = new HashMap<String, String>(); // 单元格格式Map

	private Map<Validator, String> validatorMap = new HashMap<Validator, String>();// 数据有效单元格格式

	
	private Map<String,String> formulaCell=new HashMap<String, String>();
	private Map<String, Style> namedStyles = new HashMap<String, Style>();
	private double defRowSzie;
	private double defCellSzie;
	private Map<CellRangeAddress, Validator> validators = new HashMap<CellRangeAddress, Validator>();// 数据有效性区域map
	private int StyleNum = 0;
	// 水平对齐标识
	public static int HALIGN_LEFT = 0;// 左对齐
	public static int HALIGN_CENTER = 1;// 居中对齐
	public static int HALIGN_RIGHT = 2;// 右对齐
	public static int HALIGN_DEFAULT = 3;// 底端对齐
	// 垂直对齐标识
	public static int VALIGN_TOP = 0;// 顶端对齐
	public static int VALIGN_CENTER = 1;// 居中对齐
	public static int VALIGN_BOTTOM = 2;// 底端对齐

	public static String DEFAULT_STYLENAME = "";// 单元格格式前缀
	
	//excel公式集合缓存Key
	private String EXCEL_FORMULA_KEY = "excel_formula_key";

	/**
	 * 添加命名样式
	 * 
	 * @param nameStyle
	 */
	protected void addNamedStyles(Style nameStyle) {
		this.namedStyles.put(nameStyle.getName(), nameStyle);
	}

	/**
	 * 添加带数据有效性的单元格式
	 * 
	 * @param namedStyle
	 *            源单元格格式
	 * @param va
	 *            数据有效性信息
	 * @return 带数据有效性的单元格式的格式名称
	 */
	private String addValidatorMap(Style namedStyle, Validator va) {
		Style style = new Style();
		copyObject(namedStyle, style);
		style.setName(createStyleName());
		style.setValidator(va);
		style.setFormatter("@");
		this.addNamedStyles(style);
		this.validatorMap.put(va, style.getName());
		return style.getName();
	}

	/**
	 * 复制实体内容（暂时使用,等有工具类替换）
	 * 
	 * @param from
	 * @param to
	 */
	private static void copyObject(Object from, Object to) {
		Method[] fromMethods = from.getClass().getDeclaredMethods();
		Method[] toMethods = to.getClass().getDeclaredMethods();
		Method fromMethod = null, toMethod = null;
		String fromMethodName = null, toMethodName = null;
		for (int i = 0; i < fromMethods.length; i++) {
			fromMethod = fromMethods[i];
			fromMethodName = fromMethod.getName();
			if (!fromMethodName.contains("get"))
				continue;

			toMethodName = "set" + fromMethodName.substring(3);
			toMethod = findMethodByName(toMethods, toMethodName);
			if (toMethod == null)
				continue;
			try {
				Object value = fromMethod.invoke(from, new Object[0]);
				if (value == null)
					continue;
				// 集合类判空处理
				if (value instanceof Collection) {
					Collection<?> newValue = (Collection<?>) value;
					if (newValue.size() <= 0)
						continue;
				}
				toMethod.invoke(to, new Object[] { value });
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 创建schema的基本信息
	 * 
	 * @param workbook
	 *            工作簿
	 * @return 返回schema的基本信息
	 */
	protected SpreadSchema createBaseSchema(Workbook workbook) {
		SpreadSchema schema = new SpreadSchema();
		schema.setVersion("2.0");// 设置spreadJS版本号
		int sheetnum = 1;
		if (this.sheetCount > 0) {
			sheetnum = workbook.getNumberOfSheets() <= sheetCount ? workbook
					.getNumberOfSheets() : sheetCount;
		}
		schema.setSheetCount(sheetnum);// 设置标签页总数
		schema.setActiveSheetIndex(workbook.getFirstVisibleTab());// 设置选中的标签页索引
		schema.setStartSheetIndex(workbook.getFirstVisibleTab());// 设置开始标签页索引
		schema.setNoRecalc(false);// 设置是否重新计算,该值默认为false,如果勾选页面的 void
									// recalculation after load 以后,则为true
		return schema;
	}

	/**
	 * 创建基础的标签页数据信息
	 * 
	 * @param sheet
	 *            标签页
	 * @return 标签页基本信息
	 */
	protected SheetsProperty createSheetBaseInfo(Sheet sheet) {
		SheetsProperty st = new SheetsProperty();// 标签页参数
		st.setName(sheet.getSheetName());// 设置标签页名称
		st.setRowCount(sheet.getLastRowNum() + 1);// 设置标签页包含行数
		st.setColumnCount(1);// 初始设置为“1”,防止单元格无数据时显示内容错误
		return st;
	}

	/**
	 * 自动生成样式名称
	 * 
	 * @return 样式名称
	 */
	protected String createStyleName() {
		return new StringBuilder(DEFAULT_STYLENAME).append(
				String.valueOf(++this.StyleNum)).toString();
	}

	/**
	 * 从方法数组中获取指定名称的方法（拷贝对象函数调用，等有工具类后将会替换）
	 * 
	 * @param methods
	 * @param name
	 * @return
	 */
	private static Method findMethodByName(Method[] methods, String name) {
		for (int j = 0; j < methods.length; j++) {
			if (methods[j].getName().equals(name))
				return methods[j];
		}
		return null;
	}

	private String encodeHex(int value) {
		StringBuilder buf = new StringBuilder(2);
		value &= 0x00FF;
		if (value < 0x10) {
			buf.append("0");
		}
		buf.append(Integer.toString(value, 16));
		return buf.toString();
	}
	
	/**
	 * 获取Color对象的RGB值
	 * 
	 * @param color
	 * @return
	 */
	protected String getColorRGB(Color color, HSSFColorPredefined defaultColor) {
		if (color != null) {
			if (color instanceof HSSFColor) {
				short[] rgb = HSSFColor.toHSSFColor(color).getTriplet();
				return "#" + encodeHex(rgb[0]) + encodeHex(rgb[1]) + encodeHex(rgb[2]);
			}
			if (color instanceof XSSFColor) {
				byte[] rgb = XSSFColor.toXSSFColor(color).getRGB();
				if (rgb != null) {
					return "#" + encodeHex(rgb[0]) + encodeHex(rgb[1]) + encodeHex(rgb[2]);
				}
			}
		}
		short[] rgb = defaultColor.getTriplet();
		return "#" + encodeHex(rgb[0]) + encodeHex(rgb[1]) + encodeHex(rgb[2]);
	}

	private String getCellStyleDesc(CellStyle cellStyle) {
		StringBuilder sb = new StringBuilder();
		sb.append(cellStyle.getAlignmentEnum().getCode()).append('_');
		sb.append(cellStyle.getBorderBottomEnum().getCode()).append('_');
		sb.append(cellStyle.getBorderLeftEnum().getCode()).append('_');
		sb.append(cellStyle.getBorderRightEnum().getCode()).append('_');
		sb.append(cellStyle.getBorderTopEnum().getCode()).append('_');
		sb.append(cellStyle.getFillPatternEnum().getCode()).append('_');
		sb.append(cellStyle.getFontIndex()).append('_');
		sb.append(BooleanUtils.toInteger(cellStyle.getHidden())).append('_');
		sb.append(cellStyle.getIndention()).append('_');
		sb.append(BooleanUtils.toInteger(cellStyle.getLocked())).append('_');
		sb.append(BooleanUtils.toInteger(cellStyle.getQuotePrefixed())).append('_');
		sb.append(cellStyle.getRotation()).append('_');
		sb.append(BooleanUtils.toInteger(cellStyle.getShrinkToFit())).append('_');
		sb.append(cellStyle.getVerticalAlignmentEnum().getCode()).append('_');
		sb.append(BooleanUtils.toInteger(cellStyle.getWrapText())).append('_');
		if (cellStyle instanceof HSSFCellStyle) {
			sb.append(cellStyle.getBottomBorderColor()).append('_');
			sb.append(cellStyle.getLeftBorderColor()).append('_');
			sb.append(cellStyle.getRightBorderColor()).append('_');
			sb.append(cellStyle.getTopBorderColor()).append('_');
			sb.append(cellStyle.getFillForegroundColor()).append('_');
		} else {
			sb.append(getColorRGB(((XSSFCellStyle)cellStyle).getBottomBorderXSSFColor(), HSSFColorPredefined.BLACK)).append('_');
			sb.append(getColorRGB(((XSSFCellStyle)cellStyle).getLeftBorderXSSFColor(), HSSFColorPredefined.BLACK)).append('_');
			sb.append(getColorRGB(((XSSFCellStyle)cellStyle).getRightBorderXSSFColor(), HSSFColorPredefined.BLACK)).append('_');
			sb.append(getColorRGB(((XSSFCellStyle)cellStyle).getTopBorderXSSFColor(), HSSFColorPredefined.BLACK)).append('_');
			sb.append(getColorRGB(cellStyle.getFillForegroundColorColor(), HSSFColorPredefined.WHITE)).append('_');
		}
		sb.append(StringUtils.defaultString(cellStyle.getDataFormatString()));
		return sb.toString();
	}

	/**
	 * 根据单元格格式获取单元格所属格式名称
	 * 
	 * @param key
	 * @return
	 */
	private String getCellStyle(CellStyle key) {
		String style = styleMap.get(getCellStyleDesc(key));
		if (style != null) {
			return style;
		} else {
			return "";
		}
	}

	/**
	 * 获取单元格数据
	 * 
	 * @param cell
	 *            单元格
	 * @return 单元格数据
	 */
	private Object getCellVal(Cell cell) {

		Object cellValue = null;

		switch (cell.getCellTypeEnum()) { // 根据cell中的类型来输出数据
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)
					|| DateUtil.isCellInternalDateFormatted(cell)) {
				cellValue = new StringBuilder("/OADate(")
						.append(String.valueOf(cell.getNumericCellValue()))
						.append(")/").toString();
			} else {
				cellValue = cell.getNumericCellValue();
			}
			break;
		case STRING:
			cellValue = cell.getStringCellValue();
			break;
		case BOOLEAN:
			cellValue = cell.getBooleanCellValue();
			break;
		case FORMULA:
			cellValue = cell.getCellFormula();
			break;
		case BLANK:
			cellValue = null;
			break;
		default:
			cellValue = cell.toString();
			break;
		}

		return cellValue;
	}

	/**
	 * 获取列信息
	 * 
	 * @param sheet
	 *            标签页
	 * @param colCount
	 *            当前标签页的列数
	 * @return
	 */
	protected List<SheetSize> getColumnSize(Sheet sheet, int colCount) {
		List<SheetSize> columns = new ArrayList<SheetSize>();
		for (int i = 0; i < colCount; i++) {
			//强转成double保留小数位
			double width = (double)sheet.getColumnWidth(i) / 32;
			if (width != this.defCellSzie) {
				SheetSize size = new SheetSize();
				size.setSize(width);
				columns.add(size);
			} else {
				columns.add(new SheetSize());
			}
		}
		return columns;
	}

	/**
	 * 获取标签页默认的行高列宽
	 * 
	 * @param sheet
	 *            标签页
	 * @return 标签页缺省单元格属性
	 */
	protected Defaults getDefaultInfo(Sheet sheet) {
		Defaults de = new Defaults();
		this.defCellSzie = (double) sheet.getDefaultColumnWidth() * 8;

		this.defRowSzie = (double) (UnitsUtil.pointsToPixels(sheet
				.getDefaultRowHeightInPoints(), false));
		de.setColWidth(defCellSzie);
		de.setRowHeight(defRowSzie);
		de.setColHeaderRowHeight(20.0);
		de.setRowHeaderColWidth(40.0);
		return de;
	}

	/**
	 * 获取单元格公式信息
	 * 
	 * @param cell
	 *            单元格对象
	 * @return 单元格公式信息
	 */
	protected String getFormular(int row,int col,Cell cell) {
		if (cell.getCellTypeEnum() == CellType.FORMULA) {
			this.formulaCell.put(row+","+col, cell.getCellFormula());
			return cell.getCellFormula();
		}
		return null;
	}
	
	/**
	 * 获取单元格公式信息(报表导入新增)
	 * 
	 * @param cell
	 *            单元格对象
	 * @return 单元格公式信息
	 */
	protected String getImportFormular(int row,int col,Cell cell) {
		if (this.formulaCell.get(row+","+col) != null) {
			return this.formulaCell.get(row+","+col);
		}
		return null;
	}

	/**
	 * 获取字体样式
	 * 
	 * @param font
	 *            单元格字体对象
	 * @return 字体样式字符串 eg."normal normal 12pt 宋体"
	 */
	protected String getFontInfo(Font font) {
		StringBuilder fontInfo = new StringBuilder("");
		if (font != null) {
			/*
			fontInfo.append(font.getItalic() ? "italic " : "")
					.append(font.getBold() ? "bold "
							: "").append(font.getFontHeightInPoints())
					.append("pt ").append("宋体");
			*/
			//获取Font中的字体
			fontInfo.append(font.getItalic() ? "italic " : "")
					.append(font.getBold() ? "bold "
							: "").append(font.getFontHeightInPoints())
					.append("pt ").append(font.getFontName());
		}
		return fontInfo.toString();
	}

	/**
	 * 获取命名样式列表
	 * 
	 * @return 命名样式列表
	 */
	protected Map<String, Style> getNamedStyles() {
		return namedStyles;
	}

	/**
	 * 获取行信息
	 * 
	 * @param sheet
	 *            标签页
	 * @param rowCount
	 *            标签页行总数
	 * @return
	 */
	protected List<SheetSize> getRowSize(Sheet sheet, int rowCount) {
		List<SheetSize> rows = new ArrayList<SheetSize>();
		for (int i = 0; i < rowCount; i++) {
			Row row = sheet.getRow(i);
			if (row != null) {
				//POI 读取excel行高
				double height = UnitsUtil.pointsToPixels(row.getHeightInPoints(), false);
				/*
				if (height != defRowSzie) {
					SheetSize size = new SheetSize();
					size.setSize(height);
					rows.add(size);
				}
				*/
				//去除等于默认行高的判断
				SheetSize size = new SheetSize();
				size.setSize(height);
				rows.add(size);
			} else {
				rows.add(new SheetSize());
			}

		}
		return rows;
	}

	/**
	 * 获取单元格水平对齐标识
	 * 
	 * @param aligntype
	 *            对其标识
	 * 
	 * @return 0-左对齐；1-居中对齐；2-右对齐
	 */
	protected Integer getHorizontalAlignType(short aligntype) {
		switch (HorizontalAlignment.forInt(aligntype)) {
		case GENERAL:
			return null;
		case LEFT:
			return HALIGN_LEFT;
		case CENTER:
			return HALIGN_CENTER;
		case RIGHT:
			return HALIGN_RIGHT;
		default:
			return null;
		}
	}

	/**
	 * 获取标签页数据信息
	 * 
	 * @param sheet
	 *            标签页
	 * @return 标签页数据信息
	 */
	protected Data getSheetDataInfo(Sheet sheet, boolean isImport) {
		EhcacheUtils.remove(EXCEL_FORMULA_KEY, EXCEL_FORMULA_KEY);
		Data data = new Data();
		DataTable dataTables = new DataTable();// 数据表单
		data.setName(sheet.getSheetName());// 设置标签页名称
		data.setColCount(1);
		int j;
		int rowCount=0;
		for (j = 0; j <= sheet.getLastRowNum(); j++) {
			DataTableProperty rowPro = new DataTableProperty();// 行数据参数
			Row row = sheet.getRow(j);
			if (row != null) {
				int k;
				boolean cellHav=false;
				int colCount=0;
				for (k = 0; k < row.getLastCellNum(); k++) {
					Cell cell = row.getCell(k);
					if (cell != null) {
//						去掉单元格非空判断，因为表样中某行确实会为空
//						if (!cell.toString().equals("") || validateStyle(sheet.getWorkbook(), cell.getCellStyle())) {
//						}
						DataTablePropertyProperty cellPro = new DataTablePropertyProperty();// 单元格数据参数
						cellPro.setValue(getCellVal(cell));// 设置单元格数据
						cellPro.setStyle(getCellStyleName(sheet.getWorkbook(), cell.getCellStyle()));
						if(isImport){
							cellPro.setFormula(getImportFormular(j,k,cell));
						}else{
							cellPro.setFormula(getFormular(j,k,cell));
						}
						rowPro.setAdditionalProperty(String.valueOf(k), cellPro);
						cellHav=true;
						colCount=k;
					}
				}
				if(cellHav){
					rowCount=j;
					this.setColumnCount(data, colCount+1);
					dataTables.setAdditionalProperty(String.valueOf(j), rowPro);
				}
			}
		}
		data.setRowCount(rowCount+1);
		data.setDataTable(dataTables);// 设置数据集合的数据表单
		//把excel公式的集合放到缓存里，后面解析excel时遇到共享excel公式数据会用到，单张报表导入结束后缓存清空
		EhcacheUtils.put(EXCEL_FORMULA_KEY, EXCEL_FORMULA_KEY, formulaCell);
		return data;
	}
	
	@SuppressWarnings("unused")
	private Boolean validateStyle(Workbook workbook, CellStyle cellStyle){
		Style style = createNameStyle(workbook, cellStyle, false);
		if ((style.getBackColor() == null || "#ffffff".equals(style.getBackColor()))
				&& style.getBorderTop() == null
				&& style.getBorderLeft() == null
				&& style.getBorderRight() == null
				&& style.getBorderBottom() == null)
			return false;
		return true;
	}

	/**
	 * 根据单元格样式获取映射的样式名称
	 * 
	 * @param cellStyle
	 *            单元格样式
	 * @return 映射样式名称
	 */
	private String getCellStyleName(Workbook workbook, CellStyle cellStyle) {
		String styleName = getCellStyle(cellStyle);
		if (styleName == null || styleName.equals("")) {
			Style namedStyle = createNameStyle(workbook, cellStyle,true);
			this.addNamedStyles(namedStyle);
			styleName = namedStyle.getName();
		}
		return styleName;
	}

	/**
	 * 
	 * @param sheet
	 *            标签页
	 * @return 标签页网格信息
	 */
	protected Gridline getSheetGridLineInfo(Sheet sheet) {
		Gridline gl = new Gridline();
		gl.setShowHorizontalGridline(true);// 设置显示垂直网格线
		gl.setShowVerticalGridline(true);// 设置显示水平网格线
		return gl;
	}

	/**
	 * 获取合并但单元格信息
	 * 
	 * @param sheet
	 *            标签页
	 * 
	 * @return 标签页合并单元格信息
	 */
	protected List<Span> getSheetSpanInfo(Sheet sheet) {
		List<Span> spans = new ArrayList<Span>();// 设置合并单元格数据
		int sheetmergerCount = sheet.getNumMergedRegions();
		// 遍历合并单元格
		for (int s = 0; s < sheetmergerCount; s++) {
			// 获得合并单元格加入list中
			Span span = new Span();
			CellRangeAddress ca = sheet.getMergedRegion(s);
			span.setCol(ca.getFirstColumn());
			span.setRow(ca.getFirstRow());
			span.setColCount(ca.getLastColumn() - ca.getFirstColumn() + 1);
			span.setRowCount(ca.getLastRow() - ca.getFirstRow() + 1);
			spans.add(span);
		}
		return spans;
	}

	/**
	 * 根据格式名称获取单元格格式
	 * 
	 * @param styleName
	 *            格式名称
	 * 
	 * @return 单元格样式
	 */
	protected Style getStylebyName(String styleName) {
		return namedStyles.get(styleName);
	}

	/**
	 * 获取下划线样式
	 * 
	 * @param underline
	 *            单元格样式下划线标识
	 * @param strikeout
	 *            是否有删除线标识符
	 * @return 0-无下划线；1-有下划线；2-有删除线；3-有下划线和删除线
	 */
	protected Integer getUnderLineInfo(byte underline, boolean strikeout) {
		int type = (int) underline;
		type = type > 0 ? 1 : 0;
		if (strikeout)
			type += 2;
		if (type == 0)
			return null;
		else
			return type;
	}

	/**
	 * 获取单元格的数据有效性
	 * 
	 * @param row
	 *            单元格行数
	 * @param cell
	 *            单元格列数
	 * @return
	 */

	private Validator getValidators(int row, int cell) {
		Set<CellRangeAddress> regions = validators.keySet();
		if (regions != null && regions.size() > 0) {
			for (CellRangeAddress region : regions) {
				if (row <= region.getLastRow() && row >= region.getFirstRow()
						&& cell <= region.getLastColumn()
						&& cell >= region.getFirstColumn()) {
					return validators.get(region);
				}
			}
		}
		return null;
	}

	/**
	 * 获取数据有效性单元格格式
	 *    
	 * @param styleName
	 *            原单元格格式内容
	 * @param row
	 *            单元格行数
	 * @param cell
	 *            单元格列数
	 * @return
	 */
	protected String getValidatorsStyle(String styleName, int row, int cell) {
		Validator va = this.getValidators(row, cell);
		if (va != null) {
			String name = validatorMap.get(va);
			if (name != null && !name.equals("")) {
				return name;
			} else {
				return addValidatorMap(getStylebyName(styleName), va);
			}
		}
		return "";

	}

	/**
	 * 获取单元格垂直对齐标识
	 * 
	 * @param aligntype
	 *            对其标识
	 * 
	 * @return 0-左对齐；1-居中对齐；2-右对齐
	 */
	protected Integer getVerticalAlignType(short aligntype) {
		switch (VerticalAlignment.forInt(aligntype)) {
		case TOP:
			return null;
		case CENTER:
			return VALIGN_CENTER;
		case BOTTOM:
			return VALIGN_BOTTOM;
		default:
			return null;
		}

	}

	@Override
	public Map<String,Object> readString() throws SpreadImportException {
		 Map<String,Object> map=new HashMap<String, Object>();
		SpreadSchema spreadSchema = this.read();
		map.put("json", JSON.toJSONString(spreadSchema, SerializerFeature.DisableCircularReferenceDetect)) ;
		map.put("formula", this.formulaCell) ;
		return map;
	}
	
	/**
	 * 设置sheet表列数
	 * 
	 * @param data
	 * @param row
	 *            行数
	 */
	protected void setColumnCount(Data data, int row) {
		if (row > data.getColCount())
			data.setColCount(row);

	}

	/**
	 * 添加单元格格式
	 * 
	 * @param cellStyle
	 *            单元格样式
	 * @param styleName
	 *            样式名称
	 */
	protected void setStyleMap(CellStyle cellStyle, String styleName) {
		styleMap.put(getCellStyleDesc(cellStyle), styleName);
	}

	/**
	 * 设置数据有效性区域map
	 * 
	 * @param region
	 * @param validator
	 */
	protected void setValidators(CellRangeAddress region, Validator validator) {
		validators.put(region, validator);
	}

	abstract protected Style createNameStyle(Workbook workbook, CellStyle cellStyle, boolean flag);
}
