package com.yusys.bione.frame.activiticustom.utils;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

public class ClassUtil {
	
	public static Set<Class<?>> getAllClassByPackname(String packageName) throws Exception{
		Set<Class<?>> classSet = new HashSet<Class<?>>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String packagePath = packageName.replace(".", System.getProperty("file.separator"));
		URL url = classLoader.getResource(packagePath);
		String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
		//System.out.println(url.getFile()+"=="+url.getPath()+"=="+filePath);
		//获取指定包名下的所有class
		if("file".equals(url.getProtocol())) {
			getAllClass(packageName,filePath,classSet);
		}
		return classSet;
		
	}
	
	public static void getAllClass(String packageName,String filePath,Set<Class<?>> classSet) {
		File dir = new File(filePath);
		if(!dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return ((file.isFile() && file.getName().endsWith(".class")) || file.isDirectory());
			}
		});
		for(File file : files) {
			if(file.isFile()) {
				String className = file.getName().substring(0, file.getName().length()-6);
				try {
					Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(packageName+"."+className);
					classSet.add(cls);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}else {
				getAllClass(packageName+"."+file.getName(),file.getAbsolutePath(),classSet);
			}
		}
	
	}
	public static void main1(String[] args) throws Exception {
		ClassUtil.getAllClassByPackname("com.yusys.bione.frame.activiti");
	}

}
