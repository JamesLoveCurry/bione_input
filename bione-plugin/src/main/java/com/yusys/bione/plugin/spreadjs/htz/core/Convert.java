package com.yusys.bione.plugin.spreadjs.htz.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import jxl.Cell;
import jxl.CellType;
import jxl.Range;
import jxl.Sheet;
import jxl.format.Alignment;
import jxl.format.BoldStyle;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.yusys.bione.plugin.spreadjs.htz.pdfevent.PageEvent;




public class Convert extends WriterPdf{
	/**pdf表格*/
	private PdfPTable table=null;
	/** 当 Excel 的 border 是 NONE 是，pdf 的 border 是否是 0 */
	private boolean noEmptyBorder = false;
	/**工作簿数组*/
	private Sheet[] sheets=null;
	/**工作簿*/
	private Excel excel=null;
	/**页眉*/
	private HeaderText header;
	/**最后一个工作簿图片*/
	private SheetImage sheetImage;
	
	private Map<Integer, Integer> subMap=new HashMap<Integer, Integer>();//存放合并单元格的左上角的坐标和在list中的下标位置
	
	int iRows = 0;
	int iTotalrows = 0;
	int iCount = 0;
	
	/**获得页面对象*/
	public HeaderText getHeader() {
		return header;
	}
	/**
	 * 读入一个excel文件获得一个pdf文件输出流
	 * @param filePath excel文件路径
	 * @param output pdf输出流
	 */
	public Convert(String filePath,OutputStream output) {
		super(output);
		readExcel(filePath);
	}
	/**
	 * 将指定路径一个excel文件转化为指定路径的pdf文件
	 * @param filePath excel文件路径
	 * @param destFilePath pdf文件路径
	 */
	public Convert(String filePath,String destFilePath){
		super(destFilePath);
		readExcel(filePath);
	}
	/**
	 * 从数据库中读入一个excel文件的输入转化为一个输出流
	 * @param input excel文件的输入流
	 * @param output excel文件输出流
	 */
	public Convert(InputStream input,OutputStream output){
		super(output);
		readExcel(input);
	}
	/**
	 * 读取excel的方法
	 * @param obj Object对象
	 */
	private void readExcel(Object obj){
		excel=new Excel();
		if(obj instanceof String){
			String filePath=(String)obj;
		    excel.readExcel(filePath);//从输入流中读入excel文件
		}else if(obj instanceof InputStream ){
			InputStream input=(InputStream)obj;
		    excel.readExcelFromDB(input);
		}
		sheets=excel.getSheets();//获得excel文件工作簿数
		for(int i=0;i<=sheets.length-1;i++)
		{
			int rows=sheets[i].getRows();
			if(rows != 0)
			{
				this.setITotalrows(rows);
			}
		}
		System.out.println("->sheet的个数为:"+sheets.length);
		int length=sheets.length-1;
		ExcelSheet image_Sheet=new ExcelSheet(sheets[length]);
		List<Image> imageList=image_Sheet.getImages();
		sheetImage=new SheetImage();
		sheetImage.setImage(imageList);
	}
	/**
	 * 转化的核心方法
	 * @param pageEvent 页面事件
	 * @throws Exception
	 */
	public void convert(PageEvent pageEvent)throws Exception{

		noEmptyBorder = true;
		writer.setPageEvent(pageEvent);//设置页面事件
		document.setPageSize(pageSize);//设置pdf页面的大小
		document.open();//打开document对象
		System.out.println("->document已经打开");
		if(sheets==null||sheets.length<0){
			System.out.println("->excel文件中没有工作簿");
			return;
		}
		for(int i=0;i<=sheets.length-1;i++){
			
			int rows=sheets[i].getRows();
			int cols=sheets[i].getColumns();
			for(int h=0;h<sheets[i].getColumns();h++){
				if(sheets[i].getColumnView(h).getSize()==0){
					cols--;
				}
			}
			int colswidth[]=colsWidth(sheets[i],cols);
			Map<Integer, String> merge=new HashMap<Integer, String>();//存放合并单元格的左上角的坐标和在list中的下标位置
			Range[] range=sheets[i].getMergedCells();//得到工作簿的合并单元格的数组
			for(int k=0;k<range.length;k++){
				int row=range[k].getTopLeft().getRow();
				int col=range[k].getTopLeft().getColumn();
				for(int co=0;co<=range[k].getTopLeft().getColumn();co++){
					if(sheets[i].getColumnView(co).getSize()==0){
						col--;
					}
				}
				int index=row*cols+col+1;
				int r=range[k].getBottomRight().getRow()-range[k].getTopLeft().getRow()+1;
				for(int rs=range[k].getTopLeft().getRow();rs<=range[k].getBottomRight().getRow();rs++){
					if(sheets[i].getRowView(rs).getSize()==0){
						r--;
					}
				}
				int c=range[k].getBottomRight().getColumn()-range[k].getTopLeft().getColumn()+1;
				for(int cs=range[k].getTopLeft().getColumn();cs<=range[k].getBottomRight().getColumn();cs++){
					if(sheets[i].getColumnView(cs).getSize()==0){
						c--;
					}
				}
				int mi=row;
				
				for(int m=row;m<=range[k].getBottomRight().getRow();m++){
					if(this.sheets[i].getRowView(m).getSize()!=0){
						int ni=col;
						for(int n=range[k].getTopLeft().getColumn();n<=range[k].getBottomRight().getColumn();n++){
							if(this.sheets[i].getColumnView(n).getSize()!=0){
								if(m!=row||n!=range[k].getTopLeft().getColumn()){
									int xy=mi*cols+ni+1;
									subMap.put(Integer.valueOf(xy), index);
								}
								ni++;
							}
						}
						mi++;
					}
				}
				
				String key=r+","+c;
				merge.put(Integer.valueOf(index),key);
			}
			if(cols>0){
				int size=30;
				int n=(rows-1)/size+1;
				int curp=0;
				for(int k=0;k<n;k++){
					List<Cell> cells=new ArrayList<Cell>();
					
					table=new PdfPTable(cols);//创建含有cols列的表格
					table.setWidths(colswidth);//设置每列的大小
					table.setWidthPercentage(90.0f);
//					table.setPadding(2.0f);//设置填充间隔
//					table.setSpacing(0.0f);//设置单元格之间距离
//					table.setWidth(100.0f);//设置表格的宽度百分比
//					table.setBorder(0);//设置表格的边框
//					table.setOffset(30.0f);//设置表与表之间的偏移量
					float actWidth=0.0f;
					for(int cw=0;cw<colswidth.length;cw++){
						actWidth+=colswidth[cw];
					}
					float per=this.pageSize.getWidth()/actWidth*6<1?this.pageSize.getWidth()/actWidth*6:1;
					cells=getSheetCell(sheets[i],(k+1)*size>rows?rows:(k+1)*size,this.sheets[i].getColumns());
					for(int p=(curp==0?0:curp);p<cells.size();p++){
						if(!findIndex(p,subMap)){
							if(merge.containsKey(Integer.valueOf(p+1))){//合并单元格
								PdfPCell pcell=null;
								Cell jxlcell=(Cell) cells.get(p);
								CellFormat format = jxlcell.getCellFormat();//取得单元格的格式
								Font font = null;
								String content=jxlcell.getContents();
								boolean chineseFlag=Pattern.compile("[\u0391-\uFFE5]+").matcher(content).find();
								if(format != null && format.getFont() != null) {
									font = convertFont(format.getFont(),chineseFlag,per);// 调用convertFont()的方法转变字体
								}else{
									font = new Font(FontFamily.COURIER, 10.0f*per, Font.NORMAL, BaseColor.BLACK);
								}
								
								String key = (String) merge.get(Integer
										.valueOf(p+1));
								String[] s = StringUtils.split(key, ',');
								int r = Integer.parseInt(s[0]);// 合并单元格左上角的行数
								int c = Integer.parseInt(s[1]);// 合并单元格左上角的列数
								if(content.indexOf("ERROR")>=0){
									content="NA";
								}
								Paragraph pa = new Paragraph(content, font);  
								pcell = new PdfPCell();
								if (r > 1) {
									pcell.setRowspan(r);// 对行进行合并
								}
								if (c > 1) {
									pcell.setColspan(c);// 对列进行合并
								}
								transferFormat(pcell, jxlcell, p, cols, cells, r, c,k>0?true:false,p-curp,per);// 将jxl中的cell转化为pdf的cell
								pa.setAlignment(pcell.getHorizontalAlignment());
								pcell.addElement(pa);
								//pcell.setBorderWidthRight(2.0f);
								table.addCell(pcell);
							}else{
								PdfPCell pcell=null;
								Cell jxlcell=(Cell) cells.get(p);
								CellFormat format = jxlcell.getCellFormat();//取得单元格的格式
								String content=jxlcell.getContents();
								Font font = null;
								boolean chineseFlag=Pattern.compile("[\u0391-\uFFE5]+").matcher(content).find();
								if(format != null && format.getFont() != null) {
									font = convertFont(format.getFont(),chineseFlag,per);// 调用convertFont()的方法转变字体
								}else{
									font = new Font(FontFamily.COURIER, 10.0f*per, Font.NORMAL, BaseColor.BLACK);
								}
								if(content.indexOf("ERROR")>=0){
									content="NA";
								}
								Paragraph pa = new Paragraph(content, font);  
								pcell=new PdfPCell();
								transferFormat(pcell, jxlcell,p,cols,cells, 0, 0,k>0?true:false,p-curp,per);//将jxl中的cell转化为pdf的cell
								pa.setAlignment(pcell.getHorizontalAlignment());
								pcell.addElement(pa);
								table.addCell(pcell);
//				            } 
							}
						}
						
					}
					curp=cells.size();
					System.out.println(table);
					document.add(table);//将table对象添加到文档对象中去
					cells.clear();//清空cell单元格
					if(k<n-1){
						document.newPage();
					}
					
				}
				
			}
		}
		System.out.println("执行了");
		CloseDocument();
		excel.closeWorkbook();
	}
	/**
	 * 获得工作簿中所有列宽的数组
	 * @param sheet 工作簿
	 * @return 列宽数组
	 */
	private int[] colsWidth(Sheet sheet,int col){
		int width[]=new int[col];
		int cl=0;
		for(int i=0;i<sheet.getColumns();i++){
			if(sheet.getColumnView(i).getSize()!=0){
				width[cl]=sheet.getColumnView(i).getSize() / 256 * 20;
				if(width[cl]==0){
					width[cl]=8*20;
				}
				cl++;
			}
			
		}
		return width;
	}
	/**
	 * 将当前工作簿中的所有单元格添加到List集合中
	 * @param sheet 工作簿
	 * @param rows  工作簿行数
	 * @param cols  工作簿列数
	 */
	private List<Cell> getSheetCell(Sheet sheet,int rows,int cols){
		List<Cell> cells=new ArrayList<Cell>();
		int i = 0;
		if(iRows != 0)
		{
			iRows++;
		}
		for(i = iRows;i<rows;i++){
			if(sheet.getRowView(i).getSize()!=0){
				for(int j=0;j<cols;j++){
//					System.out.println(sheet.getCell(j,i).getContents());
					if(sheet.getColumnView(j).getSize()!=0)
						cells.add(sheet.getCell(j,i));
				}
				iCount++;
				if(iCount % 3000 == 0)
				{
					iRows = i;
					break;
				}
			}
			
		}
		return cells;
	}
	/**
	 * 查找已合并后单元格下标
	 * @param i 下标位置
	 * @param subs 合并单元格的下标集合
	 * @return 是否找到
	 */
	private boolean findIndex(int i,Map<Integer,Integer> subs){
		if(subs.get(i+1)!=null){
			return true;
		}
		return false;
	}
	/**
	 * 转换字体
	 * @param f - 字体
	 * @return
	 */
	private Font convertFont(jxl.format.Font f,boolean chineseFlag,float per) {
		if (f == null || f.getName() == null)
			return FontFactory.getFont(FontFactory.COURIER, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

		int fontStyle = convertFontStyle(f);
		Font font = null;
		BaseColor fontColor = convertColour(f.getColour(), BaseColor.BLACK);
		if ((ChineseFont.BASE_CHINESE_FONT != null && ChineseFont.containsChinese(f.getName()))||chineseFlag) {
			font = new Font(ChineseFont.BASE_CHINESE_FONT, f.getPointSize()*per, fontStyle, fontColor);
		} else {
			String s = f.getName().toLowerCase();
			FontFamily fontFamily;
			if (s.indexOf("courier") >= 0) //"courier new".equals(s) || "courier".equals(s))
				fontFamily = FontFamily.COURIER;
			else if (s.indexOf("times") >= 0)
				fontFamily = FontFamily.TIMES_ROMAN;
			else
				fontFamily = FontFamily.HELVETICA;
			font = new Font(fontFamily, f.getPointSize()*per, fontStyle, fontColor);

		}

		return font;
	}
	/**
	 * 颜色转换
	 * @param colour
	 * @param defaultColor
	 * @return
	 */
	private BaseColor convertColour(Colour colour, BaseColor defaultColor) {
		if (defaultColor == null)
			defaultColor = BaseColor.WHITE;
		
		if (colour == null)
			return defaultColor;

        if (colour == Colour.AQUA) // Excel中的自动(前景色)
        	return BaseColor.BLACK;
           // return new Color(Colour.AUTOMATIC.getDefaultRGB().getRed(),Colour.AUTOMATIC.getDefaultRGB().getGreen(),Colour.AUTOMATIC.getDefaultRGB().getBlue());
        else if (colour == Colour.DEFAULT_BACKGROUND) // Excel中的自动(底色)
        	return BaseColor.WHITE;
          //  return new Color(Colour.DEFAULT_BACKGROUND.getDefaultRGB().getRed(),Colour.DEFAULT_BACKGROUND.getDefaultRGB().getGreen(),Colour.DEFAULT_BACKGROUND.getDefaultRGB().getBlue());
        
		//RGB rgb = RGB.getDefaultRGB();
		return new BaseColor(colour.getDefaultRGB().getRed(), colour.getDefaultRGB().getGreen(), colour.getDefaultRGB().getBlue());
	}
	/**
	 * 转换字体样式
	 * @param font - 字体
	 * @return
	 */
	private int convertFontStyle(jxl.format.Font font) {

		int result = Font.NORMAL;
		if (font.isItalic())
			result |= Font.ITALIC;
//		if (font.isStruckout())
//			result |= com.lowagie.text.Font.STRIKETHRU;
		
		if (font.getBoldWeight() == BoldStyle.BOLD.getValue())
			result |= Font.BOLD;
		
		if (font.getUnderlineStyle() != null) {
			// 下划线
			UnderlineStyle style = font.getUnderlineStyle();
			if (style.getValue() != UnderlineStyle.NO_UNDERLINE.getValue())
				result |=Font.UNDERLINE;
		}
		return result;
	}
	
	/**
	 * 转换单元格的格式 com.lowagie.text.Cell
	 * @param pdfCell
	 * @param cell
	 * @param mergeRow
	 * @r 行上合并的单元格数
	 * @c 列上合并的单元格数
	 */
	private void transferFormat(PdfPCell pcell, Cell cell,int index,int cols,List<Cell> cells, int r, int c,boolean flag,int realIndex,float per) {
		jxl.format.CellFormat format = cell.getCellFormat();
		pcell.setBorderWidthBottom(0.0f);
		pcell.setBorderWidthTop(0.0f);
		pcell.setBorderWidthLeft(0.0f);
		pcell.setBorderWidthRight(0.0f);
		if (format != null) {
            // 水平对齐
            pcell.setHorizontalAlignment(convertAlignment(format.getAlignment(), cell.getType()));
            // 垂直对齐
            pcell.setVerticalAlignment(convertVerticalAlignment(format.getVerticalAlignment()));
            // 背景
            // 处理 border
           
            boolean left=false,top=false,right=true,bottom=true;
            
            if(index-cols >= 0){
            	if(subMap.get(index-cols+1)==null){
            		if(cellBottomBorderLineStyle((Cell)cells.get(index-cols))){
                		top=true;
                	}
            	}
            	else{
            		if(cellBottomBorderLineStyle((Cell)cells.get(subMap.get(index-cols+1)))){
                		top=true;
                	}
            	}
            }
            if(index-1 >= 0){
            	if(subMap.get(index)==null){
            		if(cellLeftBorderLineStyle((Cell)cells.get(index-1))){
            			left=true;
            		}
            	}
            	else{
            		if(cellLeftBorderLineStyle((Cell)cells.get(subMap.get(index)))){
            			left=true;
            		}
            	}
            }
            if(index%cols==0){
             	left=true;
            }
			BorderLineStyle lineStyle = null;
			 if(r>1&&cells.size()>(index+(r-1)*cols)&&cells.get(index+(r-1)*cols)!=null&&cells.get(index+(r-1)*cols).getCellFormat()!=null){
	            	lineStyle=cells.get(index+(r-1)*cols).getCellFormat().getBorderLine(jxl.format.Border.BOTTOM);
	          }
			lineStyle = format.getBorderLine(jxl.format.Border.BOTTOM);
			if (bottom) {
				pcell.setBorderColorBottom(convertColour(
						format.getBorderColour(jxl.format.Border.BOTTOM),
						BaseColor.BLACK));
				pcell.setBorderWidthBottom(convertBorderStyle(lineStyle,true,per));
			}
			 if(flag){
	            	if(realIndex<cols){
	            		pcell.setBorderColorTop(BaseColor.BLACK);
	    				pcell.setBorderWidthTop(1.0f);
	            	}
	            }
			 else{
				 lineStyle = format.getBorderLine(jxl.format.Border.TOP);
					if(top){
		                pcell.setBorderColorTop(convertColour(format.getBorderColour(jxl.format.Border.TOP), BaseColor.BLACK));
		                pcell.setBorderWidthTop(convertBorderStyle(lineStyle,false,per));//
		            }
			 }
			
            lineStyle = format.getBorderLine(jxl.format.Border.LEFT);
            //convertBorderStyle(lineStyle)
            if(left){
                pcell.setBorderColorLeft(convertColour(format.getBorderColour(jxl.format.Border.LEFT), BaseColor.BLACK));
                pcell.setBorderWidthLeft(convertBorderStyle(lineStyle,false,per));
            }
            
            lineStyle = format.getBorderLine(jxl.format.Border.RIGHT);
            if(c>1&&cells.get(index+c-1)!=null&&cells.get(index+c-1).getCellFormat()!=null){
            	lineStyle=cells.get(index+c-1).getCellFormat().getBorderLine(jxl.format.Border.RIGHT);
            }
            if(right){
                pcell.setBorderColorRight(convertColour(format.getBorderColour(jxl.format.Border.RIGHT), BaseColor.BLACK));
                pcell.setBorderWidthRight(convertBorderStyle(lineStyle,true,per));
            }
	        if (format.getBackgroundColour().getValue() != Colour.DEFAULT_BACKGROUND.getValue()) {
	             pcell.setBackgroundColor(convertColour(format.getBackgroundColour(), BaseColor.WHITE));//设置背景色
	        }
          
        }else{
        	pcell.setBorder(0);
        }
       }
    
    
	/**
	 * 转换边框样式
	 * @param style
	 * @return
	 */
	private float convertBorderStyle(BorderLineStyle style,boolean flag,float per) {
		if (style == null) return 0.0f;
		
		float w = 0.0f;
		if(BorderLineStyle.HAIR.getValue()==style.getValue()){
			w=7.0f;
		}else if(BorderLineStyle.NONE.getValue() == style.getValue()) {
			// 默认全部使用边框，边框大小 0.5f
			if (noEmptyBorder){
				w = 0.0f*per;
			}
		}else if(BorderLineStyle.THIN.getValue() == style.getValue()){
			w = 0.5f*per;
		}else if (BorderLineStyle.THICK.getValue() == style.getValue()) {
			w = 1.0f*per;
        }else if (BorderLineStyle.MEDIUM.getValue() == style.getValue()) {
			w = 1.0f*per;
        }else if(BorderLineStyle.DOUBLE.getValue()==style.getValue()){
        	w = 2.5f*per;
        }else {
			w = 0.0f*per;
		}
		/*if(flag&&w!=0.0f){
			w+=0.5;
		}*/
		return w;
	}
	/**
	 * 转换对齐方式
	 * @param align
	 * @param cellType
	 * @return int 
	 */
	private int convertAlignment(Alignment align, CellType cellType) {
		if (align == null)
			return Element.ALIGN_UNDEFINED;
		
		if (Alignment.CENTRE.getValue() == align.getValue())
			return  Element.ALIGN_CENTER;
		
		if (Alignment.LEFT.getValue() == align.getValue())
			return Element.ALIGN_LEFT;
		
		if (Alignment.RIGHT.getValue() == align.getValue())
			return Element.ALIGN_RIGHT;
		
		if (Alignment.JUSTIFY.getValue() == align.getValue())
			return Element.ALIGN_JUSTIFIED;
		
		if (Alignment.GENERAL.getValue() == align.getValue()) {
			// 所有未明确设置对齐方式的元素，都属于 Alignment.GENERAL 类型
			if (cellType == CellType.NUMBER || cellType == CellType.NUMBER_FORMULA)
				return Element.ALIGN_RIGHT;   // 数字右对齐
			if (cellType == CellType.DATE || cellType == CellType.DATE_FORMULA)
				return Element.ALIGN_RIGHT;   // 日期右对齐
		}
		return Element.ALIGN_UNDEFINED;
	}
	/**
	 * 转换垂直对齐方式
	 * @param align - jxl 的对齐方式
	 * @return int
	 */
	private int convertVerticalAlignment(VerticalAlignment align) {
		if (align == null)
			return Element.ALIGN_UNDEFINED;
		
		if (VerticalAlignment.BOTTOM.getValue() == align.getValue())
			return Element.ALIGN_BOTTOM;
		
		if (VerticalAlignment.CENTRE.getValue() == align.getValue())
			return Element.ALIGN_MIDDLE;
		
		if (VerticalAlignment.TOP.getValue() == align.getValue())
			return Element.ALIGN_TOP;
		
		if (VerticalAlignment.JUSTIFY.getValue() == align.getValue())
			return Element.ALIGN_JUSTIFIED;

		return Element.ALIGN_UNDEFINED;
	}
	
	/***
	 * 判断下边框是否是默认边框
	 * @param cell
	 * @return
	 */
	private boolean cellBottomBorderLineStyle(Cell cell){
		boolean b=false;
		jxl.format.CellFormat format = cell.getCellFormat();
		if(format!=null){
			BorderLineStyle lineStyle = null;
            lineStyle = format.getBorderLine(jxl.format.Border.BOTTOM);
            if (lineStyle.getValue() == BorderLineStyle.NONE.getValue()){
              b=true;
            }
		}else{
			b=true;
		}
		return b;
	}
	public int getIRows() {
		return iRows;
	}
	public void setIRows(int rows) {
		iRows = rows;
	}
	public int getITotalrows() {
		return iTotalrows;
	}
	public void setITotalrows(int totalrows) {
		iTotalrows = totalrows;
	}
	/**
	 * 判断左边框是否是默认边框
	 * @param cell
	 * @return
	 */
	private boolean cellLeftBorderLineStyle(Cell cell){
		boolean b=false;
		jxl.format.CellFormat format = cell.getCellFormat();
		if(format!=null){
			BorderLineStyle lineStyle = null;
            lineStyle = format.getBorderLine(jxl.format.Border.RIGHT);
            if (lineStyle.getValue() == BorderLineStyle.NONE.getValue()){
              b=true;
            }
		}else{
			b=true;
		}
		return b;
	}
//	private boolean cellRightBorderLineStyle(Cell cell){
//		boolean b=false;
//		jxl.format.CellFormat format = cell.getCellFormat();
//		if(format!=null){
//			BorderLineStyle lineStyle = null;
//            lineStyle = format.getBorderLine(jxl.format.Border.LEFT);
//            if (lineStyle.getValue()!= BorderLineStyle.NONE.getValue()){
//              b=true;
//            }
//		}
//		return b;
//	}
}

