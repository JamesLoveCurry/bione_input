package com.yusys.bione.plugin.spreadjs.htz.impl;

import java.io.OutputStream;

import com.yusys.bione.plugin.spreadjs.htz.Excel2Pdf;
import com.yusys.bione.plugin.spreadjs.htz.core.Convert;
import com.yusys.bione.plugin.spreadjs.htz.core.HeaderText;
import com.yusys.bione.plugin.spreadjs.htz.pagesetting.PageSetting;
import com.yusys.bione.plugin.spreadjs.htz.pdfevent.PageEvent;


public class Excel_TO_PDF implements Excel2Pdf{

	public void conVertFormLocal(String strExcelFilePath, OutputStream outPut, PageSetting pageSetting) 
				throws Exception 
	{
		Convert con=new Convert(strExcelFilePath, outPut);
		if(pageSetting!=null)
		{
			con.setPageSize(pageSetting.getPageSize());
			HeaderText text=con.getHeader();
			PageEvent event=new PageEvent();
			setUp(event, pageSetting, text);
			//int iRows = con.getIRows();
			int iTotalRows = con.getITotalrows();
			if(iTotalRows > 3000)
			{
				for(int i = 0; i < (iTotalRows / 3000) + 1; i++)
				{
					con.convert(event);
				}
			}else
			{
				con.convert(event);
			}
		}else{
			throw new Exception("ҳ�����δ����");
		}
		
	}

	public void convertFromDB(String strExcelFileName, OutputStream outPut, PageSetting pageSetting) 
				throws Exception 
	{
//		FileIntoDB db=new FileIntoDB();
//		db.fileIntoDB("D:\\CRM1.xls","crm11");
//		InputStream input=db.getInputStream(excelFileName);
//		Convert con=new Convert(input,output);
//		if(pageSetting!=null){
//		con.setPageSize(pageSetting.getPageSize());
//		HeaderText text=con.getHeader();
//		PageEvent event=new PageEvent();
//		event.setHeaderText(text);
//		event.setFooter(pageSetting.getFooter());
//		event.setHeader(pageSetting.getHeader());
//		event.setPageNumberSize(pageSetting.getFooter().getFontSize());
//		event.setPageNumberStyle(pageSetting.getFooter().getPageNumberStyle());
//		con.transform(event);
//		}else{
//			throw new Exception("ҳ�����δ����");
//		}
		
	}

	public void convertFromLocal(String strExcelFilePath, String strPdfFilePath, PageSetting pageSetting) 
				throws Exception 
	{
		Convert con=new Convert(strExcelFilePath,strPdfFilePath);
		if(pageSetting!=null){
		con.setPageSize(pageSetting.getPageSize());
		HeaderText text=con.getHeader();
		PageEvent event=new PageEvent();
		setUp(event,pageSetting,text);
		con.convert(event);
		}else{
			throw new Exception("ҳ�����δ����");
		}
		
	}
	
	/**
	 * ����PdfEvent�¼�
	 * @param event 
	 * @param pageSetting
	 * @param text
	 */
	private void setUp(PageEvent event,PageSetting pageSetting,HeaderText text){
		event.setHeaderText(text);
		event.setFooter(pageSetting.getFooter());
		event.setHeader(pageSetting.getHeader());
		event.setPageNumberSize(pageSetting.getFooter().getFontSize());
		event.setPageNumberStyle(pageSetting.getFooter().getPageNumberStyle());
	}
	

}
