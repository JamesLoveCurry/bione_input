package com.yusys.bione.frame.util.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hpsf.CustomProperties;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import com.yusys.bione.comp.utils.FilepathValidateUtils;

/**
 * Excel包装类<br>
 * <br>
 * 在读取时，通过POIFSFileSystem、OPCPackage等机制读取；<br>
 * <br>
 * 在写入时，适用于大数据量下Excel导出，要求各行按照顺序导出，不支持乱序<br>
 * <br>
 * 导出时，根据设置的单文件最大行数和导出的Excel文件路径决定导出的最终结果：<br>
 * <ul><li>
 * 将根据倾向路径扩展名判断生成.xls文件还是.xlsx文件；
 * </li><li>
 * 如果结果行数没有超过单文件最大行数，以Excel文件倾向路径作为导出文件路径；
 * </li><li>
 * 如果结果行数超过了单文件最大行数，以Excel倾向路径和文件顺序逐个导出到Excel文件，并保证每个文件行数不超过单文件最大行数，最终所有导出的Excel文件压缩到.zip文件。<br>
 * 例如，Excel倾向名是"abc.xls"，那么最终以"abc.zip"文件名导出，zip文件中包含"abc_1.xls"、"abc_2.xls"，等等<br>
 * </li></ul>
 * 包装类入口是WorkbookWrapper类，与POI的Workbook接口基本一致，但进行了以下扩展：<br>
 * <ul><li>
 * 在导出时，要求给出Excel文件倾向路径，并可以设置单文件最大行数，最后需要通过getResultFilePath方法获得最终结果文件路径；
 * </li><li>
 * 无论读取还是导出，全部完成后，需要调用close方法关闭输出文件，并释放资源。
 * </li></ul>
 * 使用示例：<br>
 * <pre>
 * 	WorkbookWrapper workbook = WorkbookWrapper.open("E:\\temp\\out.xls");
 * 	...
 * 	IOUtils.closeQuietly(workbook);
 * </pre>
 * 或者<br>
 * <pre>
 * 	WorkbookWrapper workbook = WorkbookWrapper.create("E:\\temp\\out.xls");
 *	workbook.setMaxRowCount(2048);
 *	Sheet sheet = workbook.createSheet();
 *	Font font = workbook.createFont();
 *	font.setItalic(true);
 *	CellStyle cellStyle = workbook.createCellStyle();
 *	cellStyle.setFont(font);
 *	for (int rowIdx = 0; rowIdx < 10; rowIdx ++) {
 *	    Row row = sheet.createRow(rowIdx);
 *	    for (int colIdx = 0; colIdx < 2; colIdx ++) {
 *	        Cell cell = row.createCell(colIdx);
 *	        cell.setCellStyle(cellStyle);
 *	        cell.setCellValue(rowIdx + "_" + colIdx);
 *	    }
 *	}
 *	IOUtils.closeQuietly(workbook);
 *	System.out.println(workbook.getResultFilePath());
 * </pre>
 */
public class WorkbookWrapper implements Workbook {

	private List<String> filePathList;

	private Workbook workbook;

	private POIFSFileSystem poiFSFileSystem;

	private OPCPackage opcPackage;

	/**
	 * 导出的Excel文件倾向路径
	 */
	private String preferFilePath;

	/**
	 * 导出文件名
	 */
	private String resultFilePath;

	/**
	 * 单文件最大行数，对.xls文件，默认10240；对.xlsx文件，默认65536<br>
	 * 此字段只有当compress为true时生效
	 */
	private int maxRowCount;

	/**
	 * 是否压缩，默认true
	 */
	private boolean compress;

	/**
	 * 打开待读取文件
	 * @param filePath 读取的Excel文件倾向路径
	 * @return 待读取文件
	 * @throws IOException
	 * @see WorkbookWrapper
	 */
	public static WorkbookWrapper openExcel(String filePath) throws IOException {
		WorkbookWrapper wrapper = new WorkbookWrapper();
		if (FilepathValidateUtils.validateFilepath(filePath)) {
			try {
				wrapper.poiFSFileSystem = new POIFSFileSystem(new File(filePath), true);
				wrapper.workbook = new HSSFWorkbook(wrapper.poiFSFileSystem, false);
			} catch (OfficeXmlFileException e) {
				try {
					wrapper.opcPackage = OPCPackage.open(filePath, PackageAccess.READ);
					wrapper.workbook = new XSSFWorkbook(wrapper.opcPackage);
				} catch (InvalidFormatException e2) {
					e2.printStackTrace();
					return null;
				}
			}
		}
		return wrapper;
	}
	
	//打开待读取加密excel文件
	public static WorkbookWrapper openExcel(String filePath, String password) throws IOException, EncryptedDocumentException, InvalidFormatException {
		WorkbookWrapper wrapper = new WorkbookWrapper();
		if (FilepathValidateUtils.validateFilepath(filePath)) {
			wrapper.workbook = WorkbookFactory.create(new File(filePath), password);
		}
		return wrapper;
	}
	/**
	 * 读取Excel模板
	 * @param templatePath 模板路径
	 * @return
	 * @throws IOException
	 */
	public static WorkbookWrapper open(String templatePath,String preferFilePath) throws IOException {
		WorkbookWrapper wrapper = new WorkbookWrapper();
		wrapper.filePathList = new ArrayList<String>();
		InputStream tempStream = null;
		if (FilepathValidateUtils.validateFilepath(templatePath)) {
			try {
				tempStream = new FileInputStream(new File(templatePath));
				wrapper.workbook = new HSSFWorkbook(tempStream);
				if (preferFilePath != null && ! preferFilePath.startsWith(".")) {
					wrapper.preferFilePath = preferFilePath.replace('\\', File.separatorChar).replace('/', File.separatorChar);
				}
			} catch (OfficeXmlFileException e) {
				try {
					wrapper.opcPackage = OPCPackage.open(templatePath, PackageAccess.READ);
					wrapper.workbook = new XSSFWorkbook(wrapper.opcPackage);
				} catch (InvalidFormatException e2) {
					e2.printStackTrace();
					return null;
				}
			}finally {
				IOUtils.closeQuietly(tempStream);
			}
		}
		return wrapper;
	}
	/**
	 * 创建导出文件，并默认压缩
	 * @param preferFilePath 导出的Excel文件倾向路径
	 * @return 导出文件
	 * @see WorkbookWrapper
	 */
	public static WorkbookWrapper create(String preferFilePath) {
		return create(preferFilePath, true);
	}
	
	/**
	 * 创建导出文件
	 * @param preferFilePath 导出的Excel文件倾向路径
	 * @param compress 是否压缩
	 * @return 导出文件
	 * @see WorkbookWrapper
	 */
	public static WorkbookWrapper create(String preferFilePath, boolean compress) {
		WorkbookWrapper wrapper = new WorkbookWrapper();
		wrapper.filePathList = new ArrayList<String>();
		wrapper.setCompress(compress);
		if (preferFilePath != null && ! preferFilePath.startsWith(".")) {
			wrapper.preferFilePath = preferFilePath.replace('\\', File.separatorChar).replace('/', File.separatorChar);
		}
		if (preferFilePath != null && preferFilePath.endsWith(".xls")) {
			wrapper.setMaxRowCount(compress ? 10240 : Integer.MAX_VALUE);
			wrapper.workbook = new HSSFWorkbook();
		} else if (compress) {
			wrapper.setMaxRowCount(65536);
			wrapper.workbook = new XSSFWorkbook();
		} else {
			wrapper.setMaxRowCount(Integer.MAX_VALUE);
			wrapper.workbook = new SXSSFWorkbook();
		}
		return wrapper;
	}

	/**
	 * 创建导出文件
	 * @param preferFilePath 导出的Excel文件倾向路径
	 * @param compress 是否压缩
	 * @return 导出文件
	 * @see WorkbookWrapper
	 */
	@SuppressWarnings("resource")
	public static WorkbookWrapper create(String preferFilePath, boolean compress, Map<String, Object> param) {
		WorkbookWrapper wrapper = new WorkbookWrapper();
		wrapper.filePathList = new ArrayList<String>();
		wrapper.setCompress(compress);
		if (preferFilePath != null && ! preferFilePath.startsWith(".")) {
			wrapper.preferFilePath = preferFilePath.replace('\\', File.separatorChar).replace('/', File.separatorChar);
		}
		if (preferFilePath != null && preferFilePath.endsWith(".xls")) {
			wrapper.setMaxRowCount(compress ? 10240 : Integer.MAX_VALUE);
			HSSFWorkbook hSSFWorkbook = new HSSFWorkbook();
			hSSFWorkbook.createInformationProperties();
			DocumentSummaryInformation information = hSSFWorkbook.getDocumentSummaryInformation();
			CustomProperties customProperties = new CustomProperties();
			customProperties.put("repId", param.get("repId").toString());
			customProperties.put("ver", param.get("tmpVersionId").toString());
			information.setCustomProperties(customProperties);
			wrapper.workbook = hSSFWorkbook;	
		} else if (compress) {
			wrapper.setMaxRowCount(65536);
			wrapper.workbook = new XSSFWorkbook();
		} else {
			wrapper.setMaxRowCount(Integer.MAX_VALUE);
			wrapper.workbook = new SXSSFWorkbook();
		}
		return wrapper;
	}

	private void closeExcelFile(boolean finish) throws IOException {
		if (poiFSFileSystem != null || opcPackage != null) {
			IOUtils.closeQuietly(poiFSFileSystem);
			IOUtils.closeQuietly(opcPackage);
		}
		if (workbook == null) {
			return;
		}
		try {
			if (preferFilePath != null) {
				String outputFilePath;
				if (finish && filePathList.size() == 0) {
					outputFilePath = preferFilePath;
				} else {
					StringBuilder sb = new StringBuilder();
					int idx = preferFilePath.lastIndexOf('.');
					if (idx >= 0) {
						sb.append(preferFilePath.substring(0, idx));
						sb.append('_').append(filePathList.size() + 1);
						sb.append(preferFilePath.substring(idx));
					} else {
						sb.append(preferFilePath);
						sb.append('_').append(filePathList.size() + 1);
					}
					outputFilePath = sb.toString();
				}
				if (FilepathValidateUtils.validateFilepath(outputFilePath)) {
					File dir = new File(outputFilePath).getParentFile();
					if (! dir.exists()) {
						dir.mkdirs();
					}
					OutputStream out = null;
					try {
						out = new FileOutputStream(outputFilePath);
						workbook.write(out);
					} finally {
						IOUtils.closeQuietly(out);
					}
					filePathList.add(outputFilePath);
				}
			}
		} finally {
			IOUtils.closeQuietly(workbook);
			workbook = null;
		}
	}

	void refresh() throws IOException {
		closeExcelFile(false);
		if (preferFilePath.endsWith(".xls")) {
			workbook = new HSSFWorkbook();
		} else {
			workbook = new XSSFWorkbook();
		}
	}

	int getStartRow() {
		return filePathList.size() * maxRowCount;
	}

	int getEndRow() {
		return maxRowCount - 1 + getStartRow();
	}

	public ThemesTable getTheme() {
		if (workbook instanceof XSSFWorkbook) {
			return ((XSSFWorkbook)workbook).getTheme();
		}
		return null;
	}

	@Override
	public Iterator<Sheet> iterator() {
		return workbook.iterator();
	}

	@Override
	public int addOlePackage(byte[] oleData, String label, String fileName, String command) throws IOException {
		return workbook.addOlePackage(oleData, label, fileName, command);
	}

	@Override
	public int addPicture(byte[] pictureData, int format) {
		return workbook.addPicture(pictureData, format);
	}

	@Override
	public void addToolPack(UDFFinder toopack) {
		workbook.addToolPack(toopack);
	}

	@Override
	public Sheet cloneSheet(int sheetNum) {
		return workbook.cloneSheet(sheetNum);
	}

	@Override
	public void close() throws IOException {
		closeExcelFile(true);
		if (poiFSFileSystem != null || opcPackage != null || preferFilePath == null) {
			return;
		}
		if (filePathList.size() <= 1) {
			resultFilePath = preferFilePath;
			return;
		}
		int idx = preferFilePath.lastIndexOf('.');
		resultFilePath = idx >= 0 ? preferFilePath.substring(0, idx) : preferFilePath;
		resultFilePath += ".zip";
		ZipOutputStream zipOut = null;
		try {
			zipOut = new ZipOutputStream(new FileOutputStream(resultFilePath));
			zipOut.setEncoding("UTF-8");
			for (int i = 0; i < filePathList.size(); i ++) {
				if (FilepathValidateUtils.validateFilepath(filePathList.get(i))) {
					File f = new File(filePathList.get(i));
					zipOut.putNextEntry(new ZipEntry(f.getName()));
					FileUtils.copyFile(f, zipOut);
					zipOut.closeEntry();
				}
			}
		} finally {
			IOUtils.closeQuietly(zipOut);
		}
		for (int i = 0; i < filePathList.size(); i ++) {
			if (FilepathValidateUtils.validateFilepath(filePathList.get(i))) {
				File f = new File(filePathList.get(i));
				f.delete();
			}
		}
	}

	@Override
	public CellStyle createCellStyle() {
		return compress ? new CellStyleWrapper(workbook, workbook.createCellStyle()) : workbook.createCellStyle();
	}

	@Override
	public DataFormat createDataFormat() {
		return workbook.createDataFormat();
	}

	@Override
	public Font createFont() {
		return compress ? new FontWrapper(workbook, workbook.createFont()) : workbook.createFont();
	}

	@Override
	public Name createName() {
		return workbook.createName();
	}

	@Override
	public Sheet createSheet() {
		return compress ? new SheetWrapper(this, workbook.createSheet()) : workbook.createSheet();
	}

	@Override
	public Sheet createSheet(String sheetname) {
		return compress ? new SheetWrapper(this, workbook.createSheet(sheetname)) : workbook.createSheet(sheetname);
	}

	@Override
	public Font findFont(boolean bold, short color, short fontHeight, String name, boolean italic, boolean strikeout, short typeOffset, byte underline) {
		return workbook.findFont(bold, color, fontHeight, name, italic, strikeout, typeOffset, underline);
	}

	@Override
	public int getActiveSheetIndex() {
		return workbook.getActiveSheetIndex();
	}

	@Override
	public List<? extends Name> getAllNames() {
		return workbook.getAllNames();
	}

	@Override
	public List<? extends PictureData> getAllPictures() {
		return workbook.getAllPictures();
	}

	@Override
	public CellStyle getCellStyleAt(int idx) {
		return workbook.getCellStyleAt(idx);
	}

	@Override
	public CreationHelper getCreationHelper() {
		return workbook.getCreationHelper();
	}

	public HSSFPalette getCustomPalette() {
		if (workbook instanceof HSSFWorkbook) {
			return ((HSSFWorkbook)workbook).getCustomPalette();
		}
		return null;
	}

	@Override
	public int getFirstVisibleTab() {
		return workbook.getFirstVisibleTab();
	}

	@Override
	public Font getFontAt(short idx) {
		return workbook.getFontAt(idx);
	}

	@Override
	public boolean getForceFormulaRecalculation() {
		return workbook.getForceFormulaRecalculation();
	}

	@Override
	public MissingCellPolicy getMissingCellPolicy() {
		return workbook.getMissingCellPolicy();
	}

	@Override
	public Name getName(String name) {
		return workbook.getName(name);
	}

	@Override
	public Name getNameAt(int nameIndex) {
		return workbook.getNameAt(nameIndex);
	}

	@Override
	public int getNameIndex(String name) {
		return workbook.getNameIndex(name);
	}

	@Override
	public List<? extends Name> getNames(String name) {
		return workbook.getNames(name);
	}

	@Override
	public int getNumCellStyles() {
		return workbook.getNumCellStyles();
	}

	@Override
	public short getNumberOfFonts() {
		return workbook.getNumberOfFonts();
	}

	@Override
	public int getNumberOfNames() {
		return workbook.getNumberOfNames();
	}

	@Override
	public int getNumberOfSheets() {
		return workbook.getNumberOfSheets();
	}

	@Override
	public String getPrintArea(int sheetIndex) {
		return workbook.getPrintArea(sheetIndex);
	}

	@Override
	public Sheet getSheet(String name) {
		return workbook.getSheet(name);
	}

	@Override
	public Sheet getSheetAt(int index) {
		return workbook.getSheetAt(index);
	}

	@Override
	public int getSheetIndex(String name) {
		return workbook.getSheetIndex(name);
	}

	@Override
	public int getSheetIndex(Sheet sheet) {
		return workbook.getSheetIndex(sheet);
	}

	@Override
	public String getSheetName(int sheet) {
		return workbook.getSheetName(sheet);
	}

	@Override
	public SheetVisibility getSheetVisibility(int sheetIx) {
		return workbook.getSheetVisibility(sheetIx);
	}

	@Override
	public SpreadsheetVersion getSpreadsheetVersion() {
		return workbook.getSpreadsheetVersion();
	}

	public StylesTable getStylesSource() {
		if (workbook instanceof XSSFWorkbook) {
			return ((XSSFWorkbook)workbook).getStylesSource();
		}
		return null;
	}

	@Override
	public boolean isHidden() {
		return workbook.isHidden();
	}

	@Override
	public boolean isSheetHidden(int sheetIx) {
		return workbook.isSheetHidden(sheetIx);
	}

	@Override
	public boolean isSheetVeryHidden(int sheetIx) {
		return workbook.isSheetVeryHidden(sheetIx);
	}

	@Override
	public int linkExternalWorkbook(String name, Workbook workbook) {
		return workbook.linkExternalWorkbook(name, workbook);
	}

	@Override
	public void removeName(int index) {
		workbook.removeName(index);
	}

	@Override
	public void removeName(String name) {
		workbook.removeName(name);
	}

	@Override
	public void removeName(Name name) {
		workbook.removeName(name);
	}

	@Override
	public void removePrintArea(int sheetIndex) {
		workbook.removePrintArea(sheetIndex);
	}

	@Override
	public void removeSheetAt(int index) {
		workbook.removeSheetAt(index);
	}

	@Override
	public void setActiveSheet(int sheetIndex) {
		workbook.setActiveSheet(sheetIndex);
	}

	@Override
	public void setFirstVisibleTab(int sheetIndex) {
		workbook.setFirstVisibleTab(sheetIndex);
	}

	@Override
	public void setForceFormulaRecalculation(boolean value) {
		workbook.setForceFormulaRecalculation(value);
	}

	@Override
	public void setHidden(boolean hiddenFlag) {
		workbook.setHidden(hiddenFlag);
	}

	@Override
	public void setMissingCellPolicy(MissingCellPolicy missingCellPolicy) {
		workbook.setMissingCellPolicy(missingCellPolicy);
	}

	@Override
	public void setPrintArea(int sheetIndex, String reference) {
		workbook.setPrintArea(sheetIndex, reference);
	}

	@Override
	public void setPrintArea(int sheetIndex, int startColumn, int endColumn, int startRow, int endRow) {
		workbook.setPrintArea(sheetIndex, startColumn, endColumn, startRow, endRow);
	}

	@Override
	public void setSelectedTab(int index) {
		workbook.setSelectedTab(index);
	}

	@Override
	public void setSheetHidden(int sheetIx, boolean hidden) {
		workbook.setSheetHidden(sheetIx, hidden);
	}

	@Deprecated
	@Override
	public void setSheetHidden(int sheetIx, int hidden) {
		workbook.setSheetHidden(sheetIx, hidden);
	}

	@Override
	public void setSheetName(int sheet, String name) {
		workbook.setSheetName(sheet, name);
	}

	@Override
	public void setSheetOrder(String sheetname, int pos) {
		workbook.setSheetOrder(sheetname, pos);
	}

	@Override
	public void setSheetVisibility(int sheetIx, SheetVisibility visibility) {
		workbook.setSheetVisibility(sheetIx, visibility);
	}

	@Override
	public Iterator<Sheet> sheetIterator() {
		return workbook.sheetIterator();
	}

	@Override
	public void write(OutputStream stream) throws IOException {
		workbook.write(stream);
	}

	/**
	 * 获取单文件最大行数
	 */
	public int getMaxRowCount() {
		return maxRowCount;
	}

	/**
	 * 设置单文件最大行数
	 */
	public void setMaxRowCount(int maxRowCount) {
		this.maxRowCount = maxRowCount;
	}

	/**
	 * 获取导出文件名
	 */
	public String getResultFilePath() {
		return resultFilePath;
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	protected boolean isCompress() {
		return compress;
	}

	protected void setCompress(boolean compress) {
		this.compress = compress;
	}
}
