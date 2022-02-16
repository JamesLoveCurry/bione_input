package com.yusys.bione.plugin.yuformat.web;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Types;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yusys.bione.comp.utils.RSAEncryptUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.utils.EncodeUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.yuformat.service.AbstractDownloadFile;
import com.yusys.bione.plugin.yuformat.service.AbstractDownloadFile.DownLoadInStreamVO;
import com.yusys.bione.plugin.yuformat.utils.FileLoad;
import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.HashVOStruct;
import com.yusys.bione.plugin.yuformat.utils.InsertSQLBuilder;
import com.yusys.bione.plugin.yuformat.utils.SQLBuilder;
import com.yusys.bione.plugin.yuformat.utils.UpdateSQLBuilder;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil2;

/**
 * 通用的单列表模板Control!
 * 
 * @author kf0612
 *
 */
@Controller
@RequestMapping("/frs/yuformat")
public class YuFormatFrameControl extends BaseController {

	private String contextPath = this.getContextPath(); // web路径

	private YuFormatUtil bsUtil = new YuFormatUtil(); //
	private YuFormatUtil2 bsUtil2 = new YuFormatUtil2(); //

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		try {
			String str_formatCode = this.getRequest().getParameter("code"); // 先从菜单入口得到编码
			System.out.println("FormatFrame编码=[" + str_formatCode + "]"); //
			getRequest().setAttribute("formatcode", str_formatCode); // 设置一下,这样在jsp中就能拿到

			//
			//HashVO[] hvs = bsUtil.getHashVOs("select * from rpt_cabin_formatframe where formatcode=?", new Object[] { str_formatCode }); // 取得页面类型！
			HashVO formatVO = bsUtil2.getFormatFrameVO(str_formatCode); //
			if (formatVO == null) { //
				logger.error("没有取得[" + str_formatCode + "]的formatFrame,在_FormatFrame.xml没有定义这个!"); //
				return null; //
			}

			String str_type = formatVO.getStringValue("formattype", ""); // 类型
			String str_jsfile = formatVO.getStringValue("jsfile", ""); // 自定义的js文件
			String str_jscode = formatVO.getStringValue("jscode", ""); //自定义js代码,这个暂时先不搞!

			getRequest().setAttribute("jsfile", str_jsfile); // 设置一下!
			getRequest().setAttribute("jscode", str_jscode); // 设置一下!

			// 处理定义!
			String str_define = formatVO.getStringValue("define", ""); // 页面定义..
			int li_pos_1 = str_define.indexOf("【"); //
			int li_pos_2 = str_define.indexOf("】"); //
			if (li_pos_1 >= 0 && li_pos_2 >= 0) {
				str_define = str_define.substring(li_pos_1 + 1, li_pos_2); // 去掉首尾的内容
			}

			logger.info("formatFrame定义【" + str_define + "】"); //
			getRequest().setAttribute("define", str_define); // 设置一下!

			if (str_type.equals("单表")) {
				return "/yuformatjsp/onegrid"; // 跳转到【单表jsp】
			} else if (str_type.equals("双表")) {
				return "/yuformatjsp/gridgrid"; // 跳转到【双表jsp】
			} else if (str_type.equals("树表")) {
				return "/yuformatjsp/base/treegrid"; // 跳转到【树表jsp】
			} else if (str_type.equals("三表")) {
				return "/yuformatjsp/base/grid3"; // 跳转到【三张表】
			} else if (str_type.equals("树卡片")) {
				return "/yuformatjsp/treecard"; // 跳转到【树卡片】
			} else {
				return null; //
			}
		} catch (Exception _ex) {
			logger.error("加载页面异常!", _ex);
			return null; //
		}
	}

	// 修改表格数据
	@RequestMapping(value = "/updategrid", method = RequestMethod.GET)
	public ModelAndView updateGrid(String _templetcode, String _pkvalue, String _gridname, String _isadd, String _fkname, String _fkvalue) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("_templetcode", StringUtils2.javaScriptEncode(_templetcode));
		params.put("_pkvalue", StringUtils2.javaScriptEncode(_pkvalue));
		params.put("_gridname", StringUtils2.javaScriptEncode(_gridname));
		params.put("_isadd", StringUtils2.javaScriptEncode(_isadd));
		params.put("_fkname", StringUtils2.javaScriptEncode(_fkname));
		params.put("_fkvalue", StringUtils2.javaScriptEncode(_fkvalue));

		return new ModelAndView("/yuformatjsp/grid_update", params); // 跳转..
	}

	// 删除数据
	@RequestMapping(value = "/deletegrid")
	@ResponseBody
	public JSONObject deleteGridData(String _templetcode, String _tablename, String _pkname, String _pkvalue) throws Exception {
		String str_sql = "delete from " + _tablename + " where " + _pkname + "='" + _pkvalue + "'"; //
		bsUtil.executeUpdate(str_sql); //
		JSONObject jso = new JSONObject(); //
		jso.put("msg", "删除数据成功!"); //
		return jso; //
	}

	// 保存表单数据..
	@RequestMapping(value = "/saveform", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveFormData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> dataMap = new HashMap<String, String>(); //
		Enumeration<?> pars = request.getParameterNames(); //
		while (pars.hasMoreElements()) { //
			String str_parName = "" + pars.nextElement(); //
			String str_parValue = request.getParameter(str_parName); //
			dataMap.put(str_parName.toLowerCase(), str_parValue); //
			// System.out.println(str_parName + "=[" + str_parValue + "]....");
			// //
		}

		// 实际保存数据..
		// String str_templetCode = dataMap.get("ft_templetcode"); //模板编码
		String str_saveTable = dataMap.get("ft_savetable"); // 表名
		String str_pkName = dataMap.get("ft_pkname"); // 主键字段名
		String str_isAdd = dataMap.get("ft_isadd"); // 是否新增

		SQLBuilder isql;
		if ("Y".equals(str_isAdd)) {
			dataMap.put(str_pkName, RandomUtils.uuid2());
			isql = new InsertSQLBuilder(str_saveTable);
		} else {
			String str_pkValue = (String) dataMap.get(str_pkName); // 主键值
			isql = new UpdateSQLBuilder(str_saveTable, str_pkName + "='" + str_pkValue + "'"); //
		}

		HashVOStruct hvst = bsUtil.getHashVOStruct("select * from " + str_saveTable + " where 1=2", false); //
		String[] str_cols = hvst.getHeaderName(); // 所有字段
		int[] str_colTypes = hvst.getHeaderType(); // 所有字段
		for (int i = 0; i < str_cols.length; i++) { // 遍历..
			int li_dataType = str_colTypes[i]; // 字段类型
			String str_colKey = str_cols[i].toLowerCase();//
			if (dataMap.containsKey(str_colKey)) {// 只修改表单提交的字段，表单中不存在的字段不修改
				String str_colValue = (String) dataMap.get(str_colKey); // 列中有值
				if (dataMap.containsKey(str_colKey + "_val")) {
					// 如果给控件设置了value，如popupedit，会新增一个字段str_colKey+"_val"，这个字段的值才是应该存数据库的值
					str_colValue = (String) dataMap.get(str_colKey + "_val");//
					// System.out.println("【str_colKey_val】： "+str_colKey+"_val"+"  【str_colValue】: "+str_colValue);
				}
				if (str_colValue == null || str_colValue.equals("")) {
					isql.putFieldValue(str_cols[i], (String) null); //
				} else {
					if (li_dataType == Types.VARCHAR) {
						isql.putFieldValue(str_cols[i], str_colValue); //
					} else if (li_dataType == Types.NUMERIC || li_dataType == Types.DECIMAL || li_dataType == Types.DOUBLE || li_dataType == Types.FLOAT) {
						isql.putFieldValue(str_cols[i], Double.parseDouble(str_colValue)); //
					} else if (li_dataType == Types.DATE || li_dataType == Types.TIMESTAMP) { // 日期类型
						isql.putFieldValue(str_cols[i], str_colValue); // 先暂时字符串处理...
					} else {
						isql.putFieldValue(str_cols[i], str_colValue); //
					}
				}
			}
		}

		String str_sql = isql.getSQL(); //
		bsUtil.executeUpdate(str_sql); // 执行数据库..

		System.out.println("保存表单数据成功[" + str_sql + "]!!!!"); //
		Map<String, Object> map = new HashMap<String, Object>(); //
		return map; //
	}

	// 通用万能通的远程调用方法,可以跳过control，直接转调自己的业务类!
	// 这个方法可以支持任意特殊字符串,如果是数组,则还要用一个key再包一下! 因为只接收JSON对象!
	// 方法必须是出参与入参都是JSONObject,在前端的调用方法是
	// CABIN.doClassMethodCall("com.yuchengtech.cabin.base.utils.CabinBSUtil2","testMethod",v_sql);
	@RequestMapping(value = "/doClassMethodCall", method = RequestMethod.POST)
	public JSONObject doClassMethodCall(String _class, String _method, String _parstr) throws Exception {
		System.err.println("进行一次远程调用【" + _class + "】【" + _method + "】【" + _parstr + "】"); //
		try {
			// 这里先用直接反射创建,后面要改成对象池...
			Class<?> cls = Class.forName(_class); // 获得类
			// 使用私钥解密
			String decryptValue =  RSAEncryptUtils.decryptSegmentedByPrivateKey(_parstr);
			// 参数中存在中文时,会乱码，所以，前台encode，这里 decode
			Method method = cls.getMethod(_method, new Class[] { JSONObject.class }); // 取得方法
			Object obj = cls.newInstance(); // 创建实例!
			JSONObject jsopar = JSON.parseObject(URLDecoder.decode(decryptValue,"UTF-8")); // 把字符串转换成JSON对象!
			Object returnObj = method.invoke(obj, new Object[] { jsopar }); //
			return (JSONObject) returnObj; //
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
			String str_ex = bsUtil.getExceptionStringBuffer(_ex); //
			JSONObject jso_ex = new JSONObject(); //
			jso_ex.put("Exception", str_ex); //
			return jso_ex; //
		}
	}

	/**
	 * 打开模板配置页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/configIndex")
	public ModelAndView configIndex(HttpServletRequest request, HttpServletResponse response) {
		ModelMap data = new ModelMap();
		data.put("templetid", StringUtils2.javaScriptEncode(request.getParameter("templetid")));
		data.put("templetCode", StringUtils2.javaScriptEncode(request.getParameter("templetCode")));
		return new ModelAndView("/yuformatjsp/cardgrid", data);
	}

	/**
	 * 跳转到业务人员配置界面
	 * 
	 * @return
	 */
	@RequestMapping("/custconf")
	public ModelAndView CustConf(String templetcode, String pkvalue) {
		ModelMap data = new ModelMap();
		data.put("templetcode", StringUtils2.javaScriptEncode(templetcode));
		data.put("pkvalue", StringUtils2.javaScriptEncode(pkvalue));
		// System.out.println("【templetcode】： " + templetcode + "【pkvalue】：" +
		// pkvalue);
		return new ModelAndView("/yuformatjsp/refdialog_custconf", data);//
	}

	// 编辑form表单中单独的某个字段
	@RequestMapping("/editformfield")
	public ModelAndView EditFormField(String templetcode, String fieldname, String templet_bid) {
		ModelMap data = new ModelMap();
		data.put("templetcode", StringUtils2.javaScriptEncode(templetcode));
		data.put("fieldname", StringUtils2.javaScriptEncode(fieldname));//
		data.put("templet_bid", StringUtils2.javaScriptEncode(templet_bid));
		// System.out.println("【templetcode】： " + templetcode + "【fieldname】：" +
		// fieldname);
		return new ModelAndView("/yuformatjsp/form_update", data);//
	}

	/**
	 * 跳转到列表参照 页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/refdialog_grid", method = RequestMethod.GET)
	public ModelAndView openRefDialogGrid(String templetCode) {
		System.err.println("还能接到吗？...." + templetCode); //
		return new ModelAndView("/yuformatjsp/refdialog_grid");//
	}

	@RequestMapping("/loadidxtree")
	public ModelAndView openSingleTreeDialog() {
		return new ModelAndView("/yuformatjsp/dialog_testsingletree");//
	}

	// 打开一个通用窗口,只传一个dialogid,然后根据这个id在前端取得参数
	@RequestMapping("/openClientDialog")
	public ModelAndView openClientDialog(HttpServletRequest request, HttpServletResponse response) {
		ModelMap data = new ModelMap();
		String str_dialogid = request.getParameter("dialogid"); //
		String str_jspname = request.getParameter("jspname"); //
		if (str_jspname.endsWith(".jsp")) {
			str_jspname = str_jspname.substring(0, str_jspname.length() - 4); //
		}
		data.put("dialogid", StringUtils2.javaScriptEncode(str_dialogid));
		return new ModelAndView("/yuformatjsp/" + str_jspname, data);// 弹出列表窗口
	}

	@RequestMapping("/openPathDialog")
	public ModelAndView openPathDialog(HttpServletRequest request, HttpServletResponse response) {
		ModelMap data = new ModelMap();
		String str_dialogid = request.getParameter("dialogid"); //
		String str_jspname = request.getParameter("jspname"); //
		// 2020 lcy 【后台管理】不可信的连接 代码优化
		if (str_jspname.endsWith(".jsp")) { //
			str_jspname = str_jspname.substring(0, str_jspname.length() - 4); //
		}
		data.put("dialogid", StringUtils2.javaScriptEncode(str_dialogid));
		return new ModelAndView(str_jspname, data);// 弹出列表窗口
	}

	// 打开文件上传窗口
	@RequestMapping(value = "/uploadFile", method = RequestMethod.GET)
	public ModelAndView uploadFile(String type, String batchid, String fromtable, String pkname, String pkvalue) {
		// System.out.println("--- uploadFile-cabin ---");
		ModelMap map = new ModelMap();
		map.put("type", StringUtils2.javaScriptEncode(type));
		map.put("batchid", StringUtils2.javaScriptEncode(batchid));
		map.put("fromtable", StringUtils2.javaScriptEncode(fromtable));
		map.put("pkname", StringUtils2.javaScriptEncode(pkname));
		map.put("pkvalue", StringUtils2.javaScriptEncode(pkvalue));
		return new ModelAndView("/yuformatjsp/wizard-index-upload", map);
	}

	/**
	 * 图片上传
	 * 
	 * @return
	 */
	@RequestMapping("/upload")
	@ResponseBody
	public String upload(Uploader uploader, HttpServletResponse response, String type, String batchid, String fromtable, String pkname, String pkvalue) throws Exception {
		// System.out.println("【--cabin--batchid--】: "+batchid+"【--cabin--fileType--】: "+type);
		if (type == null || type.equals("")) {
			type = "Image";// 默认为上传图片
		}
		// 将图片保存到数据库中
		// 将文件转成64位字节码
		try {
			byte[] bytes = bsUtil.readFromInputStreamToBytes(uploader.getUpload().getInputStream());
			String str_filename = uploader.getUpload().getOriginalFilename();//
			if (type.equals("Image")) {
				new FileLoad().uploadImage(str_filename, bytes, batchid, fromtable, pkname, pkvalue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	//通用的下载文件工具方法!,对应客户端的JSPFree.downloadFile("com.yusys.east.checkresult.query.service.ExportWorfDMO","aaa.doc");
	@RequestMapping("/downloadFile")
	@ResponseBody
	public void downloadFile(HttpServletResponse _response, String commFormHiddenValue) throws Exception {
		JSONObject jso_init = JSONObject.parseObject(commFormHiddenValue); //再把参数转换成对象!
		String str_class = jso_init.getString("classname"); //类名
		String str_filename = jso_init.getString("filename"); //文件名
		JSONObject jso_par = jso_init.getJSONObject("par"); //参数

		String str_fileName2 = URLEncoder.encode(str_filename, "UTF-8"); //一定要这样转换一下,否则中文名到了浏览器端下载时会乱码!

		ServletOutputStream servletOut = null; //
		int li_type = AbstractDownloadFile.DownLoadAsString; //默认是
		try {
			AbstractDownloadFile downLoad = (AbstractDownloadFile) Class.forName(str_class).newInstance();
			li_type = downLoad.getDownloadType(); //下载类型

			//根据4种类型判断
			if (li_type == AbstractDownloadFile.DownLoadAsBytes) { //返回byte[]字节!!
				System.out.println("★★下载文件,直接返回byte[]方式..."); //
				byte[] bys = downLoad.getDownLoadContentAsBytes(jso_par);
				_response.setCharacterEncoding("UTF-8");
				_response.setContentType("application/octet-stream;charset=UTF-8");
				_response.setHeader("Content-disposition", "attachment;filename=" + str_fileName2);
				_response.setHeader("Content-Length", "" + bys.length); //文件大小!

				servletOut = _response.getOutputStream(); //输出流
				servletOut.write(bys); //输出文件流!
				servletOut.flush(); //
			} else if (li_type == AbstractDownloadFile.DownLoadAsString) { //返回字符串!!
				System.out.println("★★下载文件,直接返回String方式..."); //
				String str_data = downLoad.getDownLoadContentAsString(jso_par); //取数据
				_response.setCharacterEncoding("UTF-8");
				_response.setContentType("application/octet-stream;charset=UTF-8"); //
				_response.setHeader("Content-disposition", "attachment;filename=" + str_fileName2);
				//_response.setHeader("Content-Length", "" + str_data.length()); //文件大小!如果有中文,这个宽度不对,所以暂时先去掉!!

				servletOut = _response.getOutputStream(); //输出流
				servletOut.write(str_data.getBytes("UTF-8")); //输出文件流!
				servletOut.flush(); //
			} else if (li_type == AbstractDownloadFile.DownLoadAsInputStream) { //如果是输入流
				System.out.println("★★下载文件,采用InputStream输入流方式..."); //

				//输入流对象,有可能发生异常!
				DownLoadInStreamVO downVO = downLoad.getDownLoadContentAsInputStream(jso_par);

				_response.setCharacterEncoding("UTF-8");
				_response.setContentType("application/octet-stream;charset=UTF-8");
				_response.setHeader("Content-disposition", "attachment;filename=" + str_fileName2);

				long ll_fileSize = downVO.getFileLength(); //文件大小
				_response.setHeader("Content-Length", "" + ll_fileSize); //文件大小,这个只有设置准确了浏览器下载进度才精确!

				InputStream ins = downVO.getInputStream(); //实际输入流
				servletOut = _response.getOutputStream(); //输出流

				try {
					//循环输出,即输入流读2048个字节后,输出流立即输出!!
					byte[] tmpbys = new byte[2048]; //
					int pos = -1; //
					while ((pos = ins.read(tmpbys)) != -1) { // 循环读入
						servletOut.write(tmpbys, 0, pos); // 写入
						servletOut.flush(); //立即输出
					}
				} catch (Throwable _ex) {
					_ex.printStackTrace();
				} finally {
					try {
						ins.close(); //关闭输入流
					} catch (Exception _exx) {
						_exx.printStackTrace();
					}
				}
			} else if (li_type == AbstractDownloadFile.WriteServletOutputStream) { //如果是直接操纵输出流..
				System.out.println("★★下载文件,直接操纵ServletOutputStream输出流方式..."); //
				_response.setCharacterEncoding("UTF-8");
				_response.setContentType("application/octet-stream;charset=UTF-8");
				_response.setHeader("Content-disposition", "attachment;filename=" + str_fileName2);
				//_response.setHeader("Content-Length", "" + bys.length); //文件大小!

				servletOut = _response.getOutputStream(); //输出流
				downLoad.downLoaByWriteServletOut(jso_par, servletOut); //输出!
				servletOut.flush(); //
			}
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
			String str_err = "";
			if (li_type == AbstractDownloadFile.WriteServletOutputStream) { //直接输出流只能下载一个error.txt
				//原来返回一个error.txt
				_response.setCharacterEncoding("UTF-8");
				_response.setContentType("application/octet-stream;charset=UTF-8");
				_response.setHeader("Content-disposition", "attachment;filename=error.txt");
				str_err = bsUtil.getExceptionStringBuffer(_ex); //
			} else { //其他下载模式,后来觉得下载发生异常,还是弹出提示框更友好!
				_response.setCharacterEncoding("UTF-8");
				_response.setContentType("text/html"); //
				str_err = getErrorHtml(_ex.getClass() + "<br>" + _ex.getMessage());
			}

			if (servletOut == null) {
				servletOut = _response.getOutputStream(); //输出流
			}

			//System.err.println("输出内容:" + str_err); //
			servletOut.write(str_err.getBytes("UTF-8"));
			servletOut.flush();
		} finally {
			try {
				if (servletOut != null) {
					servletOut.close(); //关闭!
				}
			} catch (Exception _exx) {
				_exx.printStackTrace();
			}
		}
	}

	//返回html
	private String getErrorHtml(String _error) {
		String str_error = bsUtil.replaceAll(_error, "\"", "'"); //
		StringBuilder sb_html = new StringBuilder(); //

		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<meta charset=\"UTF-8\">\r\n");
		sb_html.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + this.contextPath + "/js/jqueryEasyUI/themes/default/easyui.css\">\r\n");
		sb_html.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + this.contextPath + "/js/jqueryEasyUI/themes/icon.css\">\r\n");

		sb_html.append("<script type=\"text/javascript\" src=\"" + this.contextPath + "/js/jqueryEasyUI/jquery.min.js\"></script>\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"" + this.contextPath + "/js/jqueryEasyUI/jquery.easyui.min.js\"></script>\r\n");

		sb_html.append("<script type=\"text/javascript\" src=\"" + this.contextPath + "/frame/js/yuformat/freeutil.js\"></script>\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"" + this.contextPath + "/frame/js/yuformat/jspfree.js\"></script>\r\n");
		sb_html.append("</head>\r\n");

		sb_html.append("<body>\r\n");
		sb_html.append("下载发生异常:" + str_error + "\r\n");
		sb_html.append("<script type=\"text/JavaScript\">\r\n");
		sb_html.append("self.parent[\"JSPFree\"].alert(\"下载发生异常:<br>" + str_error + "\");\r\n");
		sb_html.append("</script>\r\n");
		sb_html.append("</body>\r\n");

		return sb_html.toString();
	}

	/**
	 * 图片下载
	 * 
	 * @param response
	 * @param type
	 * @param batchid
	 * @param fromtable
	 * @param pkname
	 * @param pkvalue
	 */
	@RequestMapping("/downloadimg")
	@ResponseBody
	public void downloadImg(HttpServletResponse response, String type, String batchid, String fromtable, String pkname, String pkvalue) {
		// System.out.println("--downloadImg--type: "+type+"--batchid: "+batchid);
		if (type == null || type.equals("")) {
			type = "Image";// 默认为上传图片
		}
		JSONObject j_img = new JSONObject();//
		if (batchid != null && !batchid.equals("")) {
			j_img.put("batchid", batchid);
		}
		if (fromtable != null && !fromtable.equals("")) {
			j_img.put("fromtable", fromtable);
		}
		if (pkname != null && !pkname.equals("")) {
			j_img.put("pkname", pkname);
		}
		if (pkvalue != null && !pkvalue.equals("")) {
			j_img.put("pkvalue", pkvalue);
		}
		// System.out.println("【type】: "+type);
		try {
			if (type.equals("Image")) {
				JSONObject j_res = new FileLoad().getImageCode(j_img);
				String str_imgCode = j_res.getString("imgCode").toString();
				String str_filename = j_res.getString("filename").toString();
				String str_shortname = str_filename.substring(str_filename.lastIndexOf(".") + 1, str_filename.length());
				// System.out.println("--str_filename--: "+str_filename+"  --str_shortname--: "+str_shortname);
				response.setContentType("image/" + str_shortname);
				byte[] byte_img = EncodeUtils.base64Decode(str_imgCode);
				// System.out.println("将图片的byte[]写入到页面...");
				// response.getOutputStream().write(byte_img);
				InputStream is = new ByteArrayInputStream(byte_img);
				OutputStream os = response.getOutputStream();
				byte[] b = new byte[1024];
				while (is.read(b) != -1) {
					os.write(b);
				}
				is.close();
				os.flush();
				os.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存图片
	 * 
	 * @param response
	 * @param batchid
	 * @throws Exception
	 */
	@RequestMapping("/saveimg")
	@ResponseBody
	public void saveImgToLocal(HttpServletResponse response, String batchid) throws Exception {
		JSONObject j_img = new JSONObject();//
		j_img.put("batchid", batchid);
		JSONObject j_res = new FileLoad().getImageCode(j_img);
		String str_imgCode = j_res.getString("imgCode").toString();
		String str_filename = StringUtils.defaultString(j_res.getString("filename"), "");//
		// System.out.println("--str_filename--: "+str_filename+"--decode--: "+new
		// String(str_filename.getBytes("UTF-8"), "ISO-8859-1"));
		byte[] byte_img = EncodeUtils.base64Decode(str_imgCode);
		// File file_img = new File("C:\\111\\"+str_filename);//
		// FileInputStream input = new FileInputStream(file_img);
		response.setContentType("text/plain;charset=GBK"); // 设置 response 的编码方式
		response.setHeader("Content-disposition", "attachment;filename=" + new String(str_filename.getBytes("UTF-8"), "ISO-8859-1")); // 设定输出文件头
		response.getOutputStream().write(byte_img);
	}
}
