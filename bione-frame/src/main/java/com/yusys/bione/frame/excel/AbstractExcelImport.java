package com.yusys.bione.frame.excel;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.yusys.bione.comp.utils.ReflectionUtils;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.validator.common.ValidateConstants;


public abstract class AbstractExcelImport {
	protected int firstRow=0;
	protected Class<?> cla;
	protected Class<?> getEntitisClass(List<?> entitis){
		return entitis.get(0).getClass();
	}
	
	protected Object createObj(Object obj,Row row,int rowNum,String sheetName) throws ParseException, SecurityException, NoSuchFieldException, ClassNotFoundException{
		Field fileds[]=obj.getClass().getDeclaredFields();
		boolean flag=true;
		Map<String,Map<Object,Object>> dsmap=new HashMap<String,Map<Object,Object>>();
		for(int i=0;i<fileds.length;i++){
			ExcelColumn ec=fileds[i].getAnnotation(ExcelColumn.class);
			if(ec!=null){
				int index=ExcelAnnotationUtil.getExcelCol(ec.index());
				Object value=getCellVal(row.getCell(index));
				if(value==null||"".equals(value))
					value=ec.initVal();
				if(value!=null&&!"".equals(value))
					flag=false;
				if(ec.type().equals("text"))
				{
					if(ec.value().length>0&&ec.text().length>0){
						if(value instanceof String){
							String combInfo="";
							String vlas[]=StringUtils.split(value.toString().trim(), ",");
							for(int j=0;j<vlas.length;j++){
								for(int k=0;k<ec.text().length;k++){
									if(vlas[j].equals(ec.text()[k])){
										combInfo+=ec.value()[k]+";";
									}
								}
							}
							if(combInfo.length()>0)
								value=StringUtils.substring(combInfo, 0,combInfo.length()-1);
						}
					}
				}
				if("date".equals(ec.type())){
					if(value instanceof String){
						if(!"".equals(ec.formate())){
							SimpleDateFormat sdf=new SimpleDateFormat(ec.formate());
							Date date=sdf.parse(value.toString().trim());
							Timestamp ts=new Timestamp(date.getTime());
							value=ts;
						}
					}
				}
				if(ec.relDs().length>=3){
					ExcelBS ebs=SpringContextHolder.getBean("excelBS");
					String conditon="";
					if(ec.relDs().length>=4){
						conditon=ec.relDs()[3];
					}
					if(value instanceof String){
						Map<Object,Object> map=dsmap.get(fileds[i].getName());
						if(map==null){
							map=ebs.getValue(Class.forName(ec.relDs()[0]), ec.relDs()[1], ec.relDs()[2], conditon);
							dsmap.put(fileds[i].getName(), map);
						}
						String vlas[]=StringUtils.split(value.toString().trim(), ",");
						String combInfo="";
						for(int j=0;j<vlas.length;j++){
							Object info=map.get(vlas[j]);
							combInfo+=(info==null)?"":info+";";
						}
						if(combInfo.length()>0)
							value=StringUtils.substring(combInfo, 0,combInfo.length()-1);
					}
				}
				value=coerceToPrimitiveValue(value, fileds[i].getType());
				if("".equals(value))
					value=null;
				ReflectionUtils.invokeSetter(obj, fileds[i].getName(), value);
				ReflectionUtils.invokeSetter(obj, ValidateConstants.VALIDATE_EXCEL_ROWNO_FIELDNM, rowNum+1);
				ReflectionUtils.invokeSetter(obj, ValidateConstants.VALIDATE_EXCEL_SHEETNM_FIELDNM, sheetName);
			}
		}
		if(flag){
			return null;
		}
		return obj;
	}
	
	/**
	 * 获取单元格数据
	 * 
	 * @param cell
	 *            单元格
	 * @return 单元格数据
	 */
	public Object getCellVal(Cell cell) {

		Object cellValue = null;
		if(cell==null)
			return null;
		switch (cell.getCellTypeEnum()) { // 根据cell中的类型来输出数据
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)
					|| DateUtil.isCellInternalDateFormatted(cell)) {
				cellValue = new StringBuilder("/OADate(")
						.append(String.valueOf(cell.getNumericCellValue()))
						.append(")/").toString();
			} else {
				cellValue = cell.getNumericCellValue();
				//解决整数多出".0"的问题
				NumberFormat nf = NumberFormat.getInstance();
				nf.setGroupingUsed(false);//设置“千分位是否使用逗号分隔”
				cellValue = nf.format(cellValue);
				//添加数字类型的科学计数法转换，不能转换的就按照原值传递 add by chenl
				try{
					BigDecimal bd = new BigDecimal(cellValue+"");
					String str = bd.toPlainString();
					cellValue = Integer.parseInt(str);
				}catch(Exception ex){
					
				}
			}
			break;
		case STRING:
			cellValue = cell.getStringCellValue();
			break;
		case BOOLEAN:
			cellValue = cell.getBooleanCellValue();
			break;
		case FORMULA:
			try {
				cellValue = String.valueOf(cell.getStringCellValue());
			} catch (Exception e) {
				try {
					cellValue = String.valueOf(cell.getNumericCellValue());
				} catch (Exception e1) {
					cell.getErrorCellValue();
				}
			}
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

	protected Sheet getSheet(Workbook workbook){
		ExcelSheet es=cla.getAnnotation(ExcelSheet.class);
		firstRow=es.firstRow();
		if("".equals(es.index())){
			if("".equals(es.name())){
				return null;
			}
			else{
				return workbook.getSheet(es.name());
			}
		}
		else{
			return workbook.getSheetAt(NumberUtils.toInt(es.index()));
		}
	}
	
	private Object coerceToPrimitiveValue(Object value, Class<?> clazz) {
		if(value!=null){
			try{
				if (clazz == Byte.class || clazz == Byte.TYPE) {
					if(value == null || "".equals(value)){
						value = "";
					}
					return Byte.valueOf(value.toString().trim() );
				} else if (clazz == Short.class || clazz == Short.TYPE) {
					if(value == null || "".equals(value)){
						value = 0;
					}
					return Short.valueOf(value.toString());
				} else if (clazz == Integer.class || clazz == Integer.TYPE) {
					if(value == null || "".equals(value)){
						value = 0;
					}
					return Integer.valueOf(value.toString().trim());
				} else if (clazz == Long.class || clazz == Long.TYPE) {
					if(value == null || "".equals(value)){
						value = 0;
					}
					return Long.valueOf(value.toString().trim());
				} else if (clazz == Float.class || clazz == Float.TYPE) {
					if(value == null || "".equals(value)){
						value = 0;
					}
					return Float.valueOf(value.toString().trim());
				} else if (clazz == Double.class || clazz == Double.TYPE) {
					if(value == null || "".equals(value)){
						value = 0;
					}
					return Double.valueOf(value.toString().trim());
				} else if (clazz == Boolean.class || clazz == Boolean.TYPE) {
					if(value == null || "".equals(value)){
						value = false;
					}
					return Boolean.valueOf(value.toString().trim());
				} else if (clazz == BigInteger.class) {
					if(value == null || "".equals(value)){
						value = 0;
					}
					return new BigInteger( value.toString().trim() );
				} else if (clazz == BigDecimal.class) {
					if(value == null || "".equals(value)){
						value = 0;
					}
					return new BigDecimal( value.toString().trim() );
				} else if (clazz == Character.class || clazz == Character.TYPE) {
					if(value == null || "".equals(value)){
						value = "";
					}
					if ((value.toString().trim() != null) && (value.toString().trim().length() > 0)) {
						return Character.valueOf(value.toString().charAt(0));
					}
				} else if (clazz == String.class) {
					if(value == null || "".equals(value)){
						value = "";
					}
					return String.valueOf(value.toString().trim());
				}
				else{
					return value;
				}
				throw new IllegalArgumentException("Cannot convert String [" + value + "] to target class [" + clazz.getName() + "]");
			}
			catch(Exception e){
				return null;
			}
			
		}
		else{
			return value;
		}
		
	}
	
	public abstract List<?> ReadExcel() throws InstantiationException, IllegalAccessException;
	
	public abstract List<?> ReadExcel(String password) throws InstantiationException, IllegalAccessException, EncryptedDocumentException, InvalidFormatException;

}
