package com.yusys.bione.plugin.detail.download.util;

import com.yusys.bione.plugin.detail.download.entity.SheetModel;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析excel
 *
 * @author baifk
 * @Date 2021/5/18 21:21
 **/
public class ParseExcelFile {

    private OPCPackage xlsxPackage;
    private int minColumns;
    private SXSSFWorkbook outWorkbook;
    private int countNum;

    public ParseExcelFile(OPCPackage pkg, int minColumns, int countNum) {
        this.xlsxPackage = pkg;
        this.minColumns = minColumns;
        this.outWorkbook = new SXSSFWorkbook();
        this.countNum = countNum;
    }

    /**
     * 解析excel
     *
     * @param styles
     * @param strings
     * @param sheetName
     * @param sheetInputStream
     * @return java.util.List<java.lang.String [ ]>
     * @Date 2021/5/18 21:21
     * @author baifk
     **/
    public List<String[]> processSheet(StylesTable styles, ReadOnlySharedStringsTable strings,
                                       String sheetName, InputStream sheetInputStream) throws IOException, ParserConfigurationException, SAXException {
        SheetModel sheetModel = new SheetModel();
        sheetModel.setNumberOfColumns(0);
        sheetModel.setNumberOfRows(0);
        sheetModel.setCurSheetName(sheetName);
        long startTime = System.currentTimeMillis();
        InputSource sheetSource = new InputSource(sheetInputStream);
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxFactory.newSAXParser();
        XMLReader sheetParser = saxParser.getXMLReader();
        ContentHandler handler = new XSSFSheetEventHandler(styles, strings, this.minColumns, sheetModel, countNum);
        sheetParser.setContentHandler(handler);
        sheetParser.parse(sheetSource);
        sheetModel.setTimeToProcess(System.currentTimeMillis() - startTime);
        return sheetModel.getList();
    }

    /**
    * 执行excel解析，返回List<String[]>对象
    * 
    * @param sheetName
    * @return java.util.List<java.lang.String[]>
    * @Date 2021/5/18 21:21       
    * @author baifk
    **/
    public List<String[]> process(String sheetName) throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {

        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(this.xlsxPackage);
        XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
        StylesTable styles = xssfReader.getStylesTable();
        XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        List<String[]> list = new ArrayList<>();
        while (iter.hasNext()) {
            InputStream stream = iter.next();
            if (sheetName.equals(iter.getSheetName())) {
                list = processSheet(styles, strings, sheetName, stream);
            }
            stream.close();
        }
        return list;
    }
}
