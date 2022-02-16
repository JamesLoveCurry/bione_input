
package com.yusys.bione.plugin.spreadjs.entity;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 样式数组
 * 
 */
public class Style {

    /**
     * 单元格样式名称. eg:'20% - Accent1'
     * 
     */
    private String name;
    /**
     * 当插入超链接时，该字段有值：超链接;当插入百分比时，该字段值：Percent
     * 
     */
    private String parentName;
    /**
     * 格式化正则字符串，如货币格式化字符串：' _ [$¥-804]* #,##0.00_ ;_ [$¥-804]* \-#,##0.00_ ;_ [$¥-804]* "-"??_ ;_ @_ '，日期格式化字符串：'m/d/yyyy'
     * 
     */
    private String formatter;
    /**
     * 单元格背景色. eg:'Accent 1 79'
     * 
     */
    @Pattern(regexp = "\\w+ \\d+ \\d+|#(\\d|[A-F]|[a-f]){6}")
    private String backColor;
    /**
     * 单元格前景色. eg:'Text 1 0'
     * 
     */
    @Pattern(regexp = "\\w+ \\d+ \\d+|#(\\d|[A-F]|[a-f]){6}")
    private String foreColor;
    /**
     * 字体. eg:'normal normal 14.7px Arial' 字符串中第一个参数为italic，表示倾斜；第二个参数为bold，表示加粗
     * 
     */
    @Pattern(regexp = "(\\w+ )?( )?(\\w+ )?\\d+(.\\d+)?(px|pt)(/\\d+(.\\d+)?px)? (\\w+|[^\\x00-\\xff]+)")
    private String font;
    /**
     * 未测
     * 
     */
    private Integer imeMode;
    /**
     * 主题字体号. eg:'Body'
     * 
     */
    @Pattern(regexp = "Body|Head")
    private String themeFont;
    /**
     * 为数字0表示无下划线；为数字1表示单下划线；为数字2表示双下划线
     * 
     */
    private Integer textDecoration;
    /**
     * 水平对齐方式：0为左对齐，1为居中，2为右对齐，3表示缺省
     * 
     */
    private Integer hAlign;
    /**
     * 垂直对齐方式：0为顶端对齐，1为居中，2为底部对齐，3表示缺省
     * 
     */
    private Integer vAlign;
    /**
     * 单元格是否锁定
     * 
     */
    private Boolean locked;
    /**
     * 单元格带缩进样式
     * 
     */
    private Integer textIndent;
    /**
     * 是否自动换行
     * 
     */
    private Boolean wordWrap;
    /**
     * 当样式包含边框时，才有此对象
     * 
     */
    @Valid
    private StyleBorder borderLeft;
    /**
     * 当样式包含边框时，才有此对象
     * 
     */
    @Valid
    private StyleBorder borderTop;
    /**
     * 当样式包含边框时，才有此对象
     * 
     */
    @Valid
    private StyleBorder borderRight;
    /**
     * 当样式包含边框时，才有此对象
     * 
     */
    @Valid
    private StyleBorder borderBottom;
    /**
     * 单元格带下拉框等样式时，该属性值有效
     * 
     */
    @Valid
    private Validator validator;

    /**
     * 单元格样式名称. eg:'20% - Accent1'
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * 单元格样式名称. eg:'20% - Accent1'
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 当插入超链接时，该字段有值：超链接;当插入百分比时，该字段值：Percent
     * 
     */
    public String getParentName() {
        return parentName;
    }

    /**
     * 当插入超链接时，该字段有值：超链接;当插入百分比时，该字段值：Percent
     * 
     */
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    /**
     * 格式化正则字符串，如货币格式化字符串：' _ [$¥-804]* #,##0.00_ ;_ [$¥-804]* \-#,##0.00_ ;_ [$¥-804]* "-"??_ ;_ @_ '，日期格式化字符串：'m/d/yyyy'
     * 
     */
    public String getFormatter() {
        return formatter;
    }

    /**
     * 格式化正则字符串，如货币格式化字符串：' _ [$¥-804]* #,##0.00_ ;_ [$¥-804]* \-#,##0.00_ ;_ [$¥-804]* "-"??_ ;_ @_ '，日期格式化字符串：'m/d/yyyy'
     * 
     */
    public void setFormatter(String formatter) {
        this.formatter = formatter;
    }

    /**
     * 单元格背景色. eg:'Accent 1 79'
     * 
     */
    public String getBackColor() {
        return backColor;
    }

    /**
     * 单元格背景色. eg:'Accent 1 79'
     * 
     */
    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    /**
     * 单元格前景色. eg:'Text 1 0'
     * 
     */
    public String getForeColor() {
        return foreColor;
    }

    /**
     * 单元格前景色. eg:'Text 1 0'
     * 
     */
    public void setForeColor(String foreColor) {
        this.foreColor = foreColor;
    }

    /**
     * 字体. eg:'normal normal 14.7px Arial' 字符串中第一个参数为italic，表示倾斜；第二个参数为bold，表示加粗
     * 
     */
    public String getFont() {
        return font;
    }

    /**
     * 字体. eg:'normal normal 14.7px Arial' 字符串中第一个参数为italic，表示倾斜；第二个参数为bold，表示加粗
     * 
     */
    public void setFont(String font) {
        this.font = font;
    }

    /**
     * 未测
     * 
     */
    public Integer getImeMode() {
        return imeMode;
    }

    /**
     * 未测
     * 
     */
    public void setImeMode(Integer imeMode) {
        this.imeMode = imeMode;
    }

    /**
     * 主题字体号. eg:'Body'
     * 
     */
    public String getThemeFont() {
        return themeFont;
    }

    /**
     * 主题字体号. eg:'Body'
     * 
     */
    public void setThemeFont(String themeFont) {
        this.themeFont = themeFont;
    }

    /**
     * 为数字0表示无下划线；为数字1表示单下划线；为数字2表示双下划线
     * 
     */
    public Integer getTextDecoration() {
        return textDecoration;
    }

    /**
     * 为数字0表示无下划线；为数字1表示单下划线；为数字2表示双下划线
     * 
     */
    public void setTextDecoration(Integer textDecoration) {
        this.textDecoration = textDecoration;
    }

    /**
     * 水平对齐方式：0为左对齐，1为居中，2为右对齐，3表示缺省
     * 
     */
    public Integer getHAlign() {
        return hAlign;
    }

    /**
     * 水平对齐方式：0为左对齐，1为居中，2为右对齐，3表示缺省
     * 
     */
    public void setHAlign(Integer hAlign) {
        this.hAlign = hAlign;
    }

    /**
     * 垂直对齐方式：0为顶端对齐，1为居中，2为底部对齐，3表示缺省
     * 
     */
    public Integer getVAlign() {
        return vAlign;
    }

    /**
     * 垂直对齐方式：0为顶端对齐，1为居中，2为底部对齐，3表示缺省
     * 
     */
    public void setVAlign(Integer vAlign) {
        this.vAlign = vAlign;
    }

    /**
     * 单元格是否锁定
     * 
     */
    public Boolean getLocked() {
        return locked;
    }

    /**
     * 单元格是否锁定
     * 
     */
    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    /**
     * 单元格带缩进样式
     * 
     */
    public Integer getTextIndent() {
        return textIndent;
    }

    /**
     * 单元格带缩进样式
     * 
     */
    public void setTextIndent(Integer textIndent) {
        this.textIndent = textIndent;
    }

    /**
     * 是否自动换行
     * 
     */
    public Boolean getWordWrap() {
        return wordWrap;
    }

    /**
     * 是否自动换行
     * 
     */
    public void setWordWrap(Boolean wordWrap) {
        this.wordWrap = wordWrap;
    }

    /**
     * 当样式包含边框时，才有此对象
     * 
     */
    public StyleBorder getBorderLeft() {
        return borderLeft;
    }

    /**
     * 当样式包含边框时，才有此对象
     * 
     */
    public void setBorderLeft(StyleBorder borderLeft) {
        this.borderLeft = borderLeft;
    }

    /**
     * 当样式包含边框时，才有此对象
     * 
     */
    public StyleBorder getBorderTop() {
        return borderTop;
    }

    /**
     * 当样式包含边框时，才有此对象
     * 
     */
    public void setBorderTop(StyleBorder borderTop) {
        this.borderTop = borderTop;
    }

    /**
     * 当样式包含边框时，才有此对象
     * 
     */
    public StyleBorder getBorderRight() {
        return borderRight;
    }

    /**
     * 当样式包含边框时，才有此对象
     * 
     */
    public void setBorderRight(StyleBorder borderRight) {
        this.borderRight = borderRight;
    }

    /**
     * 当样式包含边框时，才有此对象
     * 
     */
    public StyleBorder getBorderBottom() {
        return borderBottom;
    }

    /**
     * 当样式包含边框时，才有此对象
     * 
     */
    public void setBorderBottom(StyleBorder borderBottom) {
        this.borderBottom = borderBottom;
    }

    /**
     * 单元格带下拉框等样式时，该属性值有效
     * 
     */
    public Validator getValidator() {
        return validator;
    }

    /**
     * 单元格带下拉框等样式时，该属性值有效
     * 
     */
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((backColor == null) ? 0 : backColor.hashCode());
		result = prime * result
				+ ((borderBottom == null) ? 0 : borderBottom.hashCode());
		result = prime * result
				+ ((borderLeft == null) ? 0 : borderLeft.hashCode());
		result = prime * result
				+ ((borderRight == null) ? 0 : borderRight.hashCode());
		result = prime * result
				+ ((borderTop == null) ? 0 : borderTop.hashCode());
		result = prime * result + ((font == null) ? 0 : font.hashCode());
		result = prime * result
				+ ((foreColor == null) ? 0 : foreColor.hashCode());
		result = prime * result
				+ ((formatter == null) ? 0 : formatter.hashCode());
		result = prime * result + ((hAlign == null) ? 0 : hAlign.hashCode());
		result = prime * result + ((imeMode == null) ? 0 : imeMode.hashCode());
		result = prime * result + ((locked == null) ? 0 : locked.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((parentName == null) ? 0 : parentName.hashCode());
		result = prime * result
				+ ((textDecoration == null) ? 0 : textDecoration.hashCode());
		result = prime * result
				+ ((textIndent == null) ? 0 : textIndent.hashCode());
		result = prime * result
				+ ((themeFont == null) ? 0 : themeFont.hashCode());
		result = prime * result + ((vAlign == null) ? 0 : vAlign.hashCode());
		result = prime * result
				+ ((validator == null) ? 0 : validator.hashCode());
		result = prime * result
				+ ((wordWrap == null) ? 0 : wordWrap.hashCode());
		return result;
	}

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Style other = (Style) obj;
		if (backColor == null) {
			if (other.backColor != null)
				return false;
		} else if (!backColor.equals(other.backColor))
			return false;
		if (font == null) {
			if (other.font != null)
				return false;
		} else if (!font.equals(other.font))
			return false;
		if (borderBottom == null) {
			if (other.borderBottom != null)
				return false;
		} else if (!borderBottom.equals(other.borderBottom))
			return false;
		if (borderLeft == null) {
			if (other.borderLeft != null)
				return false;
		} else if (!borderLeft.equals(other.borderLeft))
			return false;
		if (borderRight == null) {
			if (other.borderRight != null)
				return false;
		} else if (!borderRight.equals(other.borderRight))
			return false;
		if (borderTop == null) {
			if (other.borderTop != null)
				return false;
		} else if (!borderTop.equals(other.borderTop))
			return false;
		if (foreColor == null) {
			if (other.foreColor != null)
				return false;
		} else if (!foreColor.equals(other.foreColor))
			return false;
		if (formatter == null) {
			if (other.formatter != null)
				return false;
		} else if (!formatter.equals(other.formatter))
			return false;
		if (hAlign == null) {
			if (other.hAlign != null)
				return false;
		} else if (!hAlign.equals(other.hAlign))
			return false;
		if (imeMode == null) {
			if (other.imeMode != null)
				return false;
		} else if (!imeMode.equals(other.imeMode))
			return false;
		if (locked == null) {
			if (other.locked != null)
				return false;
		} else if (!locked.equals(other.locked))
			return false;
		if (parentName == null) {
			if (other.parentName != null)
				return false;
		} else if (!parentName.equals(other.parentName))
			return false;
		if (textDecoration == null) {
			if (other.textDecoration != null)
				return false;
		} else if (!textDecoration.equals(other.textDecoration))
			return false;
		if (textIndent == null) {
			if (other.textIndent != null)
				return false;
		} else if (!textIndent.equals(other.textIndent))
			return false;
		if (themeFont == null) {
			if (other.themeFont != null)
				return false;
		} else if (!themeFont.equals(other.themeFont))
			return false;
		if (vAlign == null) {
			if (other.vAlign != null)
				return false;
		} else if (!vAlign.equals(other.vAlign))
			return false;
		if (validator == null) {
			if (other.validator != null)
				return false;
		} else if (!validator.equals(other.validator))
			return false;
		if (wordWrap == null) {
			if (other.wordWrap != null)
				return false;
		} else if (!wordWrap.equals(other.wordWrap))
			return false;
		return true;
	}

}
