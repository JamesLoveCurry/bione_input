package com.yusys.bione.plugin.yuformat.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.yuformat.service.ExportBusinessBS;
import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;

/**
 * <pre>
 * Title: 
 * Description:
 * </pre>
 * 
 * @author wangxy31@yusys.com.cn
 * @version 1.00.00
 * @date 
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：		  修改日期:     修改内容:
 * </pre>
 */

@Controller
@RequestMapping("/export/business/data")
public class ExportBusinessController extends BaseController {
	
	private YuFormatUtil bsUtil = new YuFormatUtil();
	private ExportBusinessBS businessBS = new ExportBusinessBS();
	
	/**
	 * 【数据查询】导出CVS
     * @param response
     * @throws IOException
     */
	@GetMapping("/exportCSV")
    public void exportCVS(HttpServletRequest request, HttpServletResponse response, String org_no, String data_dt, 
    		String tab_name, String tab_name_en, String strsql, String report_type, String model_type, String cr_tab, String cr_col) throws IOException {
        response.setContentType("multipart/form-data");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode(tab_name,"UTF-8") +".csv");
     
        ZipOutputStream zos = null;
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    
        // 转换成报送机构号
        org_no = businessBS.getReportOrgNo(org_no, report_type); // report_type：报送类型(04,07...); model_type：模块类型（east,fsrs...）

        try {
        	Map<String, String> map = bsUtil.getDataBaseparam(new String[]{"base.business.download","base.business.detailed","base.download.filepath","base.download.zippath"});
	        String str_download = map.get("download");
	        String str_onerun = map.get("detailed");
	        String str_downfile = map.get("filepath");
	        String str_downzip = map.get("zippath");
	        int download= Integer.parseInt(str_download); // 获取默认允许下载数量范围(50w)
		    int onerun = Integer.parseInt(str_onerun);  // 读取记录数(1w)
	        
			// 首先判断当前条数是否超过设置的(50w)数据量范围，超过则生成多个文件存于服务器，未超过直接下载
			String sqlCount = businessBS.packageSqlByCount(tab_name_en, strsql);
			HashVO[] hvsCount = bsUtil.getHashVOs(sqlCount);
			int datanum = hvsCount[0].getIntegerValue("c", 0);
			
			String dirPath = str_downfile + File.separatorChar + model_type + File.separatorChar + "download" + File.separatorChar + org_no + File.separatorChar + data_dt + File.separatorChar;
			String zipPath = str_downzip + File.separatorChar + model_type + File.separatorChar + "download" + File.separatorChar +org_no + File.separatorChar + data_dt + File.separatorChar;
			if(FilepathValidateUtils.validateFilepath(dirPath) && FilepathValidateUtils.validateFilepath(zipPath)) {
				File dirFile = new File(dirPath);
				File zipFile = new File(zipPath);
				if (!dirFile.exists()) {
					dirFile.mkdirs();
				} else { 
		        	File[] files = dirFile.listFiles();
		        	if (files != null) {
		        		for(int i=0; i<files.length; i++) {
			        		files[i].delete();
			        	}
		        	}
		        }
				if (!zipFile.exists()) {
					zipFile.mkdirs();
				}
				
				businessBS.getBusinessDataByCVS(request, response, model_type, cr_tab, cr_col, tab_name, tab_name_en, strsql, datanum, download, onerun, dirPath);
				
				if (datanum > download) {
					String recordId = "";
					// 先判断，是否存在
					boolean isHave = businessBS.getIntoFileData(org_no, data_dt, model_type);
					if (isHave) {
						recordId = businessBS.getIntoFileDataRid(org_no, data_dt, model_type);
					} else {
						// 创建一条数据
						recordId = UUID.randomUUID().toString().replaceAll("-", "");
						businessBS.insertIntoFileData(recordId, org_no, data_dt, model_type);
					}
					
			        // 当文件生成后，生成压缩文件
					// 压缩文件的名称
				    String zipFileNm = tab_name + ".zip";
				    String zipRealPath = zipPath + zipFileNm;
				    if (FilepathValidateUtils.validateFilepath(zipRealPath)) {
					    File zipRealFile = new File(new String(zipRealPath.getBytes("utf-8")));
					    if(zipRealFile.exists()) {
					    	zipRealFile.delete();
					    }
					    zos = new ZipOutputStream(new FileOutputStream(zipRealFile)); //获取输出流
					    
					    File[] files = dirFile.listFiles();
					    ZipEntry zipEntry = null;
					    for(int i=0; i<files.length; i++) {
					    	zipEntry = new ZipEntry(new String(files[i].getName().getBytes("utf-8")));
							zos.putNextEntry(zipEntry);
							
							fis = new FileInputStream(files[i]); //获取输入流
				            bis = new BufferedInputStream(fis, 1024 * 10);
				            
				            int length = 0;
				            byte[] buff = new byte[1024 * 10]; //每次读取的字节数
				            while ((length = bis.read(buff)) >0) {
				                zos.write(buff, 0, length);
				                zos.flush();
				            }
					    }
					    
					    fis.close();
			            bis.close();
			            zos.close();
				        
				        // 更新数据
				        businessBS.updateFileData(recordId, zipFileNm, zipPath);
				    }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}