package com.yusys.bione.plugin.spreadjs.htz;

import java.io.OutputStream;

import com.yusys.bione.plugin.spreadjs.htz.pagesetting.PageSetting;

public interface Excel2Pdf {
	
	/**
	 * �ѱ��ص�excel�ļ�ת��Ϊ���ص�PDF�ļ� 
	 * @param strExcelFilePath excel�ļ����ڱ���·��
	 * @param strPdfFilePath ���PDF�ļ���·��
	 * @param pageSetting ����ҳ�������Ϣ���� 
	 * @throws Exception
	 */
	public void convertFromLocal(String strExcelFilePath,String strPdfFilePath,PageSetting pageSetting) throws Exception;
	
	
	/**
	 * ����ݿ��д�ȡexcel�ļ�ת��ΪPDF�ļ������ 
	 * @param strExcelFileName excel�ļ����
	 * @param outPut PDF�ļ������ 
	 * @param pageSetting ����ҳ�������Ϣ����
	 * @throws Exception
	 */
	public void convertFromDB(String strExcelFileName,OutputStream outPut,PageSetting pageSetting) throws Exception;
	
	/**
	 * �ѱ��ص�excel�ļ�ת��ΪPDF�ļ������ 
	 * @param strExcelFilePath  excel�ļ����ڱ���·��
	 * @param outPut PDF�ļ������
	 * @param pageSetting  ����ҳ�������Ϣ����
	 * @throws Exception
	 */
	public void conVertFormLocal(String strExcelFilePath,OutputStream outPut,PageSetting pageSetting) throws Exception;
}
