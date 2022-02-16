package com.yusys.bione.plugin.spreadjs.htz.pagesetting;


import com.itextpdf.text.Rectangle;
import com.yusys.bione.plugin.spreadjs.htz.pdfpag.PdfFooter;
import com.yusys.bione.plugin.spreadjs.htz.pdfpag.PdfHeader;
import com.yusys.bione.plugin.spreadjs.htz.pdfpag.PdfPageSize;

/**
 * ҳ������
 * �����ṩ�ܶ�Ĭ������
 */
public class PageSetting {
	/**����ҳ��*/
	private PdfFooter footer=new PdfFooter();
	/**����ҳü*/
	private PdfHeader header=new PdfHeader();
	/**����ҳ���С*/
	private Rectangle pageSize=PdfPageSize.A4;
	/**���pdf��ҳ��*/
	public PdfFooter getFooter() {
		return footer;
	}
	/**����pdf��ҳ��*/
	public void setFooter(PdfFooter footer) {
		this.footer = footer;
	}
	/**���pdf��ҳü*/
	public PdfHeader getHeader() {
		return header;
	}
	/**����pdf��ҳü*/
	public void setHeader(PdfHeader header) {
		this.header = header;
	}
	/**���ҳ��ֽ�Ŵ�С*/
	public Rectangle getPageSize() {
		return pageSize;
	}
	/**����ҳ��ֽ�Ŵ�С*/
	public void setPageSize(Rectangle pageSize) {
		this.pageSize = pageSize;
	}

	
}
