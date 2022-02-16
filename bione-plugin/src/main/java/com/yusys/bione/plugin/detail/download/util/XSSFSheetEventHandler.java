package com.yusys.bione.plugin.detail.download.util;

import com.yusys.bione.plugin.detail.download.entity.SheetModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * <pre>
 * Title: 快速解析excel工具类
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 9:56 2020/10/29
 */
public class XSSFSheetEventHandler extends DefaultHandler {

    /**
     * Table with styles
     */
    private StylesTable stylesTable;

    /**
     * Table with unique strings
     */
    private ReadOnlySharedStringsTable sharedStringsTable;

    private List<String[]> list = new ArrayList();

    private SheetModel sheetModel;

    /**
     * Number of columns to read starting with leftmost
     */
    private final int minColumnCount;

    // Set when V start element is seen
    private boolean vIsOpen;
    private String sqref;

    // Set when cell start element is seen;
    // used when cell close element is seen.
    private XSSFDataTypes nextDataType;

    // Used to format numeric cell values.
    private short formatIndex;
    private String formatString;
    private final DataFormatter formatter;
    // The last column printed to the output stream
    private int lastColumnNumber = -1;

    private String label = null;
    private String oldLabel = null;
    // Gathers characters as they are seen.
    private StringBuffer value;
    private int dataType;
    private int countNum;
    private char newR = 0;
    private char oldR = 0;
    // 解析结果
    private List<String> valueList = new ArrayList();
    // 解析excel标签
    public enum XSSFDataTypes {
        BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER;
    }
    /**
     *
     * <pre>
     * Title: 构造函数
     * </pre>
     * @author miaokx
     * @version 1.00.00
     * @date 9:59 2020/10/29
     */
    public XSSFSheetEventHandler(StylesTable styles,
                            ReadOnlySharedStringsTable strings, int cols, SheetModel sheetModel, int countNum) {
        this.stylesTable = styles;
        this.sharedStringsTable = strings;
        this.minColumnCount = cols;
        this.value = new StringBuffer();
        this.nextDataType = XSSFDataTypes.NUMBER;
        this.formatter = new DataFormatter();
        this.sheetModel = sheetModel;
        this.countNum = countNum;
    }
    /**
     *
     * <pre>
     * Title: 解析一格开始
     * </pre>
     * @author miaokx
     * @version 1.00.00
     * @date 10:00 2020/10/29
     */
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {
        // 标签判断，如果连续两个C标签，则list添加一个空值
        oldLabel = label;
        label = name;
        if ("inlineStr".equals(name) || "v".equals(name) || "t".equals(name)) {
            vIsOpen = true;
            // Clear contents cache
            value.setLength(0);
        // c => cell
        } else if ("c".equals(name)) {
            // Get the cell reference
            String r = attributes.getValue("r");
            if (StringUtils.isNotEmpty(oldLabel) && oldLabel.equals(label)) {
                valueList.add("");
            }

            // Set up defaults.
            this.nextDataType = XSSFDataTypes.NUMBER;
            this.formatIndex = -1;
            this.formatString = null;
            String cellType = attributes.getValue("t");
            String cellStyleStr = attributes.getValue("s");
            char[] chars = r.toCharArray();
            for (int i = chars.length-1; i >=0 ; i--) {
                if ((chars[i]+"").getBytes().length==1 && 'A'<=chars[i]&&chars[i]<='Z'){
                    newR = chars[i];
                    break;
                }
            }
            if (oldR == 0) {
                if (newR !='A') {
                    int result =  newR - 'A';
                    for (int i = 0; i<result; i++) {
                        valueList.add("");
                    }
                }
            }
            if (newR !=0 && oldR != 0){
               int result =  newR - oldR - 1;
               if (result < 0) {
                   result =  26 + result;
               }
               if (chars.length > 2 && result!= 0) {
            	   result =  26 + result;
               }
               for (int i = 0; i<result; i++) {
                   valueList.add("");
               }
            }
            oldR = newR;

            if ("b".equals(cellType)) {
                nextDataType = XSSFDataTypes.BOOL;
            }else if ("e".equals(cellType)) {
                nextDataType = XSSFDataTypes.ERROR;
            }else if ("inlineStr".equals(cellType)) {
                nextDataType = XSSFDataTypes.INLINESTR;
            } else if ("s".equals(cellType)) {
                nextDataType = XSSFDataTypes.SSTINDEX;
            } else if ("str".equals(cellType)) {
                nextDataType = XSSFDataTypes.FORMULA;
            }else if (cellStyleStr != null) {
                // It's a number, but almost certainly one
                // with a special style or format
                int styleIndex = Integer.parseInt(cellStyleStr);
                XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                this.formatIndex = style.getDataFormat();
                this.formatString = style.getDataFormatString();
                if (this.formatString == null) {
                    this.formatString = BuiltinFormats
                            .getBuiltinFormat(this.formatIndex);
                }
            }
        }else if ("dataValidation".equals(name)){

            String dvXmlType = attributes.getValue("type");
            sqref = attributes.getValue("sqref");
            if(dvXmlType.equals("list")){
                dataType = DataValidationConstraint.ValidationType.LIST;
                System.out.println("List Type data ");
            }
            System.out.println("At data validation start");

        }else if(name.startsWith("formula")){
            vIsOpen = true;
            value = new StringBuffer();
            System.out.println("At Formula start");
        }

    }
    /**
     *
     * <pre>
     * Title: 解析一格结束
     * </pre>
     * @author miaokx
     * @version 1.00.00
     * @date  2020/10/29
     */
    public void endElement(String uri, String localName, String name)
            throws SAXException {



        String thisStr = null;

        if (vIsOpen) {
            if ("v".equals(name) || "is".equals(name)) {
                vIsOpen = false;
            } else if (name.startsWith("formula")) {
                char last = name.charAt(name.length() - 1);
                switch (last) {
                    case '1':
                        System.out.println(""+value.toString());
                        break;
                    case '2':
                        System.out.println(value.toString());
                        break;
                }

                vIsOpen = false;
                value = null;
            }
        }


        // v => contents of a cell
        if ("v".equals(name) || "t".equals(name)) {
            // Process the value contents as required.
            // Do now, as characters() may be called more than once
            switch (nextDataType) {

                case BOOL:
                    char first = value.charAt(0);
                    thisStr = first == '0' ? "FALSE" : "TRUE";
                    break;

                case ERROR:
                    thisStr = "\"ERROR:" + value.toString() + '"';
                    break;

                case FORMULA:
                    // A formula could result in a string value,
                    // so always add double-quote characters.
                    thisStr =  value.toString() ;
                    break;

                case INLINESTR:
                    XSSFRichTextString rtsi = new XSSFRichTextString(value
                            .toString());
                    thisStr =  rtsi.toString();
                    break;

                case SSTINDEX:
                    String sstIndex = value.toString();
                    try {
                        int idx = Integer.parseInt(sstIndex);
                        XSSFRichTextString rtss = new XSSFRichTextString(
                                sharedStringsTable.getEntryAt(idx));
                        thisStr =  rtss.toString() ;
                    } catch (NumberFormatException ex) {

                    }
                    break;

                case NUMBER:
                    String n = value.toString();
                    if (this.formatString != null) {
                        thisStr = formatter.formatRawCellContents(Double
                                        .parseDouble(n), this.formatIndex,
                                this.formatString);
                    } else {
                        thisStr = n;
                    }
                    break;

                default:
                    thisStr = "(TODO: Unexpected type: " + nextDataType + ")";
                    break;
            }
            valueList.add(thisStr);

            // Output after we've seen the string contents
            // Emit commas for any fields that were missing on this row
            if (lastColumnNumber == -1) {
                lastColumnNumber = 0;
            }

        } else if ("row".equals(name)) {
            sheetModel.setNumberOfRows(sheetModel.getNumberOfRows()+1);
            int result = countNum - valueList.size();
            for (int a = 0; a < result; a++) {
                valueList.add("");
            }
            String[] values = new String[valueList.size()];
            valueList.toArray(values);
            list.add(values);
            valueList = new ArrayList<>();
            oldR = 0;
        }

        else if ("dataValidation".equals(name)) {

            if (dataType == DataValidationConstraint.ValidationType.LIST)
            {

            }
            System.out.println("At datavalidation end");
        }
        else if ("worksheet".equals(name)){
            sheetModel.setList(list);
        }

    }

    /**
     *
     * <pre>
     * Title: 解析每个格子的结果
     * </pre>
     * @author miaokx
     * @version 1.00.00
     * @date 10:01 2020/10/29
     */
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (vIsOpen) {
            value.append(ch, start, length);
        }
    }
}
