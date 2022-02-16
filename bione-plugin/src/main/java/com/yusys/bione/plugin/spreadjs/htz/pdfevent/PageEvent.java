package com.yusys.bione.plugin.spreadjs.htz.pdfevent;



import org.apache.commons.lang3.StringUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.yusys.bione.plugin.spreadjs.htz.core.ChineseFont;
import com.yusys.bione.plugin.spreadjs.htz.core.HeaderText;
import com.yusys.bione.plugin.spreadjs.htz.pdfpag.PdfFooter;
import com.yusys.bione.plugin.spreadjs.htz.pdfpag.PdfHeader;


/**
 * 自定义 PDF 事件
 */
public class PageEvent extends PdfPageEventHelper {
	/** An Image that goes in the header. */
    public Image headerImage;
    /** The Graphic state */
    public PdfGState gstate;
    /** page header */
	private PdfPTable table;
    /** footer 高度 */
    private float footerHeight;
    /** A template that will hold the total number of pages. */
    private PdfTemplate tpl;
    /** 页脚所用字体 */
    private BaseFont font;
    /** 代替总页数的空格定义，在 openDocument 时计算 */
    private String blankTextChars = "    ";
    /**页码的字体大小*/
    private float pageNumberSize;
    /** 保存初始的 top margin 值 */
    private float _topMargin;
    /** 保存初始的 bottom margin 值 */
    private float _bottomMargin;
    /** 是否重设 margin */
    private boolean resetMargin;
    /**页码样式*/
    private String pageNumberStyle;
    /**页脚*/
    private PdfFooter footer;
    /**页眉*/
    private PdfHeader header;
    /**页面信息*/
    @SuppressWarnings("unused")
	private HeaderText headerText;
    /**设置页脚信息*/
	public void setFooter(PdfFooter footer) {
		this.footer = footer;
	}
	/**设置页面信息*/
	public void setHeader(PdfHeader header) {
		this.header = header;
	}
	/**
	 * 默认构造函数
	 */
	public PageEvent() {}
	/**
	 * 通过页眉和页脚来构造该对象
	 * @param footer 页脚
	 * @param header 页眉
	 */
	public PageEvent(PdfFooter footer, PdfHeader header) {
		super();
		this.footer = footer;
		this.header = header;
	}
	/**
	 * 设置页码字体大小
	 * @param size
	 */
	public void setPageNumberSize(float size) {
		pageNumberSize = size;
		if (size > footerHeight) footerHeight = size;
	}		
//	/**
//	 * 设置是否跳过第一个页头
//	 * @param skip
//	 */
//	public void setSkipFirstWrite(boolean skip) {
//		this.skipFirstWrite = skip;
//	}
//	/**
//	 * 设置页脚
//	 * @param footerTexts
//	 */
//	public void setFooterText(List footerTexts) {
//		this.footerTexts = footerTexts;
//		if (footerTexts != null && footerTexts.size() > 0) {
//			footer.setHasFooter(true);
//			Iterator iter = footerTexts.iterator();
//			// 找出最大的字体size
//			while (iter.hasNext()) {
//				PdfFooter text = (PdfFooter) iter.next();
//				if (text.getFontSize() > footerHeight)
//					footerHeight = text.getFontSize();
//			}
//			if (footerHeight < pageNumberSize)
//				footerHeight = pageNumberSize;
//		} else {
//			footer.setHasFooter(false);
//			footerHeight = 0.0f;
//		}
//	}
//	
	/**
	 * 设置页眉的内容
	 */
	public void setHeaderText(HeaderText headerText) {
		this.headerText = headerText;
	}
	/**
	 * 设置页码显示的样式
	 * @param style
	 */
	public void setPageNumberStyle(String style) {
		if (style != null & style.indexOf(PdfFooter.SIGN_PAGE_NUMBER) >= 0) {
			pageNumberStyle = style;
			footer.setShowTotalNumber((style.indexOf(PdfFooter.SIGN_TOTAL_NUMBER) > 0));
		}
	}
	/**
	 * 是否重置 margin
	 * @param reset
	 */
	void setResetMargin(boolean reset) {
		resetMargin = reset;
	}

	/**
	 * 设置 margin
	 * @param document - com.lowagie.text.Document
	 */
	private void setMargin(Document document) {
		float leftMargin = document.leftMargin();
		float rightMargin = document.rightMargin();
		float topMargin = (table == null) ? this._topMargin : this._topMargin + table.getTotalHeight();
		float bottomMargin = this._bottomMargin + footerHeight;

		document.setMargins(leftMargin, rightMargin, topMargin, bottomMargin);
	}

	// ------------------------------------------ event implementation
	
	/**
	 * 页结束事件
	 */
	public void onEndPage(PdfWriter writer, Document document) {
        Rectangle page = document.getPageSize();
        PdfContentByte cb = writer.getDirectContent();
        if (footer.isShowPageNumber() && tpl != null) {
            cb.saveState();
            // compose the footer
            String text = StringUtils.replace(this.pageNumberStyle, PdfFooter.SIGN_PAGE_NUMBER, String.valueOf(writer.getPageNumber()));
            int totalPagePos = -1; //总页码在text中的位置
            if (footer.isShowTotalNumber()) {
                totalPagePos = text.indexOf(PdfFooter.SIGN_TOTAL_NUMBER);
                text = StringUtils.replace(text, PdfFooter.SIGN_TOTAL_NUMBER, blankTextChars);
            }
            // 文字占的宽度
            float textSize = font.getWidthPoint(text, pageNumberSize);
            // Y 坐标
            float textBase = document.bottomMargin() - footerHeight;
            cb.beginText();
            cb.setFontAndSize(font, pageNumberSize);

            // 计算 X 坐标
            float x = 0.0f;
            if (footer.getPageNumberAlign() == Element.ALIGN_CENTER)
                x = (page.getWidth() - textSize) / 2;
            else if (footer.getPageNumberAlign() == Element.ALIGN_LEFT)
                x = document.left();
            else
                x = document.right() - textSize - font.getWidthPoint("00", pageNumberSize);
            cb.setTextMatrix(x, textBase);
            cb.showText(text);
            cb.endText();
            if (footer.isShowTotalNumber() == true) {
                textSize = font.getWidthPoint(text.substring(0, totalPagePos), pageNumberSize);
                cb.addTemplate(tpl, x + textSize, textBase);
            }
			cb.beginText();
			cb.setFontAndSize(font, 8);
			if (header.getAlign() == PdfHeader.CENTER)
                x = (page.getWidth() - textSize) / 2;
            else if (header.getAlign() == PdfHeader.LEFT)
                x = document.left();
            else
                x = document.right() - textSize - font.getWidthPoint("00", pageNumberSize);
			cb.setTextMatrix(x, page.getHeight()-pageNumberSize);
			cb.showText(header.getText());
			cb.endText();
            cb.restoreState();
        }

        if (footer.isHasFooter()) {
        	// 显示 page footer
        	cb.saveState();
        	float x = 0.0f;
        	float textBase = document.bottomMargin() - footerHeight;
//        	for (int i=0; i < footerTexts.size(); i++) {
//        		PdfFooter text = (PdfFooter) footerTexts.get(i);
        	if(footer!=null){	
        		cb.beginText();
        		cb.setFontAndSize(font, footer.getFontSize());
        		if (footer.isBold())
        			cb.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE);
        		else
        			cb.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
        		if (footer.getTextAlign() == Element.ALIGN_CENTER) {
        			x = document.getPageSize().getWidth() / 2;
        		} else if (footer.getTextAlign() == Element.ALIGN_LEFT) {
        			x = document.left();
        		} else {
        			x = document.right();
        		}
        		cb.showTextAligned(footer.getTextAlign(), footer.getText(), x, textBase, 0.0f);
        		cb.endText();
        	
        	
    		cb.restoreState();
        	}
        }
        // reset skipFirstWrite
    //    skipFirstWrite = false;
        
		if (resetMargin) {
			// 设置 margin
			setMargin(document);
			resetMargin = false;
		}
		// not empty document
	}
	
	/**
	 * 打开文档事件
	 */
	public void onOpenDocument(PdfWriter writer, Document document) {
        try {
            gstate = new PdfGState();
            gstate.setFillOpacity(0.3f);
            gstate.setStrokeOpacity(0.3f);
            // initialization of the template
            tpl = writer.getDirectContent().createTemplate(100, 100);
            tpl.setBoundingBox(new Rectangle(-20, -20, 100, 100));
        	
        	if (footer.isShowPageNumber()) {
	            // initialization of the template
	            tpl = writer.getDirectContent().createTemplate(100, 100);
	            tpl.setBoundingBox(new Rectangle(-20, -20, 100, 100));
	            // initialization of the font
	            if (ChineseFont.containsChinese(pageNumberStyle) && ChineseFont.BASE_CHINESE_FONT != null)
	            	font = ChineseFont.BASE_CHINESE_FONT;
	            else
	            	font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false);

                // 计算需要多少空格来代替 "总页数" 的位置 
                float size = font.getWidthPoint("000", this.pageNumberSize);
                float blankUnitSize = font.getWidthPoint(" ", this.pageNumberSize);
                int needSpaceChars = Math.round(size / blankUnitSize);
                blankTextChars = "";
                for (int i = 0; i < needSpaceChars; i++)
                    blankTextChars += " ";
        	}
            // 保存初始的 top、bottom margin
        	_topMargin = document.topMargin();
        	_bottomMargin = document.bottomMargin();
        }
        catch(Exception e) {
            throw new ExceptionConverter(e);
        }
	}

	/**
	 * Start page 事件
	 */
	public void onStartPage(PdfWriter writer, Document document) {
        if (table!=null) {
        	Rectangle page = document.getPageSize();
        	table.setTotalWidth(page.getWidth()-document.leftMargin()-document.rightMargin());
        	table.writeSelectedRows(0, -1, document.leftMargin(),
        		page.getHeight() - document.topMargin() + table.getTotalHeight(),
        		writer.getDirectContent());
        }
	}

	/**
	 * Close document 事件
	 */
    public void onCloseDocument(PdfWriter writer, Document document) {
    	if (footer.isShowTotalNumber() && footer.isShowPageNumber() && tpl != null) {
	        tpl.beginText();
	        tpl.setFontAndSize(font, pageNumberSize);
            // 调整位置 (x 坐标)
            float x = 0.0f;
            int totalPage = writer.getPageNumber() - 1;
            if (totalPage < 10) // 1 位数
                x += font.getWidthPoint("00", pageNumberSize) / 2;
            else if (totalPage < 100) // 2 位数
                x += font.getWidthPoint("0", pageNumberSize) / 2;
            else if (totalPage > 1000) // 4 位数或更多
                x -= font.getWidthPoint("0", pageNumberSize) / 2;
            
	        tpl.setTextMatrix(x, 0);
	        tpl.showText(""+totalPage);
	        tpl.endText();
    	}
     }
}
