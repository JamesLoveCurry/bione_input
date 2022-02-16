package com.yusys.bione.plugin.rptsys.web;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.mtool.entity.BioneDriverInfo;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.frame.variable.entity.BioneSysVarInfo;
import com.yusys.bione.frame.variable.web.SysVarController;
import com.yusys.bione.plugin.rptsys.entity.RptSysVarInfo;
import com.yusys.bione.plugin.rptsys.service.RptSysVarBS;
import com.yusys.bione.plugin.rptsys.web.vo.RptSysVarInfoVO;
/**
 * 
 * <pre>
 * Title:系统参数管理
 * Description:
 * </pre>
 * 
 * @author weijiaxiang weijx@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("rpt/frame/rptsys/var")
public class RptSysVarController extends BaseController {

	
	final static String SQL_TEST_SYS_SQLNULL = "01";// SQL语句为空
	final static String SQL_TEST_SYS_CLASSNOTFOUNDEXCEPTION = "02";// 数据源/驱动配置异常
	final static String SQL_TEST_SYS_SQLEXCEPTION = "03";// SQL语法错误
	final static String SQL_TEST_SYS_SUCCESS = "04";// 测试成功
	final static String SQL_TEST_SYS_CAHCE_ERROR = "05";// 缓存获取失败
	final static String SQL_TEST_SYS_COLUMN_ERROR = "06";// 未明确列
	final static String SQL_TEST_SYS_ROW_ERROR = "07";// 结果值不唯一

	protected static Logger log = LoggerFactory
			.getLogger(SysVarController.class);

	@Autowired
	private RptSysVarBS sysVarBS;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return new ModelAndView(
				"/plugin/rptsys/sys-var-index");
	}
	
	
	/**
	 * 获取用于加载grid的数据
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager) {
		return this.sysVarBS.getSysVarList(pager,"custom");
	}
	

	/**
	 * 表单验证中的后台验证，验证变量标识是否已存在
	 */
	@RequestMapping("/sysVarNoValid")
	@ResponseBody
	public boolean sysVarNoValid(BioneSysVarInfo model) {
		boolean flag = sysVarBS.checkVarNo(model.getVarNo());
		return flag;
	}

	/**
	 * 批量删除
	 */
	@RequestMapping("/deleteBatch")
	public void deleteBatch(String ids) {
		List<String> idArrs = ArrayUtils.asList(ids, ",");
		sysVarBS.deleteSysVarInfo(idArrs);
	}

	/**
	 * 根据id，加载系统信息变量表信息
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@RequestMapping("/getSysVarInfoById.*")
	@ResponseBody
	public RptSysVarInfoVO getSysVarInfoById(String varId)
			throws IllegalAccessException, InvocationTargetException {
		return sysVarBS.getSysVarInfoByVarId(varId);
	}


	/**
	 * 测试配置的数据源与SQL语句是否正确
	 * 
	 * @param dsId
	 *            数据源ID
	 * @param sql
	 *            测试sql
	 * @param successTip
	 *            测试成功是否前端提示
	 * @return
	 */
	@RequestMapping("/test")
	@ResponseBody
	public String test(String dsId, String testSql, String buttonType) {
		if (testSql == null || ("".equals(testSql.trim()))) {
			return SQL_TEST_SYS_SQLNULL;
		}
		if (dsId == null || ("".equals(dsId.trim()))) {
			return SQL_TEST_SYS_CLASSNOTFOUNDEXCEPTION;
		}

		BioneDsInfo ds = this.sysVarBS.findDataSourceById(dsId);
		// 测试[数据源/自定义SQL语句]是否正常
		String driverName = null;
		if (ds != null && ds.getDriverId() != null
				&& (!"".equals(ds.getDriverId()))) {
			BioneDriverInfo driver = sysVarBS.findDriverInfoById(ds.getDriverId());
			if (driver != null)
				driverName = driver.getDriverName();
			else {
				return SQL_TEST_SYS_CLASSNOTFOUNDEXCEPTION;
			}
		} else {
			return SQL_TEST_SYS_CLASSNOTFOUNDEXCEPTION;
		}

		String url = ds.getConnUrl();
		String usrId = ds.getConnUser();
		String passwd = ds.getConnPwd();

		Connection conn = null;
		if (driverName != null && !"".equals(driverName) && url != null
				&& !"".equals(url) && usrId != null && !"".equals(usrId)) {
			ResultSet rs = null;
			try {
				// 注册驱动
				Class.forName(driverName);
				// 获取连接
				conn = DriverManager.getConnection(url, usrId, passwd);

				Statement st = conn.createStatement();
				rs = st.executeQuery(testSql);
				// 连接成功,取得结果
				if (rs.getMetaData().getColumnCount() == 1) {
					return SQL_TEST_SYS_SUCCESS;
				} else {
					return SQL_TEST_SYS_COLUMN_ERROR;
				}
			} catch (ClassNotFoundException e) {// 数据源/驱动配置错误
				return SQL_TEST_SYS_CLASSNOTFOUNDEXCEPTION;

			} catch (SQLException e) {// SQL语法错误
				return SQL_TEST_SYS_SQLEXCEPTION;

			} finally {
				if (conn != null) {
					try {
						conn.rollback();// 回滚
					} catch (SQLException e) {
						logger.debug("测试自定义SQL回滚异常");
					}
					// 释放连接
					JdbcUtils.closeConnection(conn);
				}
			}
		} else {
			return SQL_TEST_SYS_CLASSNOTFOUNDEXCEPTION;
		}
	}

	/**
	 * 用于保存添加或修改时的对象
	 */

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Map<String,String> create(RptSysVarInfoVO model) {
		Map<String,String> data=new HashMap<String,String>();
		String testFlag = null;
		if(model != null){
			String buttonType = model.getButtonType();
			String dsId = model.getSourceId();
			String varValueSql = model.getVarValueSql();
			RptSysVarInfo sysVar = new RptSysVarInfo();
			BeanUtils.copyProperties(model, sysVar);
			if ("01".equals(model.getVarType())) {// 变量类型，直接保存
				model.setSourceId("");
				sysVarBS.saveSysVarInfo(model);
			} else {// sql语句先测试通过再保存
				testFlag = test(dsId, varValueSql, buttonType);
				if ("04".equals(testFlag) && "save".equals(buttonType)) {
					model.setVarVal(varValueSql);
					sysVar.setVarVal(varValueSql);
					sysVarBS.saveSysVarInfo(sysVar);
				}
			}
		}
		data.put("testFlag", testFlag);
		return data;
	}
//
	/**
	 * 根据ID，加载数据
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public RptSysVarInfo show(@PathVariable("id") String id) {
		RptSysVarInfo model = this.sysVarBS.getSysVarInfoById(id);
		return model;
	}

	/**
	 * 执行修改前的数据加载
	 * 
	 * @return String 用于匹配结果页面
	 */
	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/plugin/rptsys/sys-var-edit", "id", id);
	}

	/**
	 * 执行添加前页面跳转
	 * 
	 * @return String 用于匹配结果页面
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String editNew() {
		return "/plugin/rptsys/sys-var-edit";
	}
}
