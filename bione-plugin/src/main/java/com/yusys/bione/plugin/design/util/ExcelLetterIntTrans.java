package com.yusys.bione.plugin.design.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * Title: Excell列字母与数字转换工具类
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author weijx weijx@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class ExcelLetterIntTrans {
	public static void intToLetter(int num,StringBuilder letter){
		int z=num/26;
		int y=num%26;
		if(y==0){
			y=26;
			z--;
		}
		if(z>26){
			letter.insert(0, (char)(y+64));
			intToLetter(z,letter);
		}
		else{
			letter.insert(0, (char)(y+64));
			if(z>0){
				letter.insert(0, (char)(z+64));
			}
		}
	}
	
	public static int letterToInt(String letter){
		int length=letter.length();
		int result=0;
		for(int i=0;i<length;i++){
			int num=letter.charAt(i)-64;
			result+=num*Math.pow(26, length-i-1);
		}
		return result;
	}
	

	public static int letterToRow(String letter){
		int i = 0;
		while (letter.charAt(i) <= 'Z' && letter.charAt(i) >= 'A') {
			i++;
		}
		int row=Integer.parseInt(StringUtils.substring(
				letter, i, letter.length()));
		return row;
	}
	
	public static int letterToCol(String letter){
		int i = 0;
		while (letter.charAt(i) <= 'Z' && letter.charAt(i) >= 'A') {
			i++;
		}
		int col = letterToInt(StringUtils.substring(
				letter, 0, i));
		return col;
	}
	
	public static Map<String,Integer> lettrToCell(String letter){
		int i = 0;
		while (letter.charAt(i) <= 'Z' && letter.charAt(i) >= 'A') {
			i++;
		}
		int col = letterToInt(StringUtils.substring(
				letter, 0, i));
		int row=Integer.parseInt(StringUtils.substring(
				letter, i, letter.length()));
		Map<String,Integer> cellInfo=new HashMap<String, Integer>();
		cellInfo.put("row", row);
		cellInfo.put("col", col);
		return cellInfo;
	}
	
	public static String addRow(String letter,int add){
		int i = 0;
		while (letter.charAt(i) <= 'Z' && letter.charAt(i) >= 'A') {
			i++;
		}
		int row=Integer.parseInt(StringUtils.substring(
				letter, i, letter.length()));
		return StringUtils.substring(
				letter, 0, i)+(row+add);
	}
	
	public static String addCol(String letter,int add){
		int i = 0;
		while (letter.charAt(i) <= 'Z' && letter.charAt(i) >= 'A') {
			i++;
		}
		int col = letterToInt(StringUtils.substring(
				letter, 0, i));
		int row=Integer.parseInt(StringUtils.substring(
				letter, i, letter.length()));
		StringBuilder cm=new StringBuilder("");
		intToLetter(col+add, cm);
		return cm.toString()+row;
	}
	
	public static Map<String,Integer> addRowLetter(String letter,int add){
		int i = 0;
		while (letter.charAt(i) <= 'Z' && letter.charAt(i) >= 'A') {
			i++;
		}
		int row=Integer.parseInt(StringUtils.substring(
				letter, i, letter.length()));
		return lettrToCell(StringUtils.substring(
				letter, 0, i)+(row+add));
	}
	
	public static String addRow(int col,int row,int add){
		StringBuilder cm=new StringBuilder("");
		intToLetter(col, cm);
		return cm.append((row+add)).toString();
	}
}
