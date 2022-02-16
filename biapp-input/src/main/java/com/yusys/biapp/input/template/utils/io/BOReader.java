/**
 * 
 */
package com.yusys.biapp.input.template.utils.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.google.common.collect.Lists;
import com.yusys.biapp.input.template.utils.object.BOList;

/**
 * <pre>
 * Title:       序列化文件读取和写入操作类
 * Description: 序列化文件读取和写入操作类
 * </pre>
 * @author guojiangping
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class BOReader {

	/**
	 * 向文件中写入一个集合 流程： 1.遍历BOList集合并将其每个对象序列化 2.通过对象流将逐个对象序列写入文件中
	 * @param filePath 要写入文件的路径
	 * @param dataList 要写入的集合
	 * @throws Exception 文件没有找到或创建对象流失败异常
	 */
	public static File write(List<BOList> dataList, String defaultFileName) throws Exception {
		File localFile = null;
		localFile = new File(defaultFileName);
		ObjectOutputStream os = null;
		FileOutputStream fs = null;
		try {
			fs = new FileOutputStream(localFile);
			os = new ObjectOutputStream(fs);
			os.writeInt(dataList.size());
			for (BOList boList:dataList ) {
				os.writeObject(boList);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (os != null) {
				os.close();
			}
			if (fs != null) {
				fs.close();
			}
		}
		return localFile;
	}

	/**
	 * 从文件中读取一个集合 流程： 1.通过对象流读取文件为了避免远程读取文件使用流的方式读取 2.遍历BFOList集合通过BFOListHelper类将逐个对象反序列化
	 * @param inputStream 要读取文件流
	 * @return list 读取文件得到的集合
	 * @throws Exception 文件没有找到或创建对象流失败异常
	 */
	public static List<BOList> readList(InputStream inputStream) throws Exception {
		List<BOList> list = Lists.newArrayList();
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(inputStream);
			int size = in.readInt();
			
			for (int i = 0; i < size; i++) {
				BOList bh = (BOList) in.readObject();
				list.add(bh);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return list;
	}

}
