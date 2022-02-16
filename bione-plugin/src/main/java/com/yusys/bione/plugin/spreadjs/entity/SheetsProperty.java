
package com.yusys.bione.plugin.spreadjs.entity;

import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * sheet
 * <p>
 * 标签页集合对象
 * 
 */
public class SheetsProperty {

    /**
     * 标签页名称
     * 
     */
    private String name;
    /**
     * 冻结线显示flag
     * 
     */
    private Boolean frozenLineShowFlag;
    /**
     * 是否绑定数据-未测
     * 
     */
    private Object dataBinding;
    /**
     * 冻结行数
     * 
     */
    private Integer frozenRowCount;
    /**
     * 冻结列数
     * 
     */
    private Integer frozenColCount;
    /**
     * 未测
     * 
     */
    private Integer frozenTrailingRowCount;
    /**
     * 未测
     * 
     */
    private Integer frozenTrailingColCount;
    /**
     * 列是否自动增加-未测
     * 
     */
    private Boolean autoGenerateColumns;
    /**
     * 引用样式
     * 
     */
    private Integer referenceStyle;
    /**
     * 放大比例，1表示正常显示比例100%，以此类推
     * 
     */
    private Double ZoomFactor;
    /**
     * 是否显示行区域
     * 
     */
    private Boolean showRowRangeGroup;
    /**
     * 是否显示列区域
     * 
     */
    private Boolean showColumnRangeGroup;
    /**
     * 标签页颜色名称.eg:'#000000'
     * 
     */
    private Object sheetTabColor;
    /**
     * 未测
     * 
     */
    private Integer rowHeaderAutoText;
    /**
     * 未测
     * 
     */
    private Integer colHeaderAutoText;
    /**
     * 未测
     * 
     */
    private Integer rowHeaderAutoTextIndex;
    /**
     * 未测
     * 
     */
    private Integer colHeaderAutoTextIndex;
    /**
     * 行表头是否可见
     * 
     */
    private Boolean rowHeaderVisible;
    /**
     * 列表头是否可见
     * 
     */
    private Boolean colHeaderVisible;
    /**
     * 行表头包括的列数
     * 
     */
    private Integer rowHeaderColCount;
    /**
     * 列表头包括的行数
     * 
     */
    private Integer colHeaderRowCount;
    /**
     * 是否受保护-未测
     * 
     */
    private Boolean isProtected;
    /**
     * 边框颜色-未测
     * 
     */
    private String borderColor;
    /**
     * 边框宽度-未测
     * 
     */
    private Integer borderWidth;
    /**
     * 未测
     * 
     */
    private Boolean allowDragDrop;
    /**
     * 未测
     * 
     */
    private Boolean allowDragFill;
    /**
     * 未测
     * 
     */
    private Boolean allowUndo;
    /**
     * 未测
     * 
     */
    private Boolean allowEditorReservedLocations;
    /**
     * 缺省单元格属性配置
     * 
     */
    @Valid
    private Defaults defaults;
    /**
     * 包含列数
     * 
     */
    private Integer columnCount;
    /**
     * 含数据列的列宽数组
     * 
     */
    @Valid
    private List<SheetSize> columns;
    /**
     * 包含行数
     * 
     */
    private Integer rowCount;
    /**
     * 含数据行的行高数组
     * 
     */
    @Valid
    private List<SheetSize> rows;
    /**
     * 数据存储对象
     * 
     */
    @Valid
    private Data data;
    /**
     * 迷你图片集合管理器，比如EXCEL中的折线，柱状，盈亏图
     * 
     */
    @Valid
    private SparklineGroupManager sparklineGroupManager;
    /**
     * 合并单元格数组
     * 
     */
    @Valid
    private List<Span> spans;
    /**
     * 当前选择的行
     * 
     */
    private Integer activeRow;
    /**
     * 当前选择的列
     * 
     */
    private Integer activeCol;
    /**
     * 文本翻译
     * 
     */
    private Integer textDecoration;
    /**
     * 单元格选中部分
     * 
     */
    @Valid
    private Selections selections;
    /**
     * 选择框的背景颜色
     * 
     */
    private String selectionBackColor;
    /**
     * 选择框的边框颜色
     * 
     */
    private String selectionBorderColor;
    /**
     * 表格线
     * 
     */
    @Valid
    private Gridline gridline;
    /**
     * 文件的主题样式
     * 
     */
    @Valid
    private String theme;
    /**
     * 未知属性
     * 
     */
    @Valid
    private ConditionalFormats conditionalFormats;
    /**
     * 行表头信息
     * 
     */
    @Valid
    private RowHeaderData rowHeaderData;
    /**
     * 列表头信息
     * 
     */
    @Valid
    private ColHeaderData colHeaderData;
    /**
     * 勾选页面导入选项时有用
     * 
     */
    @Valid
    private List<Object> rowHeaderColInfos;
    /**
     * 勾选页面导入选项时有用
     * 
     */
    @Valid
    private List<Object> colHeaderRowInfos;
    /**
     * 未测
     * 
     */
    @Valid
    private List<Object> rowHeaderSpan;
    /**
     * 未测
     * 
     */
    @Valid
    private List<Object> colHeaderSpan;
    /**
     * 表格样式管理对象集合
     * 
     */
    @Valid
    private TableManager tableManager;
    /**
     * 样式数组
     * 
     */
    @Valid
    private Collection<Style> namedStyles;
    /**
     * 未测
     * 
     */
    @Valid
    private List<Object> names;
    /**
     * 行范围集合
     * 
     */
    @Valid
    private RowRangeGroup rowRangeGroup;
    /**
     * 列范围集合
     * 
     */
    @Valid
    private ColRangeGroup colRangeGroup;
    /**
     * 行过滤器-未测
     * 
     */
    private Object rowFilter;
    /**
     * 是否允许单元格自动调整大小
     * 
     */
    private Boolean allowCellOverflow = false;
    /**
     * 悬浮对象管理器,是指直接插入的图片，超链接，公式等对象，注意图片是以位图的形式存储的。
     * 
     */
    @Valid
    private FloatingObjectArray floatingObjectArray;
    /**
     * 当前标签页的索引
     * 
     */
    private Integer Index;
    /**
     * 当前标签页是否被隐藏
     * 
     */
    private Boolean visible = true;

    /**
     * 未测
     * 
     */
    private String _sheetWidth;
   
    /**
     * 标签页名称
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * 标签页名称
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 列是否自动增加-未测
     * 
     */
    public Boolean getAutoGenerateColumns() {
        return autoGenerateColumns;
    }

    /**
     * 列是否自动增加-未测
     * 
     */
    public void setAutoGenerateColumns(Boolean autoGenerateColumns) {
        this.autoGenerateColumns = autoGenerateColumns;
    }

    /**
     * 是否绑定数据-未测
     * 
     */
    public Object getDataBinding() {
        return dataBinding;
    }

    /**
     * 是否绑定数据-未测
     * 
     */
    public void setDataBinding(Object dataBinding) {
        this.dataBinding = dataBinding;
    }

    /**
     * 冻结行数
     * 
     */
    public Integer getFrozenRowCount() {
        return frozenRowCount;
    }

    /**
     * 冻结行数
     * 
     */
    public void setFrozenRowCount(Integer frozenRowCount) {
        this.frozenRowCount = frozenRowCount;
    }

    /**
     * 冻结列数
     * 
     */
    public Integer getFrozenColCount() {
        return frozenColCount;
    }

    /**
     * 冻结列数
     * 
     */
    public void setFrozenColCount(Integer frozenColCount) {
        this.frozenColCount = frozenColCount;
    }

    /**
     * 未测
     * 
     */
    public Integer getFrozenTrailingRowCount() {
        return frozenTrailingRowCount;
    }

    /**
     * 未测
     * 
     */
    public void setFrozenTrailingRowCount(Integer frozenTrailingRowCount) {
        this.frozenTrailingRowCount = frozenTrailingRowCount;
    }

    /**
     * 未测
     * 
     */
    public Integer getFrozenTrailingColCount() {
        return frozenTrailingColCount;
    }

    /**
     * 未测
     * 
     */
    public void setFrozenTrailingColCount(Integer frozenTrailingColCount) {
        this.frozenTrailingColCount = frozenTrailingColCount;
    }

    /**
     * 引用样式
     * 
     */
    public Integer getReferenceStyle() {
        return referenceStyle;
    }

    /**
     * 引用样式
     * 
     */
    public void setReferenceStyle(Integer referenceStyle) {
        this.referenceStyle = referenceStyle;
    }

    /**
     * 放大比例，1表示正常显示比例100%，以此类推
     * 
     */
    public Double getZoomFactor() {
        return ZoomFactor;
    }

    /**
     * 放大比例，1表示正常显示比例100%，以此类推
     * 
     */
    public void setZoomFactor(Double ZoomFactor) {
        this.ZoomFactor = ZoomFactor;
    }

    /**
     * 是否显示行区域
     * 
     */
    public Boolean getShowRowRangeGroup() {
        return showRowRangeGroup;
    }

    /**
     * 是否显示行区域
     * 
     */
    public void setShowRowRangeGroup(Boolean showRowRangeGroup) {
        this.showRowRangeGroup = showRowRangeGroup;
    }

    /**
     * 是否显示列区域
     * 
     */
    public Boolean getShowColumnRangeGroup() {
        return showColumnRangeGroup;
    }

    /**
     * 是否显示列区域
     * 
     */
    public void setShowColumnRangeGroup(Boolean showColumnRangeGroup) {
        this.showColumnRangeGroup = showColumnRangeGroup;
    }

    /**
     * 标签页颜色名称.eg:'#000000'
     * 
     */
    public Object getSheetTabColor() {
        return sheetTabColor;
    }

    /**
     * 标签页颜色名称.eg:'#000000'
     * 
     */
    public void setSheetTabColor(Object sheetTabColor) {
        this.sheetTabColor = sheetTabColor;
    }

    /**
     * 是否显示冻结线
     * 
     */
    public void setFrozenLineShowFlag(Boolean frozenLineShowFlag) {
        this.frozenLineShowFlag = frozenLineShowFlag;
    }
    
    /**
     * 是否显示冻结线
     * 
     */
    public Boolean getFrozenLineShowFlag() {
        return frozenLineShowFlag;
    }

    /**
     * 未测
     * 
     */
    public Integer getRowHeaderAutoText() {
        return rowHeaderAutoText;
    }

    /**
     * 未测
     * 
     */
    public void setRowHeaderAutoText(Integer rowHeaderAutoText) {
        this.rowHeaderAutoText = rowHeaderAutoText;
    }

    /**
     * 未测
     * 
     */
    public Integer getColHeaderAutoText() {
        return colHeaderAutoText;
    }

    /**
     * 未测
     * 
     */
    public void setColHeaderAutoText(Integer colHeaderAutoText) {
        this.colHeaderAutoText = colHeaderAutoText;
    }

    /**
     * 未测
     * 
     */
    public Integer getRowHeaderAutoTextIndex() {
        return rowHeaderAutoTextIndex;
    }

    /**
     * 未测
     * 
     */
    public void setRowHeaderAutoTextIndex(Integer rowHeaderAutoTextIndex) {
        this.rowHeaderAutoTextIndex = rowHeaderAutoTextIndex;
    }

    /**
     * 未测
     * 
     */
    public Integer getColHeaderAutoTextIndex() {
        return colHeaderAutoTextIndex;
    }

    /**
     * 未测
     * 
     */
    public void setColHeaderAutoTextIndex(Integer colHeaderAutoTextIndex) {
        this.colHeaderAutoTextIndex = colHeaderAutoTextIndex;
    }

    /**
     * 行表头是否可见
     * 
     */
    public Boolean getRowHeaderVisible() {
        return rowHeaderVisible;
    }

    /**
     * 行表头是否可见
     * 
     */
    public void setRowHeaderVisible(Boolean rowHeaderVisible) {
        this.rowHeaderVisible = rowHeaderVisible;
    }

    /**
     * 列表头是否可见
     * 
     */
    public Boolean getColHeaderVisible() {
        return colHeaderVisible;
    }

    /**
     * 列表头是否可见
     * 
     */
    public void setColHeaderVisible(Boolean colHeaderVisible) {
        this.colHeaderVisible = colHeaderVisible;
    }

    /**
     * 行表头包括的列数
     * 
     */
    public Integer getRowHeaderColCount() {
        return rowHeaderColCount;
    }

    /**
     * 行表头包括的列数
     * 
     */
    public void setRowHeaderColCount(Integer rowHeaderColCount) {
        this.rowHeaderColCount = rowHeaderColCount;
    }

    /**
     * 列表头包括的行数
     * 
     */
    public Integer getColHeaderRowCount() {
        return colHeaderRowCount;
    }

    /**
     * 列表头包括的行数
     * 
     */
    public void setColHeaderRowCount(Integer colHeaderRowCount) {
        this.colHeaderRowCount = colHeaderRowCount;
    }

    /**
     * 是否受保护-未测
     * 
     */
    public Boolean getIsProtected() {
        return isProtected;
    }

    /**
     * 是否受保护-未测
     * 
     */
    public void setIsProtected(Boolean isProtected) {
        this.isProtected = isProtected;
    }

    /**
     * 边框颜色-未测
     * 
     */
    public String getBorderColor() {
        return borderColor;
    }

    /**
     * 边框颜色-未测
     * 
     */
    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * 边框宽度-未测
     * 
     */
    public Integer getBorderWidth() {
        return borderWidth;
    }

    /**
     * 边框宽度-未测
     * 
     */
    public void setBorderWidth(Integer borderWidth) {
        this.borderWidth = borderWidth;
    }

    /**
     * 未测
     * 
     */
    public Boolean getAllowDragDrop() {
        return allowDragDrop;
    }

    /**
     * 未测
     * 
     */
    public void setAllowDragDrop(Boolean allowDragDrop) {
        this.allowDragDrop = allowDragDrop;
    }

    /**
     * 未测
     * 
     */
    public Boolean getAllowDragFill() {
        return allowDragFill;
    }

    /**
     * 未测
     * 
     */
    public void setAllowDragFill(Boolean allowDragFill) {
        this.allowDragFill = allowDragFill;
    }

    /**
     * 未测
     * 
     */
    public Boolean getAllowUndo() {
        return allowUndo;
    }

    /**
     * 未测
     * 
     */
    public void setAllowUndo(Boolean allowUndo) {
        this.allowUndo = allowUndo;
    }

    /**
     * 未测
     * 
     */
    public Boolean getAllowEditorReservedLocations() {
        return allowEditorReservedLocations;
    }

    /**
     * 未测
     * 
     */
    public void setAllowEditorReservedLocations(Boolean allowEditorReservedLocations) {
        this.allowEditorReservedLocations = allowEditorReservedLocations;
    }

    /**
     * 缺省单元格属性配置
     * 
     */
    public Defaults getDefaults() {
        return defaults;
    }

    /**
     * 缺省单元格属性配置
     * 
     */
    public void setDefaults(Defaults defaults) {
        this.defaults = defaults;
    }

    /**
     * 包含列数
     * 
     */
    public Integer getColumnCount() {
        return columnCount;
    }

    /**
     * 包含列数
     * 
     */
    public void setColumnCount(Integer columnCount) {
        this.columnCount = columnCount;
    }

    /**
     * 含数据列的列宽数组
     * 
     */
    public List<SheetSize> getColumns() {
        return columns;
    }

    /**
     * 含数据列的列宽数组
     * 
     */
    public void setColumns(List<SheetSize> columns) {
        this.columns = columns;
    }

    /**
     * 包含行数
     * 
     */
    public Integer getRowCount() {
        return rowCount;
    }

    /**
     * 包含行数
     * 
     */
    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * 含数据行的行高数组
     * 
     */
    public List<SheetSize> getRows() {
        return rows;
    }

    /**
     * 含数据行的行高数组
     * 
     */
    public void setRows(List<SheetSize> rows) {
        this.rows = rows;
    }

    /**
     * 数据存储对象
     * 
     */
    public Data getData() {
        return data;
    }

    /**
     * 数据存储对象
     * 
     */
    public void setData(Data data) {
        this.data = data;
    }

    /**
     * 迷你图片集合管理器，比如EXCEL中的折线，柱状，盈亏图
     * 
     */
    public SparklineGroupManager getSparklineGroupManager() {
        return sparklineGroupManager;
    }

    /**
     * 迷你图片集合管理器，比如EXCEL中的折线，柱状，盈亏图
     * 
     */
    public void setSparklineGroupManager(SparklineGroupManager sparklineGroupManager) {
        this.sparklineGroupManager = sparklineGroupManager;
    }

    /**
     * 合并单元格数组
     * 
     */
    public List<Span> getSpans() {
        return spans;
    }

    /**
     * 合并单元格数组
     * 
     */
    public void setSpans(List<Span> spans) {
        this.spans = spans;
    }

    /**
     * 当前选择的行
     * 
     */
    public Integer getActiveRow() {
        return activeRow;
    }

    /**
     * 当前选择的行
     * 
     */
    public void setActiveRow(Integer activeRow) {
        this.activeRow = activeRow;
    }

    /**
     * 当前选择的列
     * 
     */
    public Integer getActiveCol() {
        return activeCol;
    }

    /**
     * 当前选择的列
     * 
     */
    public void setActiveCol(Integer activeCol) {
        this.activeCol = activeCol;
    }

    /**
     * 文本翻译
     * 
     */
    public Integer getTextDecoration() {
        return textDecoration;
    }

    /**
     * 文本翻译
     * 
     */
    public void setTextDecoration(Integer textDecoration) {
        this.textDecoration = textDecoration;
    }

    /**
     * 单元格选中部分
     * 
     */
    public Selections getSelections() {
        return selections;
    }

    /**
     * 单元格选中部分
     * 
     */
    public void setSelections(Selections selections) {
        this.selections = selections;
    }

    /**
     * 选择框的背景颜色
     * 
     */
    public String getSelectionBackColor() {
        return selectionBackColor;
    }

    /**
     * 选择框的背景颜色
     * 
     */
    public void setSelectionBackColor(String selectionBackColor) {
        this.selectionBackColor = selectionBackColor;
    }

    /**
     * 选择框的边框颜色
     * 
     */
    public String getSelectionBorderColor() {
        return selectionBorderColor;
    }

    /**
     * 选择框的边框颜色
     * 
     */
    public void setSelectionBorderColor(String selectionBorderColor) {
        this.selectionBorderColor = selectionBorderColor;
    }

    /**
     * 表格线
     * 
     */
    public Gridline getGridline() {
        return gridline;
    }

    /**
     * 表格线
     * 
     */
    public void setGridline(Gridline gridline) {
        this.gridline = gridline;
    }

    /**
     * 文件的主题样式
     * 
     */
	public void setTheme(String theme) {
		this.theme = theme;
	}
    /**
     * 文件的主题样式
     * 
     */
    public String getTheme() {
		return theme;
	}

    /**
     * 未知属性
     * 
     */
    public ConditionalFormats getConditionalFormats() {
        return conditionalFormats;
    }

    /**
     * 未知属性
     * 
     */
    public void setConditionalFormats(ConditionalFormats conditionalFormats) {
        this.conditionalFormats = conditionalFormats;
    }

    /**
     * 行表头信息
     * 
     */
    public RowHeaderData getRowHeaderData() {
        return rowHeaderData;
    }

    /**
     * 行表头信息
     * 
     */
    public void setRowHeaderData(RowHeaderData rowHeaderData) {
        this.rowHeaderData = rowHeaderData;
    }

    /**
     * 列表头信息
     * 
     */
    public ColHeaderData getColHeaderData() {
        return colHeaderData;
    }

    /**
     * 列表头信息
     * 
     */
    public void setColHeaderData(ColHeaderData colHeaderData) {
        this.colHeaderData = colHeaderData;
    }

    /**
     * 勾选页面导入选项时有用
     * 
     */
    public List<Object> getRowHeaderColInfos() {
        return rowHeaderColInfos;
    }

    /**
     * 勾选页面导入选项时有用
     * 
     */
    public void setRowHeaderColInfos(List<Object> rowHeaderColInfos) {
        this.rowHeaderColInfos = rowHeaderColInfos;
    }

    /**
     * 勾选页面导入选项时有用
     * 
     */
    public List<Object> getColHeaderRowInfos() {
        return colHeaderRowInfos;
    }

    /**
     * 勾选页面导入选项时有用
     * 
     */
    public void setColHeaderRowInfos(List<Object> colHeaderRowInfos) {
        this.colHeaderRowInfos = colHeaderRowInfos;
    }

    /**
     * 未测
     * 
     */
    public List<Object> getRowHeaderSpan() {
        return rowHeaderSpan;
    }

    /**
     * 未测
     * 
     */
    public void setRowHeaderSpan(List<Object> rowHeaderSpan) {
        this.rowHeaderSpan = rowHeaderSpan;
    }

    /**
     * 未测
     * 
     */
    public List<Object> getColHeaderSpan() {
        return colHeaderSpan;
    }

    /**
     * 未测
     * 
     */
    public void setColHeaderSpan(List<Object> colHeaderSpan) {
        this.colHeaderSpan = colHeaderSpan;
    }

    /**
     * 表格样式管理对象集合
     * 
     */
    public TableManager getTableManager() {
        return tableManager;
    }

    /**
     * 表格样式管理对象集合
     * 
     */
    public void setTableManager(TableManager tableManager) {
        this.tableManager = tableManager;
    }

    /**
     * 样式数组
     * 
     */
    public Collection<Style> getNamedStyles() {
        return namedStyles;
    }

    /**
     * 样式数组
     * 
     */
    public void setNamedStyles(Collection<Style> namedStyles) {
        this.namedStyles = namedStyles;
    }

    /**
     * 未测
     * 
     */
    public List<Object> getNames() {
        return names;
    }

    /**
     * 未测
     * 
     */
    public void setNames(List<Object> names) {
        this.names = names;
    }

    /**
     * 行范围集合
     * 
     */
    public RowRangeGroup getRowRangeGroup() {
        return rowRangeGroup;
    }

    /**
     * 行范围集合
     * 
     */
    public void setRowRangeGroup(RowRangeGroup rowRangeGroup) {
        this.rowRangeGroup = rowRangeGroup;
    }

    /**
     * 列范围集合
     * 
     */
    public ColRangeGroup getColRangeGroup() {
        return colRangeGroup;
    }

    /**
     * 列范围集合
     * 
     */
    public void setColRangeGroup(ColRangeGroup colRangeGroup) {
        this.colRangeGroup = colRangeGroup;
    }

    /**
     * 行过滤器-未测
     * 
     */
    public Object getRowFilter() {
        return rowFilter;
    }

    /**
     * 行过滤器-未测
     * 
     */
    public void setRowFilter(Object rowFilter) {
        this.rowFilter = rowFilter;
    }

    /**
     * 是否允许单元格自动调整大小
     * 
     */
    public Boolean getAllowCellOverflow() {
        return allowCellOverflow;
    }

    /**
     * 是否允许单元格自动调整大小
     * 
     */
    public void setAllowCellOverflow(Boolean allowCellOverflow) {
        this.allowCellOverflow = allowCellOverflow;
    }

    /**
     * 悬浮对象管理器,是指直接插入的图片，超链接，公式等对象，注意图片是以位图的形式存储的。
     * 
     */
    public FloatingObjectArray getFloatingObjectArray() {
        return floatingObjectArray;
    }

    /**
     * 悬浮对象管理器,是指直接插入的图片，超链接，公式等对象，注意图片是以位图的形式存储的。
     * 
     */
    public void setFloatingObjectArray(FloatingObjectArray floatingObjectArray) {
        this.floatingObjectArray = floatingObjectArray;
    }

    /**
     * 当前标签页的索引
     * 
     */
    public Integer getIndex() {
        return Index;
    }

    /**
     * 当前标签页的索引
     * 
     */
    public void setIndex(Integer Index) {
        this.Index = Index;
    }

    /**
     * 当前标签页是否被隐藏
     * 
     */
    public Boolean getVisible() {
        return visible;
    }

    /**
     * 当前标签页是否被隐藏
     * 
     */
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
    
    public String get_sheetWidth() {
		return _sheetWidth;
	}
	public void set_sheetWidth(String _sheetWidth) {
		this._sheetWidth = _sheetWidth;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }
}
