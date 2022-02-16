/**
 * 
 */
package com.yusys.bione.frame.base.web;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import com.yuchenglicense.LicenseVerify;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.frame.base.common.AfterServerStartHolder;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.common.LogicSysInfoHolder;
import com.yusys.bione.frame.base.common.MenuInfoHolder;
import com.yusys.bione.frame.base.common.ParamInfoHolder;
import com.yusys.bione.frame.base.common.ResOperInfoHolder;

/**
 * <pre>
 * Title: 系统初始化Listener
 * Description: 完成系统相关初始化工作,比如字典数据加载，全局变量设置等
 * </pre>
 * 
 * @author mengzx
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:1.01     修改人：songxf  修改日期: 2012-07-05    修改内容:增加逻辑系统信息初始化
 * </pre>
 */
public class AppInitListener implements ServletContextListener {

	private static Logger logger = LoggerFactory.getLogger(AppInitListener.class);
	
	private static ResourceLoader resourceLoader = new DefaultResourceLoader();
	
	//产品编号，对应产品“宇信科技统一监管报送平台系统V3.0”
	private static String PRODUCT_CODE = "0000001242";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet
	 * .ServletContextEvent)
	 */

	public void contextInitialized(ServletContextEvent sce) {
		validateLicense();
		try {
			System.setProperty("BIONE-StartTime", "" + System.currentTimeMillis()); //

			logger.info("系统初始化开始.......");

			PropertiesUtils dbProp = PropertiesUtils.get("/database.properties");
			String str_jdbc_url = dbProp.getProperty("jdbc.url"); //url
			String str_jdbc_username = dbProp.getProperty("jdbc.username"); //用户名
			logger.info("默认数据源是【" + str_jdbc_url + "】【" + str_jdbc_username + "】");

			/* 系统绝对物理路径 */
			GlobalConstants4frame.APP_REAL_PATH = sce.getServletContext().getRealPath("/");

			/* 系统应用上下文 */

			//修改为servlet2.5，替换到原有下方方法
			//String scn = sce.getServletContext().getServletContextName();
			//GlobalConstants4frame.APP_CONTEXT_PATH =scn.replace(".war", "");

			GlobalConstants4frame.APP_CONTEXT_PATH = sce.getServletContext().getContextPath();

			// 初始化所有菜单URL信息
			MenuInfoHolder.refreshMenuUrlInfo();

			// 初始化操作权限信息
			ResOperInfoHolder.refreshResOperInfo();

			// 初始化逻辑系统信息
			LogicSysInfoHolder.refreshLogicSysInfo();

			// 初始化参数信息
			ParamInfoHolder.refreshParamInfo();

			// 初始化系统启动后启动自定义的类
			AfterServerStartHolder.refreshJobInfo();
			logger.info("系统初始化结束.");
		} catch (Exception e) {

			logger.error("系统初始化失败.", e);
		}

	}

	@SuppressWarnings({ "unchecked" })
	private Boolean validateLicense() {
		boolean flag = false;
		PropertiesUtils propertiesUtils = PropertiesUtils.get("bione-frame/index/index.properties");
		String filePath = propertiesUtils.getProperty("licenseFile");
		// 需要将License文件放置到合适的路径下，License文件的命名为“10位序号-1位License类型编号-10位产品编号.lic”
		LicenseVerify licenseVerify;
		try {
			licenseVerify = new LicenseVerify(resourceLoader.getResource("bione-frame/license/" + filePath).getFile().getPath());
			// 通过比对License文件校验码和碓冰当前系统时间，判断License文件是否有效
			logger.info("license验证通过,有效期" + licenseVerify.getLicenseStartTime() + "至" + licenseVerify.getLicenseEndTime());
			//验证产品编号
			if (! PRODUCT_CODE.equals(licenseVerify.getProductCode())) {
				logger.error("系统初始化失败.,license验证异常：" + "请使用‘宇信科技统一监管报送平台系统V3.0’产品对应的license");
				throw new RuntimeException("license异常无法启动");
			}
			if (!licenseVerify.licenseValid()) {
				logger.error("系统初始化失败.,license验证异常：" + licenseVerify.getErrorMessage());
				throw new RuntimeException("license异常无法启动");
			}
			// 获取扩展信息，用来控制软件产品的使用范围
			HashMap<String, String> expendInfo = licenseVerify.getExpendInfo();
			String userMaxNumber = expendInfo.get("用户数");
			logger.info("最大用户数：" + userMaxNumber);
		} catch (IOException e) {
			throw new RuntimeException("license文件不存在");
		}

		return flag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */

	public void contextDestroyed(ServletContextEvent sce) {
		MenuInfoHolder.clearMenuUrlInfo();
		ResOperInfoHolder.clearResOperInfo();
	}
}
