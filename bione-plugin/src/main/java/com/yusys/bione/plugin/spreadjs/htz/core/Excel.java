package com.yusys.bione.plugin.spreadjs.htz.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;
/**
 * ����excel�ļ�����
 */

public class Excel {
	/**������Ŀ*/
	private Sheet[] sheets=null;
	/**excel�ļ��Ƿ񱻱���*/
	private boolean isProtected=false;
	/**����Workbook����*/
	private Workbook wb=null;
	/**Ĭ�Ϲ��캯��*/
	public Excel(){
	}
	/**��ø�excel�ļ��еĹ�������*/
	public Sheet[] getSheets() {
		return sheets;
	}
	/**�鿴excel�ļ��Ƿ񱻱���*/
	public boolean isProtected() {
		return isProtected;
	}
	/**
	 * �ӱ��ض�ȡһ��excel�ļ�
	 * @param sourceFilePath excel�ļ�·��
	 */
	public void readExcel(String sourceFilePath){
		InputStream is=null;
		try {
			is=new FileInputStream(sourceFilePath);
			wb=Workbook.getWorkbook(is);
			sheets=wb.getSheets();
			isProtected=wb.isProtected();
			System.out.println("-->�ӱ��ض�ȡexcel�ļ��ɹ�!");
		} catch (Exception e) {
			System.err.println("-->�ӱ��ض�ȡexcel�ļ�ʧ��!");
			e.printStackTrace();
		}
		finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	/**
	 * ����ݿ��ȡһ��excel�ļ�������
	 * @param is excel�ļ�������
	 */
	public void readExcelFromDB(InputStream is){
		Workbook wb=null;
		try {
			wb=Workbook.getWorkbook(is);
			sheets=wb.getSheets();
			isProtected=wb.isProtected();
			System.out.println("-->����ݿ��ȡexcel�ļ��ɹ�!");
		} catch (Exception e) {
			System.err.println("-->����ݿ��ȡexcel�ļ�ʧ��!");
			e.printStackTrace();
		}finally{
		}
	}
	
	/**
	 * �رչ������
	 *
	 */
	public void closeWorkbook(){
		if(wb!=null){
		wb.close();
		System.out.println("-->�ر�Workbook����ɹ�!");
		}
	}
}
