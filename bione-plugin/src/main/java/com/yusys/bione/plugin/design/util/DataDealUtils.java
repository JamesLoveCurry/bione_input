package com.yusys.bione.plugin.design.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.comp.utils.*;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.design.service.RptDataDealBS;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

public class DataDealUtils {

	
	public static Map<String,Object> saveImport(String pathname,String dsId,String serverId) throws  ClassNotFoundException{
		Map<String,Object> params=new HashMap<String, Object>();
		if (FilepathValidateUtils.validateFilepath(pathname)) {
			RptDataDealBS rptDataBS=SpringContextHolder.getBean("rptDataDealBS");
			FileInputStream fe = null;
			InputStreamReader fr = null;
			BufferedReader br = null;
			try{
				fe = new FileInputStream(new File(pathname));
				fr = new InputStreamReader(fe, "UTF-8");
				br =new BufferedReader(fr);
				StringBuilder sb=new StringBuilder("");
				String s="";
				while((s=br.readLine())!=null){
					sb.append(s);
				}
				rptDataBS.saveImport(sb.toString(),dsId,serverId);
			}
			catch(Exception e){
			}
			finally{
				if(fe != null){
					try {
						fe.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(fr != null){
					try {
						fr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(br != null){
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			params.put("isSucess", true);
		}
		return params;
	}
	
	public static Map<String,Object> saveImport(String pathname,String dsId) throws ClassNotFoundException{
		Map<String,Object> params=new HashMap<String, Object>();
		if (FilepathValidateUtils.validateFilepath(pathname)) {
			RptDataDealBS rptDataBS=SpringContextHolder.getBean("rptDataDealBS");
			FileInputStream fe = null;
			InputStreamReader fr = null;
			BufferedReader br = null;
			try{
				if(FilepathValidateUtils.validateFilepath(pathname)) {
					fe = new FileInputStream(new File(pathname));
					fr = new InputStreamReader(fe, "UTF-8");
					br = new BufferedReader(fr);
					StringBuilder sb=new StringBuilder("");
					String s="";
					while((s=br.readLine())!=null){
						sb.append(s);
					}
					rptDataBS.saveImport(sb.toString(),dsId);
				}
			}
			catch(Exception e){
			}
			finally{
				if(fe != null){
					try {
						fe.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(fr != null){
					try {
						fr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(br != null){
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			params.put("isSucess", true);
		}
		return params;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> exportImport(String Ids,String realPath,String methodNm) {
		Map<String,Object> maps=new HashMap<String, Object>();
		if (FilepathValidateUtils.validateFilepath(realPath)) {
			RptDataDealBS rptDataBS=SpringContextHolder.getBean("rptDataDealBS");
			Object[] objs=new Object[1];
			objs[0] = ArrayUtils.asList(Ids, ",");
			Map<String,Object> map=(Map<String, Object>) ReflectionUtils.invokeMethodByName(rptDataBS, methodNm, objs);
			String filepath=realPath + GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator+RandomUtils.uuid2()+".txt";
			FileOutputStream fo = null;
			OutputStreamWriter fw = null;
			try{
				fo = new FileOutputStream(new File(filepath));
				fw = new OutputStreamWriter(fo, "UTF-8");
				fw.append(JSON.toJSONString(map));
				fw.flush();
			}
			catch(Exception e){
			}
			finally{
				if(fo != null){
					try {
						fo.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(fw != null){
					try {
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			maps.put("filepath", filepath);
		}
		return maps;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> exportImportRpt(String Ids,String realPath,String methodNm, 
			String busiType, String verId) {
		Map<String,Object> maps=new HashMap<String, Object>();
		RptDataDealBS rptDataBS=SpringContextHolder.getBean("rptDataDealBS");
		Object[] objs=new Object[3];
		objs[0] = ArrayUtils.asList(Ids, ",");
		objs[1] = busiType;
		objs[2] = verId;
		Map<String,Object> map=(Map<String, Object>) ReflectionUtils.invokeMethodByName(rptDataBS, methodNm, objs);
		String filepath=realPath + GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator+RandomUtils.uuid2()+".txt";
		FileOutputStream fo = null;
		OutputStreamWriter fw = null;
		try{
			fo = new FileOutputStream(new File(filepath));
			fw = new OutputStreamWriter(fo, "UTF-8");
			fw.append(JSON.toJSONString(map));
			fw.flush();
		}
		catch(Exception e){
			e.printStackTrace();
		} finally{
			if(fo != null){
				try {
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fw != null){
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		maps.put("filepath", filepath);
		return maps;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> exportImport(String msgNos,String rptCodes,String realPath,String methodNm){
		Map<String,Object> maps=new HashMap<String, Object>();
		if (FilepathValidateUtils.validateFilepath(realPath)) {
			RptDataDealBS rptDataBS=SpringContextHolder.getBean("rptDataDealBS");
			Object[] objs=new Object[2];
			String msgArray[]=StringUtils.split(msgNos,",");
			String rptArray[]=StringUtils.split(rptCodes,",");
			objs[0] = Arrays.asList(msgArray);
			objs[1] = Arrays.asList(rptArray);
			Map<String,Object> map=(Map<String, Object>) ReflectionUtils.invokeMethodByName(rptDataBS, methodNm, objs);
			String filepath=realPath + GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator+RandomUtils.uuid2()+".txt";
			FileOutputStream fo = null;
			OutputStreamWriter fw = null;
			try{
				fo = new FileOutputStream(new File(filepath));
				fw = new OutputStreamWriter(fo, "UTF-8");
				fw.append(JSON.toJSONString(map));
				fw.close();
			}
			catch(Exception e){
			}
			finally{
				if(fo != null){
					try {
						fo.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(fw != null){
					try {
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			maps.put("filepath", filepath);
		}
		return maps;
	}
	
	/**
	 * 框架原方法，暂时保留 
	 * @param file
	 * @param classNm
	 * @param fieldNms
	 * @return Json格式的字符串
	 * @throws ClassNotFoundException
	 * @author 郭驰
	 * @since  2017-09-08
	 */
	public static String getDataInfo(File file,String classNm,List<String> fieldNms) throws ClassNotFoundException{
		return getStringDataInfo(file,classNm,fieldNms);
	}
	
	/**
	 * 
	 * @param file
	 * @param classNm
	 * @param fieldNms
	 * @return  Json格式的字符串
	 * @throws ClassNotFoundException
	 * @author 郭驰
	 * @since  2017-09-08
	 */
	public static String getStringDataInfo(File file,String classNm,List<String> fieldNms) throws ClassNotFoundException{
		Map<String,Object> tmp = getMapDataInfo(file,classNm,fieldNms);
		if(tmp != null){
			return JSON.toJSONString(tmp);
		}else{
			return "";
		}
	}
	/**
	 * 解决部分情况上传文件 返回到前台的文件名是乱码的问题
	 * @param file
	 * @param classNm
	 * @param fieldNms
	 * @return
	 * @throws ClassNotFoundException
	 * @author 郭驰
	 * @since  2017-09-08
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMapDataInfo(File file,String classNm,List<String> fieldNms) throws ClassNotFoundException{
		Map<String,Object> params=new HashMap<String, Object>();
		RptDataDealBS rptDataBS=SpringContextHolder.getBean("rptDataDealBS");
		FileInputStream fe = null;
		InputStreamReader fr = null;
		BufferedReader br = null;
		try{
			fe = new FileInputStream(file);
			fr = new InputStreamReader(fe, "UTF-8");
			br = new BufferedReader(fr);
			StringBuilder sb=new StringBuilder("");
			String s="";
			while((s=br.readLine())!=null){
				sb.append(s);
			}
			JSONObject map = JSON.parseObject(sb.toString());
			List<Map<String,Object>> rows=new ArrayList<Map<String,Object>>();
			for(String cla:map.keySet()){
				if(cla.equals(classNm)){
					String jsons = map.getString(cla);
					if(cla.equals(RptMgrReportInfo.class.getName()+" 1")){
						cla=RptMgrReportInfo.class.getName();
					}
					
					List<Object> lists = (List<Object>)JSON.parseArray(jsons, Class.forName(cla));
						for(Object info:lists){
							Map<String,Object> row=new HashMap<String, Object>();
							row.put("fileNm", file.getPath());
							if(fieldNms!=null&&fieldNms.size()>0){
								for(String fieldNm: fieldNms){
									row.put(fieldNm, ReflectionUtils.getFieldValue(info, fieldNm));
								}
							}
							row.put("isExist", rptDataBS.validateExist(info, Class.forName(cla)));
							rows.add(row);
						}
					}
			}	
			params.put("Rows", rows);
			return params;
		}
		catch(Exception e){
		}
		finally{
			if(fe != null){
				try {
					fe.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fr != null){
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
}
