package com.yusys.bione.plugin.spreadjs.htz.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.yusys.bione.plugin.spreadjs.htz.pdfpag.PdfPageSize;

/**
 * 一个pdf的抽象模板类
 */
public abstract class WriterPdf {
	/**document属性*/
	protected Document document=null;
	/**设置pdf页面大小、默认为A4*/
	public Rectangle pageSize = PdfPageSize.A4;
	/**pdf输出流*/
	protected PdfWriter writer=null;
	/**获得pdf页面大小*/
	public Rectangle getPageSize() {
		return pageSize;
	}
	/**设置pdf页面大小*/
	public void setPageSize(Rectangle pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 写到本地的pdf文件
	 * @param destFilePath pdf文件路径
	 */
	public WriterPdf(String destFilePath){
		document = new Document(null,20,20,60,30);
		System.out.println("-->创建Document对象成功!");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(destFilePath);
			writer = PdfWriter.getInstance(document,fos);
		} catch (Exception e) {
			System.err.println("-->创建Document对象失败!");
			e.printStackTrace();
		}
		finally{
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	/**
	 * 此方法一般是将是写到网络中
	 * @param output 输出流
	 */
	public WriterPdf(OutputStream output){
		try {
			document = new Document(null, 30, 30, 25, 25);
			System.out.println("-->创建Document对象成功!");
			writer = PdfWriter.getInstance(document,output);
		} catch (DocumentException e) {
			System.err.println("-->创建Document对象失败!");
			e.printStackTrace();
		}
	}
	/**
	 * 关闭document对象
	 * @throws Exception
	 */
	public void CloseDocument() throws Exception{
		try {
			if(document!=null){
			   document.close();
			System.out.println("-->关闭Document对象成功!");
			}
		} catch (Exception e) {
			System.err.println("-->关闭Document对象失败!");
			e.printStackTrace();
		}
	}
}