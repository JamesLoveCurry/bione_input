package com.yusys.bione.comp.utils;

/**
 * 
 * <pre>
 * Title:	DES加密用到的公共方法，如字符串拷贝，改变字符串某个位置的字符
 * Description: 应用系统管理
 * </pre>
 * 
 * @author fangjuan  fangjuan@yuchengtech.com
 * @version 1.00.00
 * @since	2013-12-22
 *
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class DesPublicUtils {
	
	/**
	 * 将DATA的index位置的字符改变为value
	 * @param Data
	 * @param index
	 * @param value
	 * @return
	 */
	public static byte[] changByte(byte[] Data, int index, byte value) {
		if (index < Data.length) {
			Data[index] = value;
			return Data;
		}
		if (index == Data.length) {
			byte[] tmp = Data;
			Data = new byte[tmp.length + 1];
			for(int i=0; i<tmp.length; i++){
				Data[i] = tmp[i];
			}
			Data[tmp.length] = value;
		}
		return Data;

	}
	
	/**
	 * 从src的第srcIndex位置拷贝n个字符到des的desIndex后
	 * 对字符串进行
	 * @param des
	 * @param desIndex
	 * @param src
	 * @param srcIndex
	 * @param Size
	 * @return
	 */
	public static byte[] byteArrayCopy(byte[] src, int first, int last){
		if(first > src.length || last > src.length || first > last){
			return null;
		}
		byte result[] = new byte[last - first];
		for(int i=0; i<last - first; i++){
			result[i] = src[first + i];
		}
		return result;
	}
	
	/**
	 * 给Data的length位置加一个value
	 * @param Data
	 * @param length
	 * @param value
	 * @return
	 */
	public static byte[] byteAddChar(byte[] des, int index, byte value){
		if(des == null){
			return null;
		}
		if(index > des.length){
			return des;
		}
		byte[] tmp = new byte[des.length + 1];
		for(int i=0; i<des.length + 1; i++){
			if(i < index){
				tmp[i] = des[i];
			}else if( i == index){
				tmp[i] = value;
			}else{
				tmp[i] = des[i - 1];
			}
		}
		return tmp;
	}
	/**
	 * 从src的第srcIndex位置拷贝n个字符到des的desIndex后
	 * 对字符串进行
	 * @param des
	 * @param desIndex
	 * @param src
	 * @param srcIndex
	 * @param n
	 * @return
	 */
	
	public static byte[] byteCopy(byte[] des, int desIndex, byte[] src,
			int srcIndex, int n) {
		if(des == null){
			return null;
		}
		if (srcIndex > src.length) {
			return des;
		}
		if (n + srcIndex > src.length) {
			n = src.length - srcIndex;
		}
		if (desIndex > des.length) {
			desIndex = des.length;
		}
		if((n + desIndex) > des.length){
			byte orgDes[] = des; 
			des = new byte[n + desIndex];
			for(int i=0; i< orgDes.length; i++ ){
				des[i] = orgDes[i];
			}
		}
		for(int i = 0; i < n; i++){
			des[desIndex + i] = src[srcIndex + i];
		}
		return des;
	}

}
